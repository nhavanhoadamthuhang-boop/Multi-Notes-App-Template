package com.example.utils

import com.example.data.backup.BackupComment
import com.example.data.backup.BackupNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.util.concurrent.TimeUnit

data class UrlParsedNoteData(
    val note: BackupNote,
    val sourceUrl: String,
    val pageTitle: String,
    val estimatedReadTimeMinutes: Int,
    val commentsCount: Int
)

object UrlImportHelper {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    suspend fun importFromUrl(url: String): Result<UrlParsedNoteData> = withContext(Dispatchers.IO) {
        try {
            val validUrl = normalizeUrl(url)
            if (validUrl.isBlank() || (!validUrl.startsWith("http://") && !validUrl.startsWith("https://"))) {
                return@withContext Result.failure(IllegalArgumentException("Địa chỉ URL không hợp lệ. Vui lòng nhập link bắt đầu bằng http:// hoặc https://"))
            }

            val request = Request.Builder()
                .url(validUrl)
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile; rv:128.0) Gecko/128.0 Firefox/128.0")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,text/plain")
                .header("Accept-Language", "vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7")
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext Result.failure(Exception("Không thể truy cập URL (Mã phản hồi: ${response.code} ${response.message})"))
                }

                val responseBody = response.body?.string() ?: ""
                if (responseBody.isBlank()) {
                    return@withContext Result.failure(Exception("Nội dung từ URL trống."))
                }

                val contentType = response.header("Content-Type") ?: "text/html"

                val parsedData = if (contentType.contains("text/plain") || (!responseBody.contains("<html") && !responseBody.contains("<body") && !responseBody.contains("<!DOCTYPE"))) {
                    // Plain text format
                    parsePlainText(responseBody, validUrl)
                } else {
                    // HTML Document
                    parseHtmlDocument(responseBody, validUrl)
                }

                Result.success(parsedData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(Exception("Lỗi khi tải nội dung từ URL: ${e.localizedMessage ?: e.message}"))
        }
    }

    private fun normalizeUrl(rawUrl: String): String {
        val trimmed = rawUrl.trim()
        return if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
            "https://$trimmed"
        } else {
            trimmed
        }
    }

    private fun parseHtmlDocument(html: String, sourceUrl: String): UrlParsedNoteData {
        val doc: Document = Jsoup.parse(html, sourceUrl)

        // 1. Determine site type
        val isAnotepad = sourceUrl.contains("anotepad.com", ignoreCase = true)

        var title = ""
        var content = ""
        val comments = mutableListOf<BackupComment>()
        var category = "Web Import"

        if (isAnotepad) {
            category = "aNotepad"
            // anotepad specific selectors
            val anotepadTitleElem = doc.selectFirst(".readOnlyTitle, .note_title, #edit_title, h1.title, h1")
            title = anotepadTitleElem?.text()?.trim() ?: ""

            val anotepadContentElem = doc.selectFirst(".readOnlyContent, .rich-text-content, .note_content, #edit_textarea, .content")
            if (anotepadContentElem != null) {
                content = cleanHtmlContent(anotepadContentElem)
            }
        }

        // Fallback for Title
        if (title.isBlank()) {
            title = doc.selectFirst("meta[property=og:title]")?.attr("content")?.trim()
                ?: doc.selectFirst("meta[name=twitter:title]")?.attr("content")?.trim()
                ?: doc.title().trim()
        }

        // Clean website title noise (e.g. "Note Title - aNotepad", "My Note | Medium")
        title = title.removeSuffix("- aNotepad")
            .removeSuffix("- aNotepad.com")
            .removeSuffix("| aNotepad")
            .trim()

        if (title.isBlank()) {
            title = "Ghi chú từ URL"
        }

        // Fallback for Content
        if (content.isBlank()) {
            // Remove noise elements
            doc.select("script, style, nav, header, footer, iframe, noscript, svg, .ad, .ads, .comment-form, .sidebar").remove()

            val articleElem = doc.selectFirst("article, [role=main], .main-content, .post-content, .entry-content, .article-content, #content, main")
            if (articleElem != null) {
                content = cleanHtmlContent(articleElem)
            } else {
                val body = doc.body()
                if (body != null) {
                    content = cleanHtmlContent(body)
                }
            }
        }

        // Check for any comments on the page (e.g. Disqus, comment sections, .comments, .comment-list)
        val commentElements = doc.select(".comment, .comment-item, .comment_item, li.comment")
        for ((idx, cmtElem) in commentElements.take(50).withIndex()) {
            val author = cmtElem.selectFirst(".author, .comment-author, .user-name, .commenter")?.text()?.trim() ?: "Khách"
            val text = cmtElem.selectFirst(".comment-body, .comment-content, .comment_text, p")?.text()?.trim() ?: ""
            if (text.isNotBlank()) {
                comments.add(
                    BackupComment(
                        id = (idx + 1).toLong(),
                        parentId = null,
                        authorName = author,
                        content = text,
                        createdAt = System.currentTimeMillis()
                    )
                )
            }
        }

        val domain = try {
            java.net.URI(sourceUrl).host?.removePrefix("www.") ?: "Web"
        } catch (_: Exception) {
            "Web"
        }

        val tags = if (isAnotepad) "anotepad, web" else "$domain, web"
        val wordCount = content.split("\\s+".toRegex()).count { it.isNotBlank() }
        val readTime = (wordCount / 200).coerceAtLeast(1)

        val note = BackupNote(
            title = title,
            description = content,
            category = category,
            tags = tags,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            comments = comments
        )

        return UrlParsedNoteData(
            note = note,
            sourceUrl = sourceUrl,
            pageTitle = doc.title().ifBlank { title },
            estimatedReadTimeMinutes = readTime,
            commentsCount = comments.size
        )
    }

    private fun cleanHtmlContent(element: Element): String {
        // Convert block elements to clean line breaks
        element.select("br").append("\\n")
        element.select("p, div, h1, h2, h3, h4, h5, h6, li, tr").prepend("\\n")

        val rawText = element.text().replace("\\n", "\n")
        return rawText.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString("\n\n")
    }

    private fun parsePlainText(text: String, sourceUrl: String): UrlParsedNoteData {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        val title = if (lines.isNotEmpty()) lines[0] else "Ghi chú từ $sourceUrl"
        val content = if (lines.size > 1) lines.drop(1).joinToString("\n\n") else text

        val wordCount = text.split("\\s+".toRegex()).count { it.isNotBlank() }
        val readTime = (wordCount / 200).coerceAtLeast(1)

        val note = BackupNote(
            title = title,
            description = content,
            category = "Plain Text",
            tags = "url, text",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        return UrlParsedNoteData(
            note = note,
            sourceUrl = sourceUrl,
            pageTitle = title,
            estimatedReadTimeMinutes = readTime,
            commentsCount = 0
        )
    }
}
