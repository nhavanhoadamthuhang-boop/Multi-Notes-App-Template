package com.example.data.backup

import android.content.Context
import android.net.Uri
import com.example.data.local.CommentEntity
import com.example.data.local.NoteEntity
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

object MultiFormatBackupHelper {

    // ==========================================
    // EXPORT METHODS
    // ==========================================

    // 1. Export to TXT
    fun exportToTxt(notes: List<NoteEntity>, comments: List<CommentEntity>): String {
        val sb = StringBuilder()
        val commentsByNoteId = comments.groupBy { it.noteId }

        for (note in notes) {
            sb.append("=== BẮT ĐẦU GHI CHÚ ===\n")
            sb.append("ID: ${note.id}\n")
            sb.append("Tiêu đề: ${note.title.replace("\n", "\\n")}\n")
            sb.append("Mô tả: ${note.description.replace("\n", "\\n")}\n")
            sb.append("Danh mục: ${note.category.replace("\n", "\\n")}\n")
            sb.append("Nhãn: ${note.tags.replace("\n", "\\n")}\n")
            sb.append("Ghim: ${note.isPinned}\n")
            sb.append("Yêu thích: ${note.isBookmarked}\n")
            sb.append("Thời gian tạo (ms): ${note.createdAt}\n")
            sb.append("Thời gian sửa (ms): ${note.updatedAt}\n")
            sb.append("Màu sắc: ${note.colorIndex}\n")
            sb.append("Ảnh: ${note.imageUri ?: "null"}\n")

            val noteComments = commentsByNoteId[note.id] ?: emptyList()
            for (comment in noteComments) {
                if (comment.parentId == null) {
                    sb.append("--- BẮT ĐẦU BÌNH LUẬN ---\n")
                    sb.append("ID bình luận: ${comment.id}\n")
                    sb.append("Tác giả: ${comment.authorName.replace("\n", "\\n")}\n")
                    sb.append("Nội dung: ${comment.content.replace("\n", "\\n")}\n")
                    sb.append("Thời gian tạo bình luận (ms): ${comment.createdAt}\n")
                    sb.append("--- KẾT THÚC BÌNH LUẬN ---\n")
                } else {
                    sb.append("--- BẮT ĐẦU PHẢN HỒI ---\n")
                    sb.append("ID phản hồi: ${comment.id}\n")
                    sb.append("ID cha: ${comment.parentId}\n")
                    sb.append("Tác giả: ${comment.authorName.replace("\n", "\\n")}\n")
                    sb.append("Phản hồi cho: ${(comment.replyToAuthor ?: "").replace("\n", "\\n")}\n")
                    sb.append("Nội dung: ${comment.content.replace("\n", "\\n")}\n")
                    sb.append("Thời gian tạo phản hồi (ms): ${comment.createdAt}\n")
                    sb.append("--- KẾT THÚC PHẢN HỒI ---\n")
                }
            }
            sb.append("=== KẾT THÚC GHI CHÚ ===\n\n")
        }
        return sb.toString()
    }

    // 2. Export to HTML
    fun exportToHtml(notes: List<NoteEntity>, comments: List<CommentEntity>): String {
        val sb = StringBuilder()
        sb.append("<!DOCTYPE html>\n")
        sb.append("<html>\n<head>\n<meta charset=\"utf-8\">\n")
        sb.append("<title>Sao lưu Ghi chú du lịch</title>\n")
        sb.append("<style>\n")
        sb.append("body { font-family: sans-serif; background-color: #f5f5f5; color: #333; padding: 20px; }\n")
        sb.append(".note { background: white; padding: 20px; margin-bottom: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n")
        sb.append(".note-title { color: #1a73e8; margin-top: 0; }\n")
        sb.append(".meta { font-size: 0.85em; color: #666; margin-bottom: 15px; }\n")
        sb.append(".comments-section { border-top: 1px solid #eee; padding-top: 15px; margin-top: 15px; }\n")
        sb.append(".comment { background: #f9f9f9; padding: 10px; margin-bottom: 10px; border-left: 3px solid #1a73e8; border-radius: 4px; }\n")
        sb.append(".reply { background: #f1f3f4; padding: 10px; margin-bottom: 10px; margin-left: 30px; border-left: 3px solid #34a853; border-radius: 4px; }\n")
        sb.append(".comment-author, .reply-author { font-weight: bold; font-size: 0.9em; color: #202124; }\n")
        sb.append("</style>\n</head>\n<body>\n")
        sb.append("<h1>DANH SÁCH GHI CHÚ SAO LƯU</h1>\n")

        val commentsByNoteId = comments.groupBy { it.noteId }

        for (note in notes) {
            val noteComments = commentsByNoteId[note.id] ?: emptyList()
            val roots = noteComments.filter { it.parentId == null }
            val replies = noteComments.filter { it.parentId != null }

            sb.append("<div class=\"note\" ")
            sb.append("data-id=\"${note.id}\" ")
            sb.append("data-title=\"${escapeHtmlAttr(note.title)}\" ")
            sb.append("data-category=\"${escapeHtmlAttr(note.category)}\" ")
            sb.append("data-tags=\"${escapeHtmlAttr(note.tags)}\" ")
            sb.append("data-pinned=\"${note.isPinned}\" ")
            sb.append("data-bookmarked=\"${note.isBookmarked}\" ")
            sb.append("data-created-at=\"${note.createdAt}\" ")
            sb.append("data-updated-at=\"${note.updatedAt}\" ")
            sb.append("data-color=\"${note.colorIndex}\" ")
            sb.append("data-image=\"${escapeHtmlAttr(note.imageUri ?: "")}\">\n")

            sb.append("  <h2 class=\"note-title\">${escapeHtml(note.title)}</h2>\n")
            sb.append("  <div class=\"meta\">Danh mục: ${escapeHtml(note.category)} | Nhãn: ${escapeHtml(note.tags)}</div>\n")
            sb.append("  <p class=\"note-description\">${escapeHtml(note.description).replace("\n", "<br>")}</p>\n")

            if (noteComments.isNotEmpty()) {
                sb.append("  <div class=\"comments-section\">\n")
                sb.append("    <h3>Bình luận (${noteComments.size})</h3>\n")
                
                for (root in roots) {
                    sb.append("    <div class=\"comment\" data-id=\"${root.id}\" data-author=\"${escapeHtmlAttr(root.authorName)}\" data-created-at=\"${root.createdAt}\">\n")
                    sb.append("      <div class=\"comment-author\">${escapeHtml(root.authorName)}</div>\n")
                    sb.append("      <p class=\"comment-content\">${escapeHtml(root.content).replace("\n", "<br>")}</p>\n")
                    
                    // Replies for this root
                    val rootReplies = replies.filter { it.parentId == root.id }
                    if (rootReplies.isNotEmpty()) {
                        sb.append("      <div class=\"replies\">\n")
                        for (reply in rootReplies) {
                            sb.append("        <div class=\"reply\" data-id=\"${reply.id}\" data-parent-id=\"${reply.parentId}\" data-author=\"${escapeHtmlAttr(reply.authorName)}\" data-reply-to=\"${escapeHtmlAttr(reply.replyToAuthor ?: "")}\" data-created-at=\"${reply.createdAt}\">\n")
                            sb.append("          <div class=\"reply-author\">${escapeHtml(reply.authorName)} <span style=\"color:#666; font-weight:normal;\">phản hồi</span> ${escapeHtml(reply.replyToAuthor ?: "")}</div>\n")
                            sb.append("          <p class=\"reply-content\">${escapeHtml(reply.content).replace("\n", "<br>")}</p>\n")
                            sb.append("        </div>\n")
                        }
                        sb.append("      </div>\n")
                    }
                    sb.append("    </div>\n")
                }
                sb.append("  </div>\n")
            }
            sb.append("</div>\n\n")
        }

        sb.append("</body>\n</html>")
        return sb.toString()
    }

