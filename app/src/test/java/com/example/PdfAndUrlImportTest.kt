package com.example

import com.example.utils.PdfImportHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfAndUrlImportTest {

    @Test
    fun testParsePdfStructuredNoteWithCommentsAndReplies() {
        val samplePdfText = """
            Kế hoạch phát triển ứng dụng 2026
            Created: 15/09/2026 10:30 | Category: Dự án | Tags: #android, #jetpack
            Đây là nội dung chính của ghi chú kế hoạch phát triển.
            Bao gồm kiến trúc Clean Architecture, Room Database và Compose UI.
            
            Comments & Replies
            Đàm Tường Quân - 15/09/2026 11:00
            Giao diện Material 3 nhìn rất đẹp và hiện đại.
            
                ↳ Đàm Tường Quân - 15/09/2026 11:15
                Cảm ơn bạn, chúng tôi đang tối ưu thêm tính năng nhập xuất PDF.
                
            Lương Thuỷ Tiên - 15/09/2026 12:00
            Hỗ trợ nhập từ liên kết anotepad rất tiện lợi!
        """.trimIndent()

        val parsed = PdfImportHelper.parseExtractedPdfText(samplePdfText, pageCount = 2)

        assertEquals("Kế hoạch phát triển ứng dụng 2026", parsed.note.title)
        assertEquals("Dự án", parsed.note.category)
        assertTrue(parsed.note.description.contains("Đây là nội dung chính"))
        assertEquals(2, parsed.totalPages)
        assertEquals(3, parsed.note.comments.size) // 2 root comments + 1 reply
        assertEquals(2, parsed.commentCount)
        assertEquals(1, parsed.replyCount)

        // Check root comment 1
        val cmt1 = parsed.note.comments[0]
        assertEquals("Đàm Tường Quân", cmt1.authorName)
        assertEquals(null, cmt1.parentId)

        // Check reply to comment 1
        val reply1 = parsed.note.comments[1]
        assertEquals("Đàm Tường Quân", reply1.authorName)
        assertEquals(cmt1.id, reply1.parentId)
        assertEquals("Đàm Tường Quân", reply1.replyToAuthor)

        // Check root comment 2
        val cmt2 = parsed.note.comments[2]
        assertEquals("Lương Thuỷ Tiên", cmt2.authorName)
        assertEquals(null, cmt2.parentId)
    }

    @Test
    fun testParseSimplePdfDocument() {
        val rawText = """
            Danh sách công việc tuần này
            1. Hoàn thành tính năng PDF
            2. Thử nghiệm nhập link anotepad
            3. Kiểm thử hiệu năng và bộ nhớ
        """.trimIndent()

        val parsed = PdfImportHelper.parseExtractedPdfText(rawText, pageCount = 1)
        assertEquals("Danh sách công việc tuần này", parsed.note.title)
        assertTrue(parsed.note.description.contains("1. Hoàn thành tính năng PDF"))
        assertEquals(0, parsed.note.comments.size)
    }
}
