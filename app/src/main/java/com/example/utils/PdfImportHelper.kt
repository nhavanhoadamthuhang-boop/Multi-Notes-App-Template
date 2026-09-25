package com.example.utils

import android.content.Context
import android.net.Uri
import com.example.data.backup.BackupComment
import com.example.data.backup.BackupNote
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.regex.Pattern

data class PdfParsedData(
    val note: BackupNote,
    val totalPages: Int,
    val rawText: String,
    val commentCount: Int,
    val replyCount: Int
)

object PdfImportHelper {

    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            try {
                PDFBoxResourceLoader.init(context.applicationContext)
                isInitialized = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun parsePdfUri(context: Context, uri: Uri): Result<PdfParsedData> = withContext(Dispatchers.IO) {
        init(context)
        try {
            val inputStream: InputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Không thể mở tệp PDF đã chọn."))

            inputStream.use { stream ->
                PDDocument.load(stream).use { document ->
                    val pageCount = document.numberOfPages
                    val stripper = PDFTextStripper()
                    stripper.sortByPosition = true
                    val fullText = stripper.getText(document) ?: ""

                    if (fullText.isBlank()) {
                        return@withContext Result.failure(Exception("Tệp PDF trống hoặc không chứa văn bản có thể đọc."))
                    }

                    val parsedData = parseExtractedPdfText(fullText, pageCount)
                    Result.success(parsedData)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Lỗi khi đọc tệp PDF: ${e.localizedMessage ?: e.message}"))
        }
    }

    fun parseExtractedPdfText(rawText: String, pageCount: Int = 1): PdfParsedData {
        val lines = rawText.lines().map { it.trimEnd() }.filter { it.isNotBlank() }
        if (lines.isEmpty()) {
            val fallbackNote = BackupNote(
                title = "Ghi chú từ PDF",
                description = "",
                category = "PDF Import",
                createdAt = System.currentTimeMillis()
            )
            return PdfParsedData(fallbackNote, pageCount, rawText, 0, 0)
        }

        var title = ""
        var category = "PDF Import"
        var tags = ""
        var createdAt = System.currentTimeMillis()
        val descriptionLines = mutableListOf<String>()
        val commentsList = mutableListOf<BackupComment>()

        var inCommentsSection = false
        var lineIndex = 0

        // 1. Check title (usually line 0)
        if (lines.isNotEmpty()) {
            title = lines[0].trim()
            lineIndex = 1
        }

        // 2. Check metadata line (e.g. Created: MMM dd, yyyy HH:mm | Category: ... | Tags: ...)
        if (lineIndex < lines.size) {
            val potentialMeta = lines[lineIndex].trim()
            if (potentialMeta.startsWith("Created:", ignoreCase = true) ||
                potentialMeta.contains("Category:", ignoreCase = true) ||
                potentialMeta.contains("Tags:", ignoreCase = true) ||
                potentialMeta.startsWith("Ngày tạo:", ignoreCase = true)
            ) {
                // Parse metadata
                val parts = potentialMeta.split("|").map { it.trim() }
                for (part in parts) {
                    if (part.startsWith("Created:", ignoreCase = true) || part.startsWith("Ngày tạo:", ignoreCase = true)) {
                        val dateString = part.substringAfter(":").trim()
                        tryParseDate(dateString)?.let { createdAt = it }
                    } else if (part.startsWith("Category:", ignoreCase = true) || part.startsWith("Danh mục:", ignoreCase = true)) {
                        category = part.substringAfter(":").trim()
                    } else if (part.startsWith("Tags:", ignoreCase = true) || part.startsWith("Thẻ:", ignoreCase = true)) {
                        tags = part.substringAfter(":").trim()
                    }
                }
                lineIndex++
            }
        }

        // 3. Scan rest of text for Description vs Comments / Replies
        val commentAuthorRegex = Pattern.compile("^(.+?)\\s*[-–—]\\s*([A-Za-z0-9, :\\/\\-]+)$")
        var currentParentCommentId: Long? = null
        var commentIdCounter = 1L
        var currentAuthor = ""
        var currentReplyTo: String? = null
        var currentCommentDate = System.currentTimeMillis()
        val currentCommentBody = mutableListOf<String>()
        var isCurrentReply = false

        fun flushCurrentComment() {
            if (currentAuthor.isNotBlank() || currentCommentBody.isNotEmpty()) {
                val bodyText = currentCommentBody.joinToString("\n").trim()
                val author = currentAuthor.ifBlank { "Đàm Tường Quân" }
                val newId = commentIdCounter++
                
                if (isCurrentReply && currentParentCommentId != null) {
                    commentsList.add(
                        BackupComment(
                            id = newId,
                            parentId = currentParentCommentId,
                            authorName = author,
                            replyToAuthor = currentReplyTo,
                            content = bodyText,
                            createdAt = currentCommentDate
                        )
                    )
                } else {
                    currentParentCommentId = newId
                    commentsList.add(
                        BackupComment(
                            id = newId,
                            parentId = null,
                            authorName = author,
                            replyToAuthor = null,
                            content = bodyText,
                            createdAt = currentCommentDate
                        )
                    )
                }
                currentCommentBody.clear()
            }
        }

        while (lineIndex < lines.size) {
            val line = lines[lineIndex]
            val trimmed = line.trim()

            // Check if this line marks the Comments header
            if (trimmed.equals("Comments", ignoreCase = true) ||
                trimmed.equals("Bình luận", ignoreCase = true) ||
                trimmed.equals("Danh sách bình luận", ignoreCase = true) ||
                trimmed.equals("Bình luận & Phản hồi", ignoreCase = true) ||
                trimmed.equals("Comments & Replies", ignoreCase = true)
            ) {
                inCommentsSection = true
                lineIndex++
                continue
            }

            if (!inCommentsSection) {
                // Also check if line looks like a comment header even if no "Comments" heading was explicitly found
                val matcher = commentAuthorRegex.matcher(trimmed)
                val isLikelyDate = tryParseDate(if (matcher.find()) matcher.group(2) else "") != null
                if (isLikelyDate && (trimmed.contains("PM") || trimmed.contains("AM") || trimmed.contains("202") || trimmed.contains(":"))) {
                    // Transition to comments
                    inCommentsSection = true
                    continue
                } else {
                    descriptionLines.add(line)
                }
            } else {
                // In comments section:
                // Check if line indicates a comment or reply author line
                // Look for indentation or reply indicators
                val isIndented = line.startsWith("    ") || line.startsWith("\t") || line.startsWith("  ") || trimmed.startsWith("↳") || trimmed.startsWith("->") || trimmed.startsWith("Phản hồi:") || trimmed.startsWith("Reply:")
                val cleanLine = trimmed.removePrefix("↳").removePrefix("->").removePrefix("Phản hồi:").removePrefix("Reply:").trim()

                val matcher = commentAuthorRegex.matcher(cleanLine)
                if (matcher.matches()) {
                    val potentialAuthor = matcher.group(1)?.trim() ?: ""
                    val potentialDate = matcher.group(2)?.trim() ?: ""
                    val parsedDate = tryParseDate(potentialDate)

                    if (parsedDate != null || potentialAuthor.isNotEmpty() && potentialAuthor.length < 50) {
                        // Flush previous comment
                        flushCurrentComment()

                        currentAuthor = potentialAuthor
                        currentCommentDate = parsedDate ?: System.currentTimeMillis()
                        isCurrentReply = isIndented
                        if (isCurrentReply && commentsList.isNotEmpty()) {
                            // Find parent root comment author
                            val parent = commentsList.find { it.id == currentParentCommentId }
                            currentReplyTo = parent?.authorName
                        } else {
                            currentReplyTo = null
                        }
                        lineIndex++
                        continue
                    }
                }

                // If not an author header line, it's comment body text
                currentCommentBody.add(trimmed)
            }

            lineIndex++
        }

        // Flush any remaining comment
        flushCurrentComment()

        val finalDescription = descriptionLines.joinToString("\n").trim()
        val finalTitle = title.ifBlank { "Ghi chú từ PDF" }

        val rootCommentsCount = commentsList.count { it.parentId == null }
        val repliesCount = commentsList.count { it.parentId != null }

        val note = BackupNote(
            title = finalTitle,
            description = finalDescription,
            category = category,
            tags = tags,
            createdAt = createdAt,
            updatedAt = System.currentTimeMillis(),
            comments = commentsList
        )

        return PdfParsedData(
            note = note,
            totalPages = pageCount,
            rawText = rawText,
            commentCount = rootCommentsCount,
            replyCount = repliesCount
        )
    }

    private fun tryParseDate(dateStr: String): Long? {
        if (dateStr.isBlank()) return null
        val formats = listOf(
            "MMM dd, yyyy HH:mm",
            "MMM dd, yyyy hh:mm a",
            "dd/MM/yyyy HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "dd-MM-yyyy HH:mm",
            "MMM d, yyyy HH:mm"
        )
        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.getDefault())
                val date = sdf.parse(dateStr.trim())
                if (date != null) return date.time
            } catch (_: Exception) {
            }
            try {
                val sdf = SimpleDateFormat(format, Locale.US)
                val date = sdf.parse(dateStr.trim())
                if (date != null) return date.time
            } catch (_: Exception) {
            }
        }
        return null
    }
}