    // 3. Export to CSV
    fun exportToCsv(notes: List<NoteEntity>, comments: List<CommentEntity>): String {
        val sb = StringBuilder()
        // Headers
        sb.append("Type,TitleOrAuthor,CategoryOrParent,TagsOrId,CreatedAt,IsPinnedOrBookmarked,Content\n")

        val commentsByNoteId = comments.groupBy { it.noteId }

        for (note in notes) {
            val pinBookmarkStatus = when {
                note.isPinned && note.isBookmarked -> "PINNED_BOOKMARKED"
                note.isPinned -> "PINNED"
                note.isBookmarked -> "BOOKMARKED"
                else -> "NONE"
            }
            // Add NOTE row
            sb.append("NOTE,")
            sb.append(escapeCsv(note.title)).append(",")
            sb.append(escapeCsv(note.category)).append(",")
            sb.append(escapeCsv(note.tags)).append(",")
            sb.append(note.createdAt).append(",")
            sb.append(pinBookmarkStatus).append(",")
            sb.append(escapeCsv(note.description.replace("\n", "\\n"))).append("\n")

            val noteComments = commentsByNoteId[note.id] ?: emptyList()
            for (comment in noteComments) {
                if (comment.parentId == null) {
                    sb.append("COMMENT,")
                    sb.append(escapeCsv(comment.authorName)).append(",")
                    sb.append(",,") // category, tags
                    sb.append(comment.createdAt).append(",")
                    sb.append(comment.id).append(",") // Use pin status field to hold commentId for linking replies
                    sb.append(escapeCsv(comment.content.replace("\n", "\\n"))).append("\n")
                } else {
                    sb.append("REPLY,")
                    sb.append(escapeCsv(comment.authorName)).append(",")
                    sb.append(comment.parentId).append(",") // parentId in second column
                    sb.append(escapeCsv(comment.replyToAuthor ?: "")).append(",") // replyToAuthor in tags col
                    sb.append(comment.createdAt).append(",")
                    sb.append(comment.id).append(",") // commentId
                    sb.append(escapeCsv(comment.content.replace("\n", "\\n"))).append("\n")
                }
            }
        }
        return sb.toString()
    }

    // 4. Export to DOCX (using ZipOutputStream to write a real Docx package containing plain text document.xml)
    fun exportToDocx(outStream: OutputStream, notes: List<NoteEntity>, comments: List<CommentEntity>) {
        val zip = ZipOutputStream(outStream)

        // 1. _rels/.rels
        zip.putNextEntry(ZipEntry("_rels/.rels"))
        val relsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>"""
        zip.write(relsXml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 2. [Content_Types].xml
        zip.putNextEntry(ZipEntry("[Content_Types].xml"))
        val contentTypesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
</Types>"""
        zip.write(contentTypesXml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 3. word/document.xml
        zip.putNextEntry(ZipEntry("word/document.xml"))
        val docXmlBuilder = StringBuilder()
        docXmlBuilder.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>""")

        // Generate text representation lines
        val txtContent = exportToTxt(notes, comments)
        val lines = txtContent.split("\n")
        for (line in lines) {
            docXmlBuilder.append("\n    <w:p>\n      <w:r>\n        <w:t>")
            docXmlBuilder.append(escapeXml(line))
            docXmlBuilder.append("</w:t>\n      </w:r>\n    </w:p>")
        }

        docXmlBuilder.append("""
  </w:body>
</w:document>""")

        zip.write(docXmlBuilder.toString().toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        zip.close()
    }

    // 5. Export to PPTX (using ZipOutputStream to write a real minimal PPTX package with slides)
    fun exportToPptx(outStream: OutputStream, notes: List<NoteEntity>, comments: List<CommentEntity>) {
        val zip = ZipOutputStream(outStream)
        val commentsByNoteId = comments.groupBy { it.noteId }

        // 1. _rels/.rels
        zip.putNextEntry(ZipEntry("_rels/.rels"))
        val relsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="ppt/presentation.xml"/>
</Relationships>"""
        zip.write(relsXml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 2. [Content_Types].xml
        zip.putNextEntry(ZipEntry("[Content_Types].xml"))
        val contentTypesBuilder = StringBuilder()
        contentTypesBuilder.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/ppt/presentation.xml" ContentType="application/vnd.openxmlformats-officedocument.presentationml.presentation.main+xml"/>""")
        
        for (i in notes.indices) {
            contentTypesBuilder.append("\n  <Override PartName=\"/ppt/slides/slide${i + 1}.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.presentationml.slide+xml\"/>")
        }
        contentTypesBuilder.append("\n</Types>")
        zip.write(contentTypesBuilder.toString().toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 3. ppt/_rels/presentation.xml.rels
        zip.putNextEntry(ZipEntry("ppt/_rels/presentation.xml.rels"))
        val presRelsBuilder = StringBuilder()
        presRelsBuilder.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">""")
        for (i in notes.indices) {
            presRelsBuilder.append("\n  <Relationship Id=\"rId${i + 1}\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/slide\" Target=\"slides/slide${i + 1}.xml\"/>")
        }
        presRelsBuilder.append("\n</Relationships>")
        zip.write(presRelsBuilder.toString().toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 4. ppt/presentation.xml
        zip.putNextEntry(ZipEntry("ppt/presentation.xml"))
        val presBuilder = StringBuilder()
        presBuilder.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:presentation xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main">
  <p:sldIdLst>""")
        for (i in notes.indices) {
            presBuilder.append("\n    <p:sldId id=\"${256 + i}\" r:id=\"rId${i + 1}\"/>")
        }
        presBuilder.append("""
  </p:sldIdLst>
  <p:notesSz cx="9144000" cy="6858000"/>
</p:presentation>""")
        zip.write(presBuilder.toString().toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 5. ppt/slides/slideN.xml
        for ((index, note) in notes.withIndex()) {
            zip.putNextEntry(ZipEntry("ppt/slides/slide${index + 1}.xml"))
            
            val noteComments = commentsByNoteId[note.id] ?: emptyList()
            
            val slideBuilder = StringBuilder()
            slideBuilder.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<p:sld xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main">
  <p:cSld>
    <p:spTree>
      <p:nvGrpSpPr>
        <p:cNvPr id="1" name=""/>
        <p:cNvGrpSpPr/>
        <p:nvPr/>
      </p:nvGrpSpPr>
      <p:grpSpPr>
        <p:xfrm>
          <a:off x="0" y="0"/>
          <a:ext cx="0" cy="0"/>
          <a:chOff x="0" y="0"/>
          <a:chExt cx="0" cy="0"/>
        </p:xfrm>
      </p:grpSpPr>
      <p:sp>
        <p:nvSpPr>
          <p:cNvPr id="2" name="Title ${index + 2}"/>
          <p:cNvSpPr>
            <a:spLocks noGrp="1"/>
          </p:cNvSpPr>
          <p:nvPr/>
        </p:nvSpPr>
        <p:spPr/>
        <p:txBody>
          <a:bodyPr/>
          <a:lstStyle/>
          <a:p>
            <a:r>
              <a:rPr sz="2400" b="1">
                <a:latin typeface="Arial"/>
              </a:rPr>
              <a:t>${escapeXml(note.title)}</a:t>
            </a:r>
          </a:p>
          <a:p>
            <a:r>
              <a:rPr sz="1400" i="1">
                <a:latin typeface="Arial"/>
              </a:rPr>
              <a:t>Danh mục: ${escapeXml(note.category)} | Nhãn: ${escapeXml(note.tags)}</a:t>
            </a:r>
          </a:p>
          <a:p>
            <a:r>
              <a:rPr sz="1600">
                <a:latin typeface="Arial"/>
              </a:rPr>
              <a:t>${escapeXml(note.description)}</a:t>
            </a:r>
          </a:p>""")
            
            if (noteComments.isNotEmpty()) {
                slideBuilder.append("""
          <a:p>
            <a:r>
              <a:rPr sz="1400" b="1">
                <a:latin typeface="Arial"/>
              </a:rPr>
              <a:t>--- BÌNH LUẬN (${noteComments.size}) ---</a:t>
            </a:r>
          </a:p>""")
                for (comment in noteComments) {
                    val prefix = if (comment.parentId == null) "• " else "  - "
                    slideBuilder.append("""
          <a:p>
            <a:r>
              <a:rPr sz="1200">
                <a:latin typeface="Arial"/>
              </a:rPr>
              <a:t>$prefix${escapeXml(comment.authorName)}: ${escapeXml(comment.content)}</a:t>
            </a:r>
          </a:p>""")
                }
            }

            slideBuilder.append("""
        </p:txBody>
      </p:sp>
    </p:spTree>
  </p:cSld>
</p:sld>""")
            
            zip.write(slideBuilder.toString().toByteArray(Charsets.UTF_8))
            zip.closeEntry()
        }

        zip.close()
    }

