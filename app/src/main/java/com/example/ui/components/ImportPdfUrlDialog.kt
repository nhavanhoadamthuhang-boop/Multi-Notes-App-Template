package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.data.backup.BackupComment
import com.example.data.backup.BackupNote
import com.example.data.repository.ImportSummary
import com.example.ui.NotesViewModel
import com.example.utils.PdfParsedData
import com.example.utils.UrlParsedNoteData
import kotlinx.coroutines.launch

@Composable
fun ImportPdfUrlDialog(
    viewModel: NotesViewModel,
    initialTab: Int = 0,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(initialTab) }
    var isProcessing by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successSummary by remember { mutableStateOf<ImportSummary?>(null) }

    // --- PDF State ---
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var pdfFileName by remember { mutableStateOf<String?>(null) }
    var parsedPdfData by remember { mutableStateOf<PdfParsedData?>(null) }
    var pdfTitleInput by remember { mutableStateOf("") }
    var pdfCategoryInput by remember { mutableStateOf("") }
    var pdfTagsInput by remember { mutableStateOf("") }
    var pdfDescriptionInput by remember { mutableStateOf("") }
    var showPdfCommentsList by remember { mutableStateOf(true) }

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedPdfUri = uri
            errorMessage = null
            successSummary = null
            isProcessing = true

            // Query file display name
            var fileName = "Tệp tin.pdf"
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIndex) ?: fileName
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            pdfFileName = fileName

            coroutineScope.launch {
                val result = viewModel.fetchPdfPreview(context, uri)
                isProcessing = false
                if (result.isSuccess) {
                    val data = result.getOrThrow()
                    parsedPdfData = data
                    pdfTitleInput = data.note.title
                    pdfCategoryInput = data.note.category.ifBlank { "PDF Import" }
                    pdfTagsInput = data.note.tags
                    pdfDescriptionInput = data.note.description
                } else {
                    parsedPdfData = null
                    errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Lỗi đọc tệp PDF"
                }
            }
        }
    }

    // --- URL State ---
    var urlInput by remember { mutableStateOf("") }
    var parsedUrlData by remember { mutableStateOf<UrlParsedNoteData?>(null) }
    var urlTitleInput by remember { mutableStateOf("") }
    var urlCategoryInput by remember { mutableStateOf("") }
    var urlTagsInput by remember { mutableStateOf("") }
    var urlDescriptionInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .testTag("dialog_import_pdf_url"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (selectedTab == 0) Icons.Default.PictureAsPdf else Icons.Default.Language,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Nhập Ghi Chú & Bình Luận",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_close_import_dialog")
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Tab Selection
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            errorMessage = null
                            successSummary = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tệp PDF (.pdf)", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_import_pdf")
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            errorMessage = null
                            successSummary = null
                        },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Public, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Từ URL Web", fontWeight = FontWeight.SemiBold)
                            }
                        },
                        modifier = Modifier.testTag("tab_import_url")
                    )
                }

                // Success summary banner
                AnimatedVisibility(visible = successSummary != null) {
                    successSummary?.let { summary ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .testTag("card_import_success")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Nhập thành công!",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "Đã thêm ${summary.notesImported} ghi chú, ${summary.commentsImported} bình luận, ${summary.repliesImported} phản hồi.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }
                    }
                }

                // Error message banner
                AnimatedVisibility(visible = errorMessage != null) {
                    errorMessage?.let { err ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .testTag("card_import_error")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // ==================== TAB 0: PDF IMPORT ====================
                if (selectedTab == 0) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Chọn tệp PDF từ máy",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Tự động phân tích và trích xuất ghi chú, tiêu đề, danh mục, bình luận và các phản hồi lồng nhau bên trong tài liệu PDF.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    pdfPickerLauncher.launch(arrayOf("application/pdf"))
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_select_pdf_file"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (pdfFileName == null) "Chọn tệp PDF (.pdf)" else "Chọn tệp khác...")
                            }

                            if (pdfFileName != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surface,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = pdfFileName ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    if (isProcessing) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Đang đọc và phân tích tệp PDF...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    // Parsed PDF Preview & Edit
                    parsedPdfData?.let { data ->
                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Số trang", style = MaterialTheme.typography.labelSmall)
                                    Text("${data.totalPages}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Bình luận", style = MaterialTheme.typography.labelSmall)
                                    Text("${data.commentCount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Phản hồi", style = MaterialTheme.typography.labelSmall)
                                    Text("${data.replyCount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Editable Title
                        OutlinedTextField(
                            value = pdfTitleInput,
                            onValueChange = { pdfTitleInput = it },
                            label = { Text("Tiêu đề ghi chú") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_pdf_title"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category & Tags Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = pdfCategoryInput,
                                onValueChange = { pdfCategoryInput = it },
                                label = { Text("Danh mục") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_pdf_category"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = pdfTagsInput,
                                onValueChange = { pdfTagsInput = it },
                                label = { Text("Thẻ (tags)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_pdf_tags"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Editable Description
                        OutlinedTextField(
                            value = pdfDescriptionInput,
                            onValueChange = { pdfDescriptionInput = it },
                            label = { Text("Nội dung trích xuất") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp, max = 220.dp)
                                .testTag("input_pdf_description"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Comments Preview
                        if (data.note.comments.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Forum,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Bình luận & Phản hồi phát hiện (${data.note.comments.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                TextButton(
                                    onClick = { showPdfCommentsList = !showPdfCommentsList }
                                ) {
                                    Text(if (showPdfCommentsList) "Ẩn bớt" else "Xem chi tiết")
                                }
                            }

                            AnimatedVisibility(visible = showPdfCommentsList) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    data.note.comments.forEach { cmt ->
                                        val isReply = cmt.parentId != null
                                        Card(
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isReply)
                                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                                else
                                                    MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(start = if (isReply) 20.dp else 0.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(10.dp)) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(
                                                        text = (if (isReply) "↳ " else "") + cmt.authorName + (if (cmt.replyToAuthor != null) " (trả lời ${cmt.replyToAuthor})" else ""),
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        text = if (isReply) "Phản hồi" else "Bình luận",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = cmt.content,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    maxLines = 3,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Button
                        Button(
                            onClick = {
                                val uri = selectedPdfUri ?: return@Button
                                isProcessing = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val finalNote = data.note.copy(
                                        title = pdfTitleInput.trim().ifBlank { "Ghi chú từ PDF" },
                                        category = pdfCategoryInput.trim().ifBlank { "PDF Import" },
                                        tags = pdfTagsInput.trim(),
                                        description = pdfDescriptionInput.trim()
                                    )
                                    val saveResult = viewModel.saveImportedNote(finalNote)
                                    isProcessing = false
                                    if (saveResult.isSuccess) {
                                        successSummary = saveResult.getOrThrow()
                                        Toast.makeText(context, "Đã nhập ghi chú PDF thành công (+20 💎)!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        errorMessage = saveResult.exceptionOrNull()?.localizedMessage ?: "Lỗi lưu ghi chú"
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_save_pdf_note"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isProcessing && pdfTitleInput.isNotBlank()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nhập vào Ghi chú ngay", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // ==================== TAB 1: URL IMPORT ====================
                if (selectedTab == 1) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Nhập liên kết ghi chú hoặc bài viết",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Hỗ trợ anotepad (ví dụ: vi.anotepad.com, anotepad.com), các trang blog, ghi chú trực tuyến, và bài viết web.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                label = { Text("Địa chỉ URL (http:// hoặc https://)") },
                                placeholder = { Text("https://vi.anotepad.com/notes/ib9s88hn") },
                                leadingIcon = {
                                    Icon(Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    if (urlInput.isNotEmpty()) {
                                        IconButton(onClick = { urlInput = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Xóa")
                                        }
                                    } else {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                                val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                                                if (!clip.isNullOrBlank()) {
                                                    urlInput = clip.trim()
                                                }
                                            },
                                            modifier = Modifier.testTag("btn_paste_url")
                                        ) {
                                            Icon(Icons.Default.ContentPaste, contentDescription = "Dán từ bộ nhớ tạm")
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_url_address"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sample suggestion chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SuggestionChip(
                                    onClick = {
                                        urlInput = "https://vi.anotepad.com/notes/ib9s88hn"
                                    },
                                    label = { Text("Ví dụ: vi.anotepad.com", fontSize = 12.sp) },
                                    modifier = Modifier.testTag("chip_example_anotepad")
                                )
                                SuggestionChip(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                        val clip = clipboard?.primaryClip?.getItemAt(0)?.text?.toString()
                                        if (!clip.isNullOrBlank()) {
                                            urlInput = clip.trim()
                                        }
                                    },
                                    label = { Text("Dán Link", fontSize = 12.sp) },
                                    modifier = Modifier.testTag("chip_paste_clipboard")
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (urlInput.isBlank()) {
                                        errorMessage = "Vui lòng nhập địa chỉ URL hợp lệ"
                                        return@Button
                                    }
                                    isProcessing = true
                                    errorMessage = null
                                    successSummary = null
                                    coroutineScope.launch {
                                        val result = viewModel.fetchUrlPreview(urlInput.trim())
                                        isProcessing = false
                                        if (result.isSuccess) {
                                            val data = result.getOrThrow()
                                            parsedUrlData = data
                                            urlTitleInput = data.note.title
                                            urlCategoryInput = data.note.category
                                            urlTagsInput = data.note.tags
                                            urlDescriptionInput = data.note.description
                                        } else {
                                            parsedUrlData = null
                                            errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Lỗi tải dữ liệu từ URL"
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_fetch_url_data"),
                                shape = RoundedCornerShape(12.dp),
                                enabled = !isProcessing && urlInput.isNotBlank()
                            ) {
                                if (isProcessing) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Đang tải dữ liệu...")
                                } else {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tải & Trích xuất nội dung", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Parsed URL Preview & Edit
                    parsedUrlData?.let { data ->
                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Card
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Nguồn", style = MaterialTheme.typography.labelSmall)
                                    Text(data.note.category, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Thời gian đọc", style = MaterialTheme.typography.labelSmall)
                                    Text("~${data.estimatedReadTimeMinutes} phút", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                }
                                if (data.commentsCount > 0) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Bình luận", style = MaterialTheme.typography.labelSmall)
                                        Text("${data.commentsCount}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Editable Title
                        OutlinedTextField(
                            value = urlTitleInput,
                            onValueChange = { urlTitleInput = it },
                            label = { Text("Tiêu đề ghi chú") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_url_title"),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Category & Tags Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = urlCategoryInput,
                                onValueChange = { urlCategoryInput = it },
                                label = { Text("Danh mục") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_url_category"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = urlTagsInput,
                                onValueChange = { urlTagsInput = it },
                                label = { Text("Thẻ (tags)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_url_tags"),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Editable Description
                        OutlinedTextField(
                            value = urlDescriptionInput,
                            onValueChange = { urlDescriptionInput = it },
                            label = { Text("Nội dung ghi chú") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 140.dp, max = 240.dp)
                                .testTag("input_url_description"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Action Button
                        Button(
                            onClick = {
                                isProcessing = true
                                errorMessage = null
                                coroutineScope.launch {
                                    val finalNote = data.note.copy(
                                        title = urlTitleInput.trim().ifBlank { "Ghi chú từ URL" },
                                        category = urlCategoryInput.trim().ifBlank { "Web Import" },
                                        tags = urlTagsInput.trim(),
                                        description = urlDescriptionInput.trim()
                                    )
                                    val saveResult = viewModel.saveImportedNote(finalNote)
                                    isProcessing = false
                                    if (saveResult.isSuccess) {
                                        successSummary = saveResult.getOrThrow()
                                        Toast.makeText(context, "Đã lưu ghi chú từ URL thành công (+20 💎)!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        errorMessage = saveResult.exceptionOrNull()?.localizedMessage ?: "Lỗi lưu ghi chú"
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_save_url_note"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isProcessing && urlTitleInput.isNotBlank()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lưu thành Ghi chú mới", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_done_import_dialog")
            ) {
                Text(if (successSummary != null) "Hoàn tất" else "Đóng", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}
