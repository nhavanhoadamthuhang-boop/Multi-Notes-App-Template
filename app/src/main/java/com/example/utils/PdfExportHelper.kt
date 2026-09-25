package com.example.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.data.local.NoteEntity
import com.example.ui.CommentWithReplies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExportHelper {

    suspend fun exportNoteToPdf(
        context: Context,
        uri: Uri,
        note: NoteEntity,
        comments: List<CommentWithReplies>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            
            val pageRef = arrayOf(pdfDocument.startPage(pageInfo))
            val canvasRef = arrayOf(pageRef[0].canvas)
            val currentYRef = floatArrayOf(50f)
            val margin = 50f
            val maxTextWidth = pageInfo.pageWidth - 2 * margin

            val titlePaint = TextPaint().apply {
                textSize = 24f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = android.graphics.Color.BLACK
            }
            
            val contentPaint = TextPaint().apply {
                textSize = 14f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                color = android.graphics.Color.DKGRAY
            }
            
            val metaPaint = TextPaint().apply {
                textSize = 12f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                color = android.graphics.Color.GRAY
            }
            
            val boldMetaPaint = TextPaint().apply {
                textSize = 12f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = android.graphics.Color.BLACK
            }

            // Draw Note Title
            val titleLayout = StaticLayout.Builder.obtain(note.title, 0, note.title.length, titlePaint, maxTextWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
                
            drawLayoutAcrossPages(titleLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
            currentYRef[0] += 10f

            // Draw Meta Info
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            val dateStr = sdf.format(Date(note.createdAt))
            val relativeTime = getRelativeTimeSpanString(note.createdAt)
            val metaText = "Created: $dateStr ($relativeTime)" + (if (note.category.isNotBlank()) " | Category: ${note.category}" else "") + (if (note.tagList.isNotEmpty()) " | Tags: ${note.tagList.joinToString(", ")}" else "")
            
            val metaLayout = StaticLayout.Builder.obtain(metaText, 0, metaText.length, metaPaint, maxTextWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
            
            drawLayoutAcrossPages(metaLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
            currentYRef[0] += 20f

            // Draw Note Content
            val contentLayout = StaticLayout.Builder.obtain(note.description, 0, note.description.length, contentPaint, maxTextWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1.2f)
                .setIncludePad(false)
                .build()
            
            drawLayoutAcrossPages(contentLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
            currentYRef[0] += 30f

            // Draw Comments Section
            if (comments.isNotEmpty()) {
                val commentsTitleLayout = StaticLayout.Builder.obtain("Comments", 0, "Comments".length, titlePaint.apply { textSize = 20f }, maxTextWidth.toInt())
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1f)
                    .setIncludePad(false)
                    .build()
                
                drawLayoutAcrossPages(commentsTitleLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
                currentYRef[0] += 15f
                
                for (thread in comments) {
                    val cmt = thread.comment
                    val cmtRelative = getRelativeTimeSpanString(cmt.createdAt)
                    val cmtMeta = "${cmt.authorName} - ${sdf.format(Date(cmt.createdAt))} ($cmtRelative)"
                    val cmtText = cmt.content
                    
                    val cmtMetaLayout = StaticLayout.Builder.obtain(cmtMeta, 0, cmtMeta.length, boldMetaPaint, maxTextWidth.toInt())
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0f, 1f)
                        .setIncludePad(false)
                        .build()
                        
                    drawLayoutAcrossPages(cmtMetaLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
                    currentYRef[0] += 5f
                    
                    val cmtTextLayout = StaticLayout.Builder.obtain(cmtText, 0, cmtText.length, contentPaint, maxTextWidth.toInt())
                        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                        .setLineSpacing(0f, 1.2f)
                        .setIncludePad(false)
                        .build()
                        
                    drawLayoutAcrossPages(cmtTextLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
                    currentYRef[0] += 15f
                    
                    // Replies
                    for (reply in thread.replies) {
                        val replyIndent = 20f
                        val replyMaxWidth = maxTextWidth - replyIndent
                        val repRelative = getRelativeTimeSpanString(reply.createdAt)
                        val repMeta = "${reply.authorName} - ${sdf.format(Date(reply.createdAt))} ($repRelative)"
                        val repText = reply.content
                        
                        val repMetaLayout = StaticLayout.Builder.obtain(repMeta, 0, repMeta.length, boldMetaPaint, replyMaxWidth.toInt())
                            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                            .setLineSpacing(0f, 1f)
                            .setIncludePad(false)
                            .build()
                            
                        drawLayoutAcrossPages(repMetaLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin, offsetX = replyIndent)
                        currentYRef[0] += 5f
                        
                        val repTextLayout = StaticLayout.Builder.obtain(repText, 0, repText.length, contentPaint, replyMaxWidth.toInt())
                            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                            .setLineSpacing(0f, 1.2f)
                            .setIncludePad(false)
                            .build()
                            
                        drawLayoutAcrossPages(repTextLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin, offsetX = replyIndent)
                        currentYRef[0] += 15f
                    }
                }
            }

            pdfDocument.finishPage(pageRef[0])

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun drawLayoutAcrossPages(
        layout: StaticLayout,
        pdfDocument: PdfDocument,
        pageInfo: PdfDocument.PageInfo,
        canvasRef: Array<Canvas>,
        pageRef: Array<PdfDocument.Page>,
        currentYRef: FloatArray,
        margin: Float,
        offsetX: Float = 0f
    ) {
        val lineCount = layout.lineCount
        for (i in 0 until lineCount) {
            val lineBottom = layout.getLineBottom(i)
            val lineTop = layout.getLineTop(i)
            val lineHeight = (lineBottom - lineTop).toFloat()
            
            if (currentYRef[0] + lineHeight > pageInfo.pageHeight - margin) {
                pdfDocument.finishPage(pageRef[0])
                pageRef[0] = pdfDocument.startPage(pageInfo)
                canvasRef[0] = pageRef[0].canvas
                currentYRef[0] = margin
            }
            
            canvasRef[0].save()
            // Translate to the required X and Y, minus the line's top so it draws at exactly currentY
            canvasRef[0].translate(margin + offsetX, currentYRef[0] - lineTop)
            
            // Clip to draw only the current line
            canvasRef[0].clipRect(
                -margin - offsetX,
                lineTop.toFloat(),
                pageInfo.pageWidth.toFloat(),
                lineBottom.toFloat()
            )
            layout.draw(canvasRef[0])
            
            canvasRef[0].restore()
            
            currentYRef[0] += lineHeight
        }
    }

    fun getRelativeTimeSpanString(createdAt: Long): String {
        val diff = System.currentTimeMillis() - createdAt
        if (diff <= 0) {
            return "0 năm 0 tháng 0 tuần 0 ngày 0 giờ 0 phút 0 giây trước"
        }
        
        var remaining = diff
        
        val msPerYear = 365L * 24 * 60 * 60 * 1000
        val years = remaining / msPerYear
        remaining %= msPerYear
        
        val msPerMonth = 30L * 24 * 60 * 60 * 1000
        val months = remaining / msPerMonth
        remaining %= msPerMonth
        
        val msPerWeek = 7L * 24 * 60 * 60 * 1000
        val weeks = remaining / msPerWeek
        remaining %= msPerWeek
        
        val msPerDay = 24L * 60 * 60 * 1000
        val days = remaining / msPerDay
        remaining %= msPerDay
        
        val msPerHour = 60L * 60 * 1000
        val hours = remaining / msPerHour
        remaining %= msPerHour
        
        val msPerMinute = 60L * 1000
        val minutes = remaining / msPerMinute
        remaining %= msPerMinute
        
        val msPerSecond = 1000L
        val seconds = remaining / msPerSecond
        
        return "${years} năm ${months} tháng ${weeks} tuần ${days} ngày ${hours} giờ ${minutes} phút ${seconds} giây trước"
    }

    suspend fun exportFavoritesToPdf(
        context: Context,
        uri: Uri,
        notes: List<NoteEntity>
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            
            val pageRef = arrayOf(pdfDocument.startPage(pageInfo))
            val canvasRef = arrayOf(pageRef[0].canvas)
            val currentYRef = floatArrayOf(50f)
            val margin = 50f
            val maxTextWidth = pageInfo.pageWidth - 2 * margin

            val headerPaint = TextPaint().apply {
                textSize = 20f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = android.graphics.Color.BLACK
            }
            
            val titlePaint = TextPaint().apply {
                textSize = 15f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = android.graphics.Color.BLACK
            }
            
            val contentPaint = TextPaint().apply {
                textSize = 12f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                color = android.graphics.Color.DKGRAY
            }
            
            val metaPaint = TextPaint().apply {
                textSize = 10f
                isAntiAlias = true
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                color = android.graphics.Color.GRAY
            }
            
            val dividerPaint = Paint().apply {
                color = android.graphics.Color.LTGRAY
                strokeWidth = 1f
                style = Paint.Style.STROKE
            }

            // Draw Header
            val headerText = "DANH SÁCH GHI CHÚ ĐƯỢC YÊU THÍCH"
            val headerLayout = StaticLayout.Builder.obtain(headerText, 0, headerText.length, headerPaint, maxTextWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
            drawLayoutAcrossPages(headerLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
            currentYRef[0] += 10f

            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val subHeaderText = "Xuất ngày: ${sdf.format(Date())} | Tổng cộng: ${notes.size} ghi chú"
            val subHeaderLayout = StaticLayout.Builder.obtain(subHeaderText, 0, subHeaderText.length, metaPaint, maxTextWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0f, 1f)
                .setIncludePad(false)
                .build()
            drawLayoutAcrossPages(subHeaderLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
            currentYRef[0] += 15f

            // Draw divider
            if (currentYRef[0] + 5f > pageInfo.pageHeight - margin) {
                pdfDocument.finishPage(pageRef[0])
                pageRef[0] = pdfDocument.startPage(pageInfo)
                canvasRef[0] = pageRef[0].canvas
                currentYRef[0] = margin
            }
            canvasRef[0].drawLine(margin, currentYRef[0], pageInfo.pageWidth - margin, currentYRef[0], dividerPaint)
            currentYRef[0] += 20f

            for ((index, note) in notes.withIndex()) {
                // Note Title
                val noteTitleText = "${index + 1}. ${note.title}"
                val titleLayout = StaticLayout.Builder.obtain(noteTitleText, 0, noteTitleText.length, titlePaint, maxTextWidth.toInt())
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1f)
                    .setIncludePad(false)
                    .build()
                drawLayoutAcrossPages(titleLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
                currentYRef[0] += 5f

                // Note Meta (with relative time!)
                val relativeTimeStr = getRelativeTimeSpanString(note.createdAt)
                val noteDateStr = sdf.format(Date(note.createdAt))
                val noteMetaText = "Đã tạo: $noteDateStr ($relativeTimeStr)" + 
                        (if (note.category.isNotBlank()) " | Danh mục: ${note.category}" else "") + 
                        (if (note.tagList.isNotEmpty()) " | Nhãn: ${note.tagList.joinToString(", ")}" else "")
                val metaLayout = StaticLayout.Builder.obtain(noteMetaText, 0, noteMetaText.length, metaPaint, maxTextWidth.toInt())
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1f)
                    .setIncludePad(false)
                    .build()
                drawLayoutAcrossPages(metaLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
                currentYRef[0] += 10f

                // Note Content
                val contentLayout = StaticLayout.Builder.obtain(note.description, 0, note.description.length, contentPaint, maxTextWidth.toInt())
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1.2f)
                    .setIncludePad(false)
                    .build()
                drawLayoutAcrossPages(contentLayout, pdfDocument, pageInfo, canvasRef, pageRef, currentYRef, margin)
                currentYRef[0] += 25f

                // Small separator between notes
                if (index < notes.size - 1) {
                    if (currentYRef[0] + 5f > pageInfo.pageHeight - margin) {
                        pdfDocument.finishPage(pageRef[0])
                        pageRef[0] = pdfDocument.startPage(pageInfo)
                        canvasRef[0] = pageRef[0].canvas
                        currentYRef[0] = margin
                    } else {
                        val dashDividerPaint = Paint().apply {
                            color = android.graphics.Color.parseColor("#E0E0E0")
                            strokeWidth = 1f
                            style = Paint.Style.STROKE
                            pathEffect = android.graphics.DashPathEffect(floatArrayOf(5f, 5f), 0f)
                        }
                        canvasRef[0].drawLine(margin, currentYRef[0], pageInfo.pageWidth - margin, currentYRef[0], dashDividerPaint)
                        currentYRef[0] += 20f
                    }
                }
            }

            pdfDocument.finishPage(pageRef[0])

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