    // 6. Export to XML (.xml)
    fun exportToXml(notes: List<NoteEntity>, comments: List<CommentEntity>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<notes_backup version=\"1.0\" app=\"Ghi Chú Đa Năng\" export_date=\"${System.currentTimeMillis()}\">\n")
        val commentsByNoteId = comments.groupBy { it.noteId }
        sb.append("  <notes total=\"${notes.size}\">\n")
        for (note in notes) {
            sb.append("    <note id=\"${note.id}\" is_pinned=\"${note.isPinned}\" is_bookmarked=\"${note.isBookmarked}\" created_at=\"${note.createdAt}\" updated_at=\"${note.updatedAt}\" color_index=\"${note.colorIndex}\">\n")
            sb.append("      <title>${escapeXml(note.title)}</title>\n")
            sb.append("      <category>${escapeXml(note.category)}</category>\n")
            sb.append("      <tags>${escapeXml(note.tags)}</tags>\n")
            sb.append("      <description>${escapeXml(note.description)}</description>\n")
            val img = note.imageUri ?: ""
            if (img.isNotEmpty()) {
                sb.append("      <image_uri>${escapeXml(img)}</image_uri>\n")
            }
            val noteComments = commentsByNoteId[note.id] ?: emptyList()
            val roots = noteComments.filter { it.parentId == null }
            val replies = noteComments.filter { it.parentId != null }
            sb.append("      <comments total=\"${noteComments.size}\">\n")
            for (root in roots) {
                sb.append("        <comment id=\"${root.id}\" author=\"${escapeXml(root.authorName)}\" created_at=\"${root.createdAt}\">\n")
                sb.append("          <content>${escapeXml(root.content)}</content>\n")
                val rootReplies = replies.filter { it.parentId == root.id }
                if (rootReplies.isNotEmpty()) {
                    sb.append("          <replies total=\"${rootReplies.size}\">\n")
                    for (reply in rootReplies) {
                        val rTo = reply.replyToAuthor ?: ""
                        sb.append("            <reply id=\"${reply.id}\" parent_id=\"${reply.parentId}\" author=\"${escapeXml(reply.authorName)}\" reply_to=\"${escapeXml(rTo)}\" created_at=\"${reply.createdAt}\">\n")
                        sb.append("              <content>${escapeXml(reply.content)}</content>\n")
                        sb.append("            </reply>\n")
                    }
                    sb.append("          </replies>\n")
                }
                sb.append("        </comment>\n")
            }
            sb.append("      </comments>\n")
            sb.append("    </note>\n")
        }
        sb.append("  </notes>\n")
        sb.append("</notes_backup>")
        return sb.toString()
    }

    // 7. Export to Excel (.xlsx) using OpenXML SpreadsheetML
    fun exportToXlsx(outStream: OutputStream, notes: List<NoteEntity>, comments: List<CommentEntity>) {
        val zip = ZipOutputStream(outStream)
        val commentsByNoteId = comments.groupBy { it.noteId }
        val allRootComments = comments.filter { it.parentId == null }
        val allReplies = comments.filter { it.parentId != null }
        val noteTitleMap = notes.associate { it.id to it.title }

        // 1. [Content_Types].xml
        zip.putNextEntry(ZipEntry("[Content_Types].xml"))
        val contentTypesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/worksheets/sheet2.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/worksheets/sheet3.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>"""
        zip.write(contentTypesXml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 2. _rels/.rels
        zip.putNextEntry(ZipEntry("_rels/.rels"))
        val relsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>"""
        zip.write(relsXml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 3. xl/_rels/workbook.xml.rels
        zip.putNextEntry(ZipEntry("xl/_rels/workbook.xml.rels"))
        val wbRelsXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet2.xml"/>
  <Relationship Id="rId3" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet3.xml"/>
  <Relationship Id="rId4" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""
        zip.write(wbRelsXml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 4. xl/workbook.xml
        zip.putNextEntry(ZipEntry("xl/workbook.xml"))
        val wbXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Ghi chú" sheetId="1" r:id="rId1"/>
    <sheet name="Bình luận" sheetId="2" r:id="rId2"/>
    <sheet name="Phản hồi" sheetId="3" r:id="rId3"/>
  </sheets>
</workbook>"""
        zip.write(wbXml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 5. xl/styles.xml
        zip.putNextEntry(ZipEntry("xl/styles.xml"))
        val stylesXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="2">
    <font><sz val="11"/><name val="Calibri"/></font>
    <font><b/><sz val="11"/><name val="Calibri"/></font>
  </fonts>
  <fills count="2">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
  </fills>
  <borders count="1">
    <border><left/><right/><top/><bottom/><diagonal/></border>
  </borders>
  <cellStyleXfs count="1">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
  </cellStyleXfs>
  <cellXfs count="2">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
    <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/>
  </cellXfs>
</styleSheet>"""
        zip.write(stylesXml.toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // Helper to format cell
        fun cell(col: String, row: Int, text: String, isHeader: Boolean = false): String {
            val styleAttr = if (isHeader) " s=\"1\"" else ""
            return "<c r=\"$col$row\" t=\"inlineStr\"$styleAttr><is><t>${escapeXml(text)}</t></is></c>"
        }

        // 6. xl/worksheets/sheet1.xml (Ghi chú)
        zip.putNextEntry(ZipEntry("xl/worksheets/sheet1.xml"))
        val s1 = StringBuilder()
        s1.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <sheetData>
    <row r="1">
      ${cell("A", 1, "ID Ghi chú", true)}
      ${cell("B", 1, "Tiêu đề", true)}
      ${cell("C", 1, "Mô tả / Nội dung", true)}
      ${cell("D", 1, "Danh mục", true)}
      ${cell("E", 1, "Nhãn (Tags)", true)}
      ${cell("F", 1, "Đã ghim", true)}
      ${cell("G", 1, "Yêu thích", true)}
      ${cell("H", 1, "Số bình luận", true)}
      ${cell("I", 1, "Thời gian tạo (ms)", true)}
      ${cell("J", 1, "Thời gian sửa (ms)", true)}
    </row>""")

        var rIdx = 2
        for (note in notes) {
            val cCount = (commentsByNoteId[note.id] ?: emptyList()).size
            s1.append("""
    <row r="$rIdx">
      ${cell("A", rIdx, note.id.toString())}
      ${cell("B", rIdx, note.title)}
      ${cell("C", rIdx, note.description)}
      ${cell("D", rIdx, note.category)}
      ${cell("E", rIdx, note.tags)}
      ${cell("F", rIdx, if (note.isPinned) "Có" else "Không")}
      ${cell("G", rIdx, if (note.isBookmarked) "Có" else "Không")}
      ${cell("H", rIdx, cCount.toString())}
      ${cell("I", rIdx, note.createdAt.toString())}
      ${cell("J", rIdx, note.updatedAt.toString())}
    </row>""")
            rIdx++
        }
        s1.append("""
  </sheetData>
</worksheet>""")
        zip.write(s1.toString().toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 7. xl/worksheets/sheet2.xml (Bình luận)
        zip.putNextEntry(ZipEntry("xl/worksheets/sheet2.xml"))
        val s2 = StringBuilder()
        s2.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <sheetData>
    <row r="1">
      ${cell("A", 1, "ID Bình luận", true)}
      ${cell("B", 1, "ID Ghi chú", true)}
      ${cell("C", 1, "Tiêu đề Ghi chú", true)}
      ${cell("D", 1, "Tác giả", true)}
      ${cell("E", 1, "Nội dung bình luận", true)}
      ${cell("F", 1, "Số phản hồi", true)}
      ${cell("G", 1, "Thời gian tạo (ms)", true)}
    </row>""")

        rIdx = 2
        for (root in allRootComments) {
            val noteTitle = noteTitleMap[root.noteId] ?: "Ghi chú #${root.noteId}"
            val replyCount = allReplies.count { it.parentId == root.id }
            s2.append("""
    <row r="$rIdx">
      ${cell("A", rIdx, root.id.toString())}
      ${cell("B", rIdx, root.noteId.toString())}
      ${cell("C", rIdx, noteTitle)}
      ${cell("D", rIdx, root.authorName)}
      ${cell("E", rIdx, root.content)}
      ${cell("F", rIdx, replyCount.toString())}
      ${cell("G", rIdx, root.createdAt.toString())}
    </row>""")
            rIdx++
        }
        s2.append("""
  </sheetData>
</worksheet>""")
        zip.write(s2.toString().toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        // 8. xl/worksheets/sheet3.xml (Phản hồi)
        zip.putNextEntry(ZipEntry("xl/worksheets/sheet3.xml"))
        val s3 = StringBuilder()
        s3.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <sheetData>
    <row r="1">
      ${cell("A", 1, "ID Phản hồi", true)}
      ${cell("B", 1, "ID Bình luận cha", true)}
      ${cell("C", 1, "ID Ghi chú", true)}
      ${cell("D", 1, "Tác giả phản hồi", true)}
      ${cell("E", 1, "Phản hồi cho", true)}
      ${cell("F", 1, "Nội dung phản hồi", true)}
      ${cell("G", 1, "Thời gian tạo (ms)", true)}
    </row>""")

        rIdx = 2
        for (reply in allReplies) {
            s3.append("""
    <row r="$rIdx">
      ${cell("A", rIdx, reply.id.toString())}
      ${cell("B", rIdx, (reply.parentId ?: 0).toString())}
      ${cell("C", rIdx, reply.noteId.toString())}
      ${cell("D", rIdx, reply.authorName)}
      ${cell("E", rIdx, reply.replyToAuthor ?: "")}
      ${cell("F", rIdx, reply.content)}
      ${cell("G", rIdx, reply.createdAt.toString())}
    </row>""")
            rIdx++
        }
        s3.append("""
  </sheetData>
</worksheet>""")
        zip.write(s3.toString().toByteArray(Charsets.UTF_8))
        zip.closeEntry()

        zip.close()
    }


    // ==========================================
    // IMPORT METHODS
    // ==========================================

    // 1. Import from TXT
    fun parseTxt(txtString: String): Result<BackupData> {
        return try {
            val notes = mutableListOf<BackupNote>()
            val lines = txtString.split("\n")

            var currentNote: BackupNote? = null
            var currentComments = mutableListOf<BackupComment>()

            var noteId: Long? = null
            var title = ""
            var description = ""
            var category = ""
            var tags = ""
            var isPinned = false
            var isBookmarked = false
            var createdAt = System.currentTimeMillis()
            var updatedAt = System.currentTimeMillis()
            var colorIndex = 0
            var imageUri: String? = null

            var commentId: Long? = null
            var commentParentId: Long? = null
            var commentAuthor = ""
            var commentReplyTo: String? = null
            var commentContent = ""
            var commentCreatedAt = System.currentTimeMillis()

            for (lineRaw in lines) {
                val line = lineRaw.trim()
                if (line.isEmpty()) continue

                when {
                    line == "=== BẮT ĐẦU GHI CHÚ ===" -> {
                        noteId = null
                        title = ""
                        description = ""
                        category = ""
                        tags = ""
                        isPinned = false
                        isBookmarked = false
                        createdAt = System.currentTimeMillis()
                        updatedAt = System.currentTimeMillis()
                        colorIndex = 0
                        imageUri = null
                        currentComments = mutableListOf()
                    }
                    line.startsWith("ID: ") -> {
                        noteId = line.substringAfter("ID: ").toLongOrNull()
                    }
                    line.startsWith("Tiêu đề: ") -> {
                        title = line.substringAfter("Tiêu đề: ").replace("\\n", "\n")
                    }
                    line.startsWith("Mô tả: ") -> {
                        description = line.substringAfter("Mô tả: ").replace("\\n", "\n")
                    }
                    line.startsWith("Danh mục: ") -> {
                        category = line.substringAfter("Danh mục: ").replace("\\n", "\n")
                    }
                    line.startsWith("Nhãn: ") -> {
                        tags = line.substringAfter("Nhãn: ").replace("\\n", "\n")
                    }
                    line.startsWith("Ghim: ") -> {
                        isPinned = line.substringAfter("Ghim: ").toBoolean()
                    }
                    line.startsWith("Yêu thích: ") -> {
                        isBookmarked = line.substringAfter("Yêu thích: ").toBoolean()
                    }
                    line.startsWith("Thời gian tạo (ms): ") -> {
                        createdAt = line.substringAfter("Thời gian tạo (ms): ").toLongOrNull() ?: System.currentTimeMillis()
                    }
                    line.startsWith("Thời gian sửa (ms): ") -> {
                        updatedAt = line.substringAfter("Thời gian sửa (ms): ").toLongOrNull() ?: System.currentTimeMillis()
                    }
                    line.startsWith("Màu sắc: ") -> {
                        colorIndex = line.substringAfter("Màu sắc: ").toIntOrNull() ?: 0
                    }
                    line.startsWith("Ảnh: ") -> {
                        val img = line.substringAfter("Ảnh: ")
                        imageUri = if (img == "null" || img.isBlank()) null else img
                    }
                    line == "--- BẮT ĐẦU BÌNH LUẬN ---" -> {
                        commentId = null
                        commentParentId = null
                        commentAuthor = ""
                        commentReplyTo = null
                        commentContent = ""
                        commentCreatedAt = System.currentTimeMillis()
                    }
                    line.startsWith("ID bình luận: ") -> {
                        commentId = line.substringAfter("ID bình luận: ").toLongOrNull()
                    }
                    line.startsWith("Tác giả: ") -> {
                        commentAuthor = line.substringAfter("Tác giả: ").replace("\\n", "\n")
                    }
                    line.startsWith("Nội dung: ") -> {
                        commentContent = line.substringAfter("Nội dung: ").replace("\\n", "\n")
                    }
                    line.startsWith("Thời gian tạo bình luận (ms): ") -> {
                        commentCreatedAt = line.substringAfter("Thời gian tạo bình luận (ms): ").toLongOrNull() ?: System.currentTimeMillis()
                    }
                    line == "--- KẾT THÚC BÌNH LUẬN ---" -> {
                        if (commentAuthor.isNotBlank() && commentContent.isNotBlank()) {
                            currentComments.add(BackupComment(
                                id = commentId,
                                parentId = null,
                                authorName = commentAuthor,
                                replyToAuthor = null,
                                content = commentContent,
                                createdAt = commentCreatedAt
                            ))
                        }
                    }
                    line == "--- BẮT ĐẦU PHẢN HỒI ---" -> {
                        commentId = null
                        commentParentId = null
                        commentAuthor = ""
                        commentReplyTo = null
                        commentContent = ""
                        commentCreatedAt = System.currentTimeMillis()
                    }
                    line.startsWith("ID phản hồi: ") -> {
                        commentId = line.substringAfter("ID phản hồi: ").toLongOrNull()
                    }
                    line.startsWith("ID cha: ") -> {
                        commentParentId = line.substringAfter("ID cha: ").toLongOrNull()
                    }
                    line.startsWith("Phản hồi cho: ") -> {
                        val r = line.substringAfter("Phản hồi cho: ").replace("\\n", "\n")
                        commentReplyTo = r.ifBlank { null }
                    }
                    line.startsWith("Thời gian tạo phản hồi (ms): ") -> {
                        commentCreatedAt = line.substringAfter("Thời gian tạo phản hồi (ms): ").toLongOrNull() ?: System.currentTimeMillis()
                    }
                    line == "--- KẾT THÚC PHẢN HỒI ---" -> {
                        if (commentAuthor.isNotBlank() && commentContent.isNotBlank()) {
                            currentComments.add(BackupComment(
                                id = commentId,
                                parentId = commentParentId,
                                authorName = commentAuthor,
                                replyToAuthor = commentReplyTo,
                                content = commentContent,
                                createdAt = commentCreatedAt
                            ))
                        }
                    }
                    line == "=== KẾT THÚC GHI CHÚ ===" -> {
                        notes.add(BackupNote(
                            id = noteId,
                            title = title,
                            description = description,
                            isPinned = isPinned,
                            isBookmarked = isBookmarked,
                            createdAt = createdAt,
                            updatedAt = updatedAt,
                            colorIndex = colorIndex,
                            category = category,
                            tags = tags,
                            imageUri = imageUri,
                            comments = currentComments
                        ))
                    }
                }
            }

            if (notes.isEmpty()) {
                Result.failure(IllegalArgumentException("Không tìm thấy dữ liệu ghi chú hợp lệ trong tệp .txt."))
            } else {
                Result.success(BackupData(notes = notes))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. Import from HTML (using JSoup)
    fun parseHtml(htmlString: String): Result<BackupData> {
        return try {
            val notes = mutableListOf<BackupNote>()
            val doc = Jsoup.parse(htmlString)
            val noteElements = doc.select("div.note")

            for (noteElem in noteElements) {
                val id = noteElem.attr("data-id").toLongOrNull()
                val title = noteElem.attr("data-title").ifBlank { noteElem.selectFirst(".note-title")?.text() ?: "Ghi chú đã nhập" }
                val category = noteElem.attr("data-category")
                val tags = noteElem.attr("data-tags")
                val isPinned = noteElem.attr("data-pinned") == "true"
                val isBookmarked = noteElem.attr("data-bookmarked") == "true"
                val createdAt = noteElem.attr("data-created-at").toLongOrNull() ?: System.currentTimeMillis()
                val updatedAt = noteElem.attr("data-updated-at").toLongOrNull() ?: createdAt
                val colorIndex = noteElem.attr("data-color").toIntOrNull() ?: 0
                val img = noteElem.attr("data-image")
                val imageUri = if (img.isBlank() || img == "null") null else img

                val descElem = noteElem.selectFirst("p.note-description")
                val description = descElem?.html()?.replace("<br>", "\n")?.replace("<br/>", "\n") ?: ""

                val comments = mutableListOf<BackupComment>()
                
                // Root comments
                val commentElements = noteElem.select("div.comment")
                for (cmtElem in commentElements) {
                    val cmtId = cmtElem.attr("data-id").toLongOrNull()
                    val cmtAuthor = cmtElem.attr("data-author")
                    val cmtCreatedAt = cmtElem.attr("data-created-at").toLongOrNull() ?: System.currentTimeMillis()
                    val cmtContent = cmtElem.selectFirst("p.comment-content")?.html()?.replace("<br>", "\n")?.replace("<br/>", "\n") ?: ""

                    comments.add(BackupComment(
                        id = cmtId,
                        parentId = null,
                        authorName = cmtAuthor,
                        replyToAuthor = null,
                        content = cmtContent,
                        createdAt = cmtCreatedAt
                    ))

                    // Nested replies under this comment
                    val replyElements = cmtElem.select("div.reply")
                    for (repElem in replyElements) {
                        val repId = repElem.attr("data-id").toLongOrNull()
                        val repParentId = repElem.attr("data-parent-id").toLongOrNull() ?: cmtId
                        val repAuthor = repElem.attr("data-author")
                        val repReplyTo = repElem.attr("data-reply-to")
                        val repCreatedAt = repElem.attr("data-created-at").toLongOrNull() ?: System.currentTimeMillis()
                        val repContent = repElem.selectFirst("p.reply-content")?.html()?.replace("<br>", "\n")?.replace("<br/>", "\n") ?: ""

                        comments.add(BackupComment(
                            id = repId,
                            parentId = repParentId,
                            authorName = repAuthor,
                            replyToAuthor = repReplyTo.ifBlank { null },
                            content = repContent,
                            createdAt = repCreatedAt
                        ))
                    }
                }

                notes.add(BackupNote(
                    id = id,
                    title = title,
                    description = description,
                    isPinned = isPinned,
                    isBookmarked = isBookmarked,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    colorIndex = colorIndex,
                    category = category,
                    tags = tags,
                    imageUri = imageUri,
                    comments = comments
                ))
            }

            if (notes.isEmpty()) {
                Result.failure(IllegalArgumentException("Không tìm thấy dữ liệu ghi chú hợp lệ trong tệp .html."))
            } else {
                Result.success(BackupData(notes = notes))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 3. Import from CSV
    fun parseCsv(csvString: String): Result<BackupData> {
        return try {
            val notes = mutableListOf<BackupNote>()
            val lines = csvString.split("\n")

            var currentNote: BackupNote? = null
            var currentComments = mutableListOf<BackupComment>()

            // Map to link temporary file comment IDs to parents
            val oldCommentIdMap = mutableMapOf<Long, Long>()

            for (i in 1 until lines.size) { // skip header
                val line = lines[i].trim()
                if (line.isEmpty()) continue

                val fields = parseCsvLine(line)
                if (fields.isEmpty()) continue

                val type = fields[0]
                when (type) {
                    "NOTE" -> {
                        // Save previous note if any
                        if (currentNote != null) {
                            notes.add(currentNote.copy(comments = currentComments))
                        }

                        val title = fields.getOrNull(1) ?: "Ghi chú đã nhập"
                        val category = fields.getOrNull(2) ?: ""
                        val tags = fields.getOrNull(3) ?: ""
                        val createdAt = fields.getOrNull(4)?.toLongOrNull() ?: System.currentTimeMillis()
                        val pinStatus = fields.getOrNull(5) ?: "NONE"
                        val isPinned = pinStatus == "PINNED" || pinStatus == "PINNED_BOOKMARKED"
                        val isBookmarked = pinStatus == "BOOKMARKED" || pinStatus == "PINNED_BOOKMARKED"
                        val description = (fields.getOrNull(6) ?: "").replace("\\n", "\n")

                        currentNote = BackupNote(
                            title = title,
                            description = description,
                            isPinned = isPinned,
                            isBookmarked = isBookmarked,
                            createdAt = createdAt,
                            updatedAt = createdAt,
                            category = category,
                            tags = tags
                        )
                        currentComments = mutableListOf()
                    }
                    "COMMENT" -> {
                        if (currentNote != null) {
                            val author = fields.getOrNull(1) ?: "Đàm Tường Quân"
                            val createdAt = fields.getOrNull(4)?.toLongOrNull() ?: System.currentTimeMillis()
                            val commentId = fields.getOrNull(5)?.toLongOrNull() ?: System.currentTimeMillis()
                            val content = (fields.getOrNull(6) ?: "").replace("\\n", "\n")

                            currentComments.add(BackupComment(
                                id = commentId,
                                parentId = null,
                                authorName = author,
                                replyToAuthor = null,
                                content = content,
                                createdAt = createdAt
                            ))
                        }
                    }
                    "REPLY" -> {
                        if (currentNote != null) {
                            val author = fields.getOrNull(1) ?: "Đàm Tường Quân"
                            val parentId = fields.getOrNull(2)?.toLongOrNull()
                            val replyTo = fields.getOrNull(3)
                            val createdAt = fields.getOrNull(4)?.toLongOrNull() ?: System.currentTimeMillis()
                            val commentId = fields.getOrNull(5)?.toLongOrNull() ?: System.currentTimeMillis()
                            val content = (fields.getOrNull(6) ?: "").replace("\\n", "\n")

                            currentComments.add(BackupComment(
                                id = commentId,
                                parentId = parentId,
                                authorName = author,
                                replyToAuthor = replyTo?.ifBlank { null },
                                content = content,
                                createdAt = createdAt
                            ))
                        }
                    }
                }
            }

            // Add the last note
            if (currentNote != null) {
                notes.add(currentNote.copy(comments = currentComments))
            }

            if (notes.isEmpty()) {
                Result.failure(IllegalArgumentException("Không tìm thấy dữ liệu ghi chú hợp lệ trong tệp .csv."))
            } else {
                Result.success(BackupData(notes = notes))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. Import from DOCX (by reading Zip and parsing document.xml)
    fun parseDocx(context: Context, uri: Uri): Result<BackupData> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Không thể mở luồng tệp tin DOCX"))
            
            val zip = ZipInputStream(inputStream)
            var entry = zip.getNextEntry()
            var xmlContent = ""
            while (entry != null) {
                if (entry.name == "word/document.xml") {
                    val reader = BufferedReader(InputStreamReader(zip, Charsets.UTF_8))
                    xmlContent = reader.readText()
                    break
                }
                entry = zip.getNextEntry()
            }
            zip.close()

            if (xmlContent.isEmpty()) {
                return Result.failure(IllegalArgumentException("Không thể đọc được nội dung cấu trúc Word XML (word/document.xml) trong tệp tin DOCX."))
            }

            // Extract all <w:t> elements
            val textLines = mutableListOf<String>()
            val pattern = java.util.regex.Pattern.compile("<w:t[^>]*>(.*?)</w:t>")
            val matcher = pattern.matcher(xmlContent)
            while (matcher.find()) {
                val escapedText = matcher.group(1) ?: ""
                textLines.add(unescapeXml(escapedText))
            }

            val docxPlainText = textLines.joinToString("\n")
            parseTxt(docxPlainText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. Import from PPTX (by reading Zip and parsing slides XML files)
    fun parsePptx(context: Context, uri: Uri): Result<BackupData> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Không thể mở luồng tệp tin PPTX"))
            
            val zip = ZipInputStream(inputStream)
            var entry = zip.getNextEntry()
            val slideContents = mutableMapOf<Int, String>()
            
            while (entry != null) {
                if (entry.name.startsWith("ppt/slides/slide") && entry.name.endsWith(".xml")) {
                    val slideIndex = entry.name.substringAfter("ppt/slides/slide").substringBefore(".xml").toIntOrNull() ?: 0
                    val reader = BufferedReader(InputStreamReader(zip, Charsets.UTF_8))
                    val xmlContent = reader.readText()
                    
                    // Extract all <a:t> elements
                    val textLines = mutableListOf<String>()
                    val pattern = java.util.regex.Pattern.compile("<a:t[^>]*>(.*?)</a:t>")
                    val matcher = pattern.matcher(xmlContent)
                    while (matcher.find()) {
                        val escapedText = matcher.group(1) ?: ""
                        textLines.add(unescapeXml(escapedText))
                    }
                    slideContents[slideIndex] = textLines.joinToString("\n")
                }
                entry = zip.getNextEntry()
            }
            zip.close()

            if (slideContents.isEmpty()) {
                return Result.failure(IllegalArgumentException("Không thể đọc được nội dung slide trong tệp tin PPTX."))
            }

            // Combine slide contents in order of slide index
            val pptxPlainText = slideContents.keys.sorted().map { slideContents[it] }.joinToString("\n\n")
            parseTxt(pptxPlainText)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 6. Import from XML (.xml)
    fun parseXml(xmlString: String): Result<BackupData> {
        return try {
            val dbFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
            dbFactory.isNamespaceAware = false
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(java.io.ByteArrayInputStream(xmlString.toByteArray(Charsets.UTF_8)))
            doc.documentElement.normalize()

            val notes = mutableListOf<BackupNote>()
            val noteNodes = doc.getElementsByTagName("note")

            for (i in 0 until noteNodes.length) {
                val noteNode = noteNodes.item(i)
                if (noteNode.nodeType != org.w3c.dom.Node.ELEMENT_NODE) continue
                val noteElement = noteNode as org.w3c.dom.Element

                val id = noteElement.getAttribute("id")?.toLongOrNull()
                val isPinned = noteElement.getAttribute("is_pinned")?.toBoolean() ?: false
                val isBookmarked = noteElement.getAttribute("is_bookmarked")?.toBoolean() ?: false
                val createdAt = noteElement.getAttribute("created_at")?.toLongOrNull() ?: System.currentTimeMillis()
                val updatedAt = noteElement.getAttribute("updated_at")?.toLongOrNull() ?: createdAt
                val colorIndex = noteElement.getAttribute("color_index")?.toIntOrNull() ?: 0

                fun getDirectChildText(parent: org.w3c.dom.Element, tagName: String): String {
                    val children = parent.childNodes
                    for (c in 0 until children.length) {
                        val node = children.item(c)
                        if (node.nodeType == org.w3c.dom.Node.ELEMENT_NODE && node.nodeName == tagName) {
                            return node.textContent ?: ""
                        }
                    }
                    return ""
                }

                val title = getDirectChildText(noteElement, "title").trim().ifBlank { "Ghi chú đã nhập" }
                val description = getDirectChildText(noteElement, "description").trim()
                val category = getDirectChildText(noteElement, "category").trim()
                val tags = getDirectChildText(noteElement, "tags").trim()
                val imageUri = getDirectChildText(noteElement, "image_uri").trim().ifBlank { null }

                val comments = mutableListOf<BackupComment>()
                val commentNodes = noteElement.getElementsByTagName("comment")
                for (j in 0 until commentNodes.length) {
                    val cNode = commentNodes.item(j)
                    if (cNode.nodeType != org.w3c.dom.Node.ELEMENT_NODE) continue
                    val cElement = cNode as org.w3c.dom.Element
                    val cId = cElement.getAttribute("id")?.toLongOrNull()
                    val cAuthor = cElement.getAttribute("author") ?: "Tác giả"
                    val cCreatedAt = cElement.getAttribute("created_at")?.toLongOrNull() ?: System.currentTimeMillis()
                    val cContent = getDirectChildText(cElement, "content").trim()

                    if (cContent.isNotBlank() || cAuthor.isNotBlank()) {
                        comments.add(
                            BackupComment(
                                id = cId,
                                parentId = null,
                                authorName = cAuthor.ifBlank { "Tác giả" },
                                replyToAuthor = null,
                                content = cContent,
                                createdAt = cCreatedAt
                            )
                        )
                    }

                    val replyNodes = cElement.getElementsByTagName("reply")
                    for (k in 0 until replyNodes.length) {
                        val rNode = replyNodes.item(k)
                        if (rNode.nodeType != org.w3c.dom.Node.ELEMENT_NODE) continue
                        val rElement = rNode as org.w3c.dom.Element
                        val rId = rElement.getAttribute("id")?.toLongOrNull()
                        val rParentId = rElement.getAttribute("parent_id")?.toLongOrNull() ?: cId
                        val rAuthor = rElement.getAttribute("author") ?: "Tác giả"
                        val rReplyTo = rElement.getAttribute("reply_to")?.ifBlank { null }
                        val rCreatedAt = rElement.getAttribute("created_at")?.toLongOrNull() ?: System.currentTimeMillis()
                        val rContent = getDirectChildText(rElement, "content").trim()

                        if (rContent.isNotBlank() || rAuthor.isNotBlank()) {
                            comments.add(
                                BackupComment(
                                    id = rId,
                                    parentId = rParentId,
                                    authorName = rAuthor.ifBlank { "Tác giả" },
                                    replyToAuthor = rReplyTo,
                                    content = rContent,
                                    createdAt = rCreatedAt
                                )
                            )
                        }
                    }
                }

                notes.add(
                    BackupNote(
                        id = id,
                        title = title,
                        description = description,
                        isPinned = isPinned,
                        isBookmarked = isBookmarked,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        colorIndex = colorIndex,
                        category = category,
                        tags = tags,
                        imageUri = imageUri,
                        comments = comments
                    )
                )
            }

            if (notes.isEmpty()) {
                Result.failure(IllegalArgumentException("Không tìm thấy ghi chú hợp lệ trong tệp .xml."))
            } else {
                Result.success(BackupData(notes = notes))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 7. Import from Excel (.xlsx) by parsing worksheet XML entries
    fun parseXlsx(context: Context, uri: Uri): Result<BackupData> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Không thể mở luồng tệp tin Excel .xlsx"))

            val zip = ZipInputStream(inputStream)
            var entry = zip.getNextEntry()
            var sheet1Xml = ""
            var sheet2Xml = ""
            var sheet3Xml = ""

            while (entry != null) {
                val name = entry.name
                if (name == "xl/worksheets/sheet1.xml") {
                    sheet1Xml = BufferedReader(InputStreamReader(zip, Charsets.UTF_8)).readText()
                } else if (name == "xl/worksheets/sheet2.xml") {
                    sheet2Xml = BufferedReader(InputStreamReader(zip, Charsets.UTF_8)).readText()
                } else if (name == "xl/worksheets/sheet3.xml") {
                    sheet3Xml = BufferedReader(InputStreamReader(zip, Charsets.UTF_8)).readText()
                }
                entry = zip.getNextEntry()
            }
            zip.close()

            if (sheet1Xml.isEmpty()) {
                return Result.failure(IllegalArgumentException("Không tìm thấy cấu trúc bảng tính hợp lệ (xl/worksheets/sheet1.xml) trong tệp Excel."))
            }

            fun parseSheetRows(xml: String): List<Map<String, String>> {
                val rows = mutableListOf<Map<String, String>>()
                val rowPattern = java.util.regex.Pattern.compile("<row[^>]*>(.*?)</row>", java.util.regex.Pattern.DOTALL)
                val cellPattern = java.util.regex.Pattern.compile("<c r=\"([A-Z]+)[0-9]+\"[^>]*>.*?<t>(.*?)</t>.*?</c>", java.util.regex.Pattern.DOTALL)
                val rowMatcher = rowPattern.matcher(xml)
                while (rowMatcher.find()) {
                    val rowXml = rowMatcher.group(1) ?: continue
                    val rowMap = mutableMapOf<String, String>()
                    val cellMatcher = cellPattern.matcher(rowXml)
                    while (cellMatcher.find()) {
                        val col = cellMatcher.group(1) ?: continue
                        val text = unescapeXml(cellMatcher.group(2) ?: "")
                        rowMap[col] = text
                    }
                    if (rowMap.isNotEmpty()) {
                        rows.add(rowMap)
                    }
                }
                return rows
            }

            val sheet1Rows = parseSheetRows(sheet1Xml)
            val sheet2Rows = if (sheet2Xml.isNotEmpty()) parseSheetRows(sheet2Xml) else emptyList()
            val sheet3Rows = if (sheet3Xml.isNotEmpty()) parseSheetRows(sheet3Xml) else emptyList()

            val commentsByNoteId = mutableMapOf<Long, MutableList<BackupComment>>()

            // Parse sheet2 comments (skip header row 0)
            for (i in 1 until sheet2Rows.size) {
                val r = sheet2Rows[i]
                val cmtId = r["A"]?.toLongOrNull() ?: (1000L + i)
                val noteId = r["B"]?.toLongOrNull() ?: 1L
                val author = r["D"] ?: "Tác giả"
                val content = r["E"] ?: ""
                val createdAt = r["G"]?.toLongOrNull() ?: System.currentTimeMillis()

                val cmt = BackupComment(
                    id = cmtId,
                    parentId = null,
                    authorName = author,
                    replyToAuthor = null,
                    content = content,
                    createdAt = createdAt
                )
                commentsByNoteId.getOrPut(noteId) { mutableListOf() }.add(cmt)
            }

            // Parse sheet3 replies (skip header row 0)
            for (i in 1 until sheet3Rows.size) {
                val r = sheet3Rows[i]
                val replyId = r["A"]?.toLongOrNull() ?: (5000L + i)
                val parentId = r["B"]?.toLongOrNull()
                val noteId = r["C"]?.toLongOrNull() ?: 1L
                val author = r["D"] ?: "Tác giả"
                val replyTo = r["E"]
                val content = r["F"] ?: ""
                val createdAt = r["G"]?.toLongOrNull() ?: System.currentTimeMillis()

                val reply = BackupComment(
                    id = replyId,
                    parentId = parentId,
                    authorName = author,
                    replyToAuthor = replyTo?.ifBlank { null },
                    content = content,
                    createdAt = createdAt
                )
                commentsByNoteId.getOrPut(noteId) { mutableListOf() }.add(reply)
            }

            val notes = mutableListOf<BackupNote>()
            // Parse sheet1 notes (skip header row 0)
            for (i in 1 until sheet1Rows.size) {
                val r = sheet1Rows[i]
                val noteId = r["A"]?.toLongOrNull() ?: (i.toLong())
                val title = r["B"] ?: "Ghi chú đã nhập"
                val description = r["C"] ?: ""
                val category = r["D"] ?: ""
                val tags = r["E"] ?: ""
                val isPinned = r["F"] == "Có" || r["F"] == "true"
                val isBookmarked = r["G"] == "Có" || r["G"] == "true"
                val createdAt = r["I"]?.toLongOrNull() ?: System.currentTimeMillis()
                val updatedAt = r["J"]?.toLongOrNull() ?: createdAt

                val noteComments = commentsByNoteId[noteId] ?: emptyList()
                notes.add(
                    BackupNote(
                        id = noteId,
                        title = title,
                        description = description,
                        isPinned = isPinned,
                        isBookmarked = isBookmarked,
                        createdAt = createdAt,
                        updatedAt = updatedAt,
                        category = category,
                        tags = tags,
                        comments = noteComments
                    )
                )
            }

            if (notes.isEmpty()) {
                Result.failure(IllegalArgumentException("Không tìm thấy dữ liệu ghi chú hợp lệ trong tệp Excel .xlsx."))
            } else {
                Result.success(BackupData(notes = notes))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ==========================================
    // ESCAPING HELPERS
    // ==========================================

    private fun escapeHtml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun escapeHtmlAttr(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }

    private fun escapeXml(text: String): String {
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun unescapeXml(text: String): String {
        return text.replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
    }

    private fun escapeCsv(value: String): String {
        val escaped = value.replace("\"", "\"\"")
        return if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            "\"$escaped\""
        } else {
            escaped
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = java.lang.StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '"') {
                if (inQuotes && i + 1 < line.length && line[i + 1] == '"') {
                    current.append('"')
                    i++
                } else {
                    inQuotes = !inQuotes
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString())
                current.setLength(0)
            } else {
                current.append(c)
            }
            i++
        }
        result.add(current.toString())
        return result
    }
}
