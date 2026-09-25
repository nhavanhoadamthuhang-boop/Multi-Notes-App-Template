package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.backup.BackupData
import com.example.data.backup.JsonBackupHelper
import com.example.data.local.NoteEntity
import com.example.data.repository.ImportSummary
import com.example.ui.NotesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MultiFilesImportExportDialog(
    viewModel: NotesViewModel,
    singleNoteToExport: NoteEntity? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }
    var isProcessing by remember { mutableStateOf(false) }
    val autoBackupState by viewModel.autoBackupState.collectAsStateWithLifecycle()

    // Format selection
    var selectedFormat by remember { mutableStateOf("JSON") }
    val formatOptions = listOf("JSON", "XML", "XLSX", "HTML", "CSV", "DOCX", "PPTX", "TXT")

    // Export State
    var exportedJsonText by remember { mutableStateOf("") }
    var exportStats by remember { mutableStateOf<Pair<Int, Int>?>(null) } // notes, comments
    var showPreview by remember { mutableStateOf(false) }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }
    var previewTextState by remember { mutableStateOf("") }

    // Compute preview text asynchronously
    LaunchedEffect(selectedFormat, exportedJsonText, showPreview) {
        if (showPreview) {
            withContext(Dispatchers.IO) {
                val preview = when (selectedFormat) {
                    "JSON" -> exportedJsonText
                    "XML" -> viewModel.exportAllNotesXml()
                    "XLSX" -> "[Bảng tính Microsoft Excel (.xlsx) với 3 trang tính: Ghi chú, Bình luận, Phản hồi]"
                    "TXT" -> viewModel.exportAllNotesTxt()
                    "HTML" -> viewModel.exportAllNotesHtml()
                    "CSV" -> viewModel.exportAllNotesCsv()
                    else -> ""
                }.take(1000)
                withContext(Dispatchers.Main) {
                    previewTextState = preview
                }
            }
        }
    }

    // Import State
    var importedRawText by remember { mutableStateOf("") }
    var parsedBackupData by remember { mutableStateOf<BackupData?>(null) }
    var parseErrorMessage by remember { mutableStateOf<String?>(null) }
    var replaceExistingData by remember { mutableStateOf(false) }
    var importSuccessSummary by remember { mutableStateOf<ImportSummary?>(null) }
    var importedFormat by remember { mutableStateOf("") }
    var importUri by remember { mutableStateOf<Uri?>(null) }
    var pasteFormat by remember { mutableStateOf("JSON") }

    // Default File Name for Export
    val timestampStr = remember {
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    }
    val defaultFileName = remember(singleNoteToExport) {
        if (singleNoteToExport != null) {
            val sanitizedTitle = singleNoteToExport.title
                .replace(Regex("[^a-zA-Z0-9_-]"), "_")
                .take(20)
            "GhiChu_${sanitizedTitle}_$timestampStr.json"
        } else {
            "GhiChu_Backup_$timestampStr.json"
        }
    }

    // Prepare export JSON on open or when singleNoteToExport changes
    LaunchedEffect(singleNoteToExport) {
        isProcessing = true
        withContext(Dispatchers.IO) {
            val json = if (singleNoteToExport != null) {
                viewModel.exportSingleNoteJson(singleNoteToExport.id) ?: ""
            } else {
                viewModel.exportAllNotesJson()
            }
            val parseResult = JsonBackupHelper.parseJson(json)
            val stats = if (parseResult.isSuccess) {
                val data = parseResult.getOrThrow()
                Pair(data.notes.size, data.totalCommentsCount)
            } else {
                Pair(0, 0)
            }
            withContext(Dispatchers.Main) {
                exportedJsonText = json
                exportStats = stats
                isProcessing = false
            }
        }
    }

    fun getFileExtension(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val mimeTypeMap = android.webkit.MimeTypeMap.getSingleton()
        var extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
        if (extension == null) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        val name = it.getString(nameIndex)
                        extension = name.substringAfterLast('.', "")
                    }
                }
            }
        }
        return extension?.lowercase() ?: ""
    }

    fun saveFile(uri: Uri, format: String) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    when (format) {
                        "JSON" -> outputStream.write(exportedJsonText.toByteArray(Charsets.UTF_8))
                        "XML" -> outputStream.write(viewModel.exportAllNotesXml().toByteArray(Charsets.UTF_8))
                        "XLSX" -> viewModel.exportAllNotesXlsx(outputStream)
                        "TXT" -> outputStream.write(viewModel.exportAllNotesTxt().toByteArray(Charsets.UTF_8))
                        "HTML" -> outputStream.write(viewModel.exportAllNotesHtml().toByteArray(Charsets.UTF_8))
                        "CSV" -> outputStream.write(viewModel.exportAllNotesCsv().toByteArray(Charsets.UTF_8))
                        "DOCX" -> viewModel.exportAllNotesDocx(outputStream)
                        "PPTX" -> viewModel.exportAllNotesPptx(outputStream)
                    }
                }
                withContext(Dispatchers.Main) {
                    exportSuccessMessage = "Đã lưu tệp $format thành công vào thiết bị!"
                    Toast.makeText(context, "Đã lưu tệp $format thành công!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lỗi khi lưu tệp: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Launchers for saving different file formats
    val saveJsonLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> if (uri != null) saveFile(uri, "JSON") }

    val saveXmlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/xml")
    ) { uri -> if (uri != null) saveFile(uri, "XML") }

    val saveXlsxLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ) { uri -> if (uri != null) saveFile(uri, "XLSX") }

    val saveTxtLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri -> if (uri != null) saveFile(uri, "TXT") }

    val saveHtmlLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/html")
    ) { uri -> if (uri != null) saveFile(uri, "HTML") }

    val saveCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri -> if (uri != null) saveFile(uri, "CSV") }

    val saveDocxLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    ) { uri -> if (uri != null) saveFile(uri, "DOCX") }

    val savePptxLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.openxmlformats-officedocument.presentationml.presentation")
    ) { uri -> if (uri != null) saveFile(uri, "PPTX") }

    fun triggerSaveFlow() {
        val nameWithExt = when (selectedFormat) {
            "JSON" -> defaultFileName
            "XML" -> defaultFileName.replace(".json", ".xml")
            "XLSX" -> defaultFileName.replace(".json", ".xlsx")
            "TXT" -> defaultFileName.replace(".json", ".txt")
            "HTML" -> defaultFileName.replace(".json", ".html")
            "CSV" -> defaultFileName.replace(".json", ".csv")
            "DOCX" -> defaultFileName.replace(".json", ".docx")
            "PPTX" -> defaultFileName.replace(".json", ".pptx")
            else -> defaultFileName
        }
        when (selectedFormat) {
            "JSON" -> saveJsonLauncher.launch(nameWithExt)
            "XML" -> saveXmlLauncher.launch(nameWithExt)
            "XLSX" -> saveXlsxLauncher.launch(nameWithExt)
            "TXT" -> saveTxtLauncher.launch(nameWithExt)
            "HTML" -> saveHtmlLauncher.launch(nameWithExt)
            "CSV" -> saveCsvLauncher.launch(nameWithExt)
            "DOCX" -> saveDocxLauncher.launch(nameWithExt)
            "PPTX" -> savePptxLauncher.launch(nameWithExt)
        }
    }

    // File Picker Launcher for Importing Multi-Format Files
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            importUri = uri
            val extension = getFileExtension(context, uri)
            importedFormat = extension
            coroutineScope.launch(Dispatchers.IO) {
                try {
                    if (extension == "docx") {
                        val result = com.example.data.backup.MultiFormatBackupHelper.parseDocx(context, uri)
                        withContext(Dispatchers.Main) {
                            if (result.isSuccess) {
                                parsedBackupData = result.getOrThrow()
                                parseErrorMessage = null
                                importedRawText = "[Tệp tin Word .docx]"
                            } else {
                                parsedBackupData = null
                                parseErrorMessage = result.exceptionOrNull()?.message ?: "Tệp DOCX không hợp lệ"
                            }
                        }
                    } else if (extension == "pptx") {
                        val result = com.example.data.backup.MultiFormatBackupHelper.parsePptx(context, uri)
                        withContext(Dispatchers.Main) {
                            if (result.isSuccess) {
                                parsedBackupData = result.getOrThrow()
                                parseErrorMessage = null
                                importedRawText = "[Tệp tin PowerPoint .pptx]"
                            } else {
                                parsedBackupData = null
                                parseErrorMessage = result.exceptionOrNull()?.message ?: "Tệp PPTX không hợp lệ"
                            }
                        }
                    } else if (extension == "xlsx") {
                        val result = com.example.data.backup.MultiFormatBackupHelper.parseXlsx(context, uri)
                        withContext(Dispatchers.Main) {
                            if (result.isSuccess) {
                                parsedBackupData = result.getOrThrow()
                                parseErrorMessage = null
                                importedRawText = "[Bảng tính Excel .xlsx]"
                            } else {
                                parsedBackupData = null
                                parseErrorMessage = result.exceptionOrNull()?.message ?: "Tệp Excel không hợp lệ"
                            }
                        }
                    } else {
                        val content = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                        } ?: ""
                        withContext(Dispatchers.Main) {
                            importedRawText = content
                            val result = when (extension) {
                                "json" -> JsonBackupHelper.parseJson(content)
                                "xml" -> com.example.data.backup.MultiFormatBackupHelper.parseXml(content)
                                "txt" -> com.example.data.backup.MultiFormatBackupHelper.parseTxt(content)
                                "html" -> com.example.data.backup.MultiFormatBackupHelper.parseHtml(content)
                                "csv" -> com.example.data.backup.MultiFormatBackupHelper.parseCsv(content)
                                else -> Result.failure(Exception("Định dạng không được hỗ trợ (.$extension)"))
                            }
                            if (result.isSuccess) {
                                parsedBackupData = result.getOrThrow()
                                parseErrorMessage = null
                            } else {
                                parsedBackupData = null
                                parseErrorMessage = result.exceptionOrNull()?.message ?: "Tệp tin không hợp lệ"
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        parsedBackupData = null
                        parseErrorMessage = "Không thể đọc tệp: ${e.message}"
                    }
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .testTag("json_import_export_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DataObject,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (singleNoteToExport != null) "Xuất ghi chú đa định dạng" else "Nhập/xuất tệp tin đa định dạng",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
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
                // Tab Selection (Export vs Import)
                if (singleNoteToExport == null) {
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .padding(bottom = 16.dp)
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Xuất tệp", fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Nhập tệp", fontWeight = FontWeight.SemiBold) },
                            icon = { Icon(Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        )
                    }
                }

                if (isProcessing) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator()
                            Text("Đang xử lý dữ liệu...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else if (selectedTab == 0) {
                    // ==========================================
                    // EXPORT TAB
                    // ==========================================
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Format Chip Selection
                        Text(
                            text = "Chọn định dạng xuất:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            formatOptions.forEach { format ->
                                val isSelected = selectedFormat == format
                                Surface(
                                    selected = isSelected,
                                    onClick = {
                                        selectedFormat = format
                                        exportSuccessMessage = null
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Text(
                                        text = format,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }

                        // Export Info Card
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (singleNoteToExport != null)
                                            "Xuất ghi chú \"${singleNoteToExport.title}\" sang .$selectedFormat"
                                        else "Sao lưu dữ liệu sang tệp .$selectedFormat",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                val noteCount = exportStats?.first ?: 0
                                val commentCount = exportStats?.second ?: 0
                                Text(
                                    text = "• Ghi chú: $noteCount\n• Bình luận & Phản hồi: $commentCount",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = when (selectedFormat) {
                                        "JSON" -> "Tệp tin JSON chuẩn cấu trúc, lý tưởng nhất để sao lưu và khôi phục nguyên vẹn dữ liệu."
                                        "XML" -> "Tệp tin XML chuẩn (.xml), cấu trúc phân cấp cây thẻ rõ ràng gồm ghi chú, bình luận và phản hồi."
                                        "XLSX" -> "Bảng tính Microsoft Excel (.xlsx) chuyên nghiệp với 3 trang tính (Ghi chú, Bình luận, Phản hồi), mở trực tiếp bằng Excel, Google Sheets, WPS."
                                        "HTML" -> "Thiết kế trang web trực quan, có cấu trúc CSS đẹp mắt, dễ dàng xem trực tiếp trên mọi trình duyệt."
                                        "CSV" -> "Bảng tính CSV phân tách bằng dấu phẩy, dễ dàng nhập vào Excel hoặc Google Sheets."
                                        "DOCX" -> "Tài liệu Microsoft Word (.docx) chuẩn, mở trực tiếp bằng Word, Google Docs hay WPS Office."
                                        "PPTX" -> "Bài thuyết trình Microsoft PowerPoint (.pptx) chuẩn, mở trực tiếp bằng PowerPoint hay Google Slides."
                                        else -> "Tệp tin Plain Text (.txt) thuần túy siêu nhẹ, tương thích với tất cả các thiết bị."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (exportSuccessMessage != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                    Text(
                                        text = exportSuccessMessage!!,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }

                        // Primary Action: Save to File
                        Button(
                            onClick = { triggerSaveFlow() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_save_json_file"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lưu tệp .$selectedFormat vào máy", fontWeight = FontWeight.Bold)
                        }

                        // Secondary Actions: Share & Copy
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    try {
                                        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
                                        val suffix = ".${selectedFormat.lowercase()}"
                                        val cacheFile = File(exportDir, defaultFileName.replace(".json", suffix))
                                        
                                        coroutineScope.launch(Dispatchers.IO) {
                                            if (selectedFormat == "DOCX") {
                                                cacheFile.outputStream().use { fos ->
                                                    viewModel.exportAllNotesDocx(fos)
                                                }
                                            } else if (selectedFormat == "PPTX") {
                                                cacheFile.outputStream().use { fos ->
                                                    viewModel.exportAllNotesPptx(fos)
                                                }
                                            } else if (selectedFormat == "XLSX") {
                                                cacheFile.outputStream().use { fos ->
                                                    viewModel.exportAllNotesXlsx(fos)
                                                }
                                            } else {
                                                val content = when (selectedFormat) {
                                                    "JSON" -> exportedJsonText
                                                    "XML" -> viewModel.exportAllNotesXml()
                                                    "TXT" -> viewModel.exportAllNotesTxt()
                                                    "HTML" -> viewModel.exportAllNotesHtml()
                                                    "CSV" -> viewModel.exportAllNotesCsv()
                                                    else -> ""
                                                }
                                                cacheFile.writeText(content)
                                            }

                                            withContext(Dispatchers.Main) {
                                                val contentUri = FileProvider.getUriForFile(
                                                    context,
                                                    "${context.packageName}.fileprovider",
                                                    cacheFile
                                                )

                                                val mimeType = when (selectedFormat) {
                                                    "JSON" -> "application/json"
                                                    "XML" -> "application/xml"
                                                    "XLSX" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                                    "TXT" -> "text/plain"
                                                    "HTML" -> "text/html"
                                                    "CSV" -> "text/csv"
                                                    "DOCX" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                                    "PPTX" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation"
                                                    else -> "*/*"
                                                }

                                                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = mimeType
                                                    putExtra(Intent.EXTRA_STREAM, contentUri)
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }

                                                val shareIntent = Intent.createChooser(sendIntent, "Chia sẻ tệp $selectedFormat").apply {
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }
                                                context.startActivity(shareIntent)
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Không thể chia sẻ tệp: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chia sẻ")
                            }

                            if (selectedFormat != "DOCX") {
                                FilledTonalButton(
                                    onClick = {
                                        try {
                                            coroutineScope.launch(Dispatchers.IO) {
                                                val textToCopy = when (selectedFormat) {
                                                    "JSON" -> exportedJsonText
                                                    "TXT" -> viewModel.exportAllNotesTxt()
                                                    "HTML" -> viewModel.exportAllNotesHtml()
                                                    "CSV" -> viewModel.exportAllNotesCsv()
                                                    else -> ""
                                                }
                                                withContext(Dispatchers.Main) {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    val clip = ClipData.newPlainText("Ghi chú $selectedFormat", textToCopy)
                                                    clipboard.setPrimaryClip(clip)
                                                    Toast.makeText(context, "Đã sao chép văn bản $selectedFormat vào khay nhớ tạm!", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Dung lượng quá lớn để sao chép vào khay nhớ tạm!", Toast.LENGTH_LONG).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sao chép")
                                }
                            }
                        }

                        // Preview Toggle
                        if (selectedFormat != "DOCX") {
                            OutlinedButton(
                                onClick = { showPreview = !showPreview },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (showPreview) "Ẩn nội dung xem trước" else "Xem trước nội dung $selectedFormat")
                            }

                            AnimatedVisibility(visible = showPreview) {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                ) {
                                    Text(
                                        text = previewTextState + if (previewTextState.length >= 1000) "\n\n... (đã rút gọn xem trước)" else "",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }

                        // Auto Daily Backup Settings Card (JSON)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_json_auto_backup_quick"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (autoBackupState.isEnabled)
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Sync,
                                            contentDescription = null,
                                            tint = if (autoBackupState.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Column {
                                            Text(
                                                text = "Tự động sao lưu hàng ngày",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (autoBackupState.isEnabled) "Đang bật sao lưu định kỳ trên máy (JSON)" else "Đang tắt tự động sao lưu",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (autoBackupState.isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Switch(
                                        checked = autoBackupState.isEnabled,
                                        onCheckedChange = { isChecked ->
                                            viewModel.setAutoBackupEnabled(isChecked)
                                            Toast.makeText(
                                                context,
                                                if (isChecked) "Đã bật tự động sao lưu hàng ngày" else "Đã tắt tự động sao lưu",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        modifier = Modifier.testTag("switch_json_auto_backup")
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // ==========================================
                    // IMPORT TAB
                    // ==========================================
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // File picker button
                        Button(
                            onClick = {
                                openFileLauncher.launch(arrayOf("*/*"))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("btn_select_json_file"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chọn tệp tin dữ liệu (JSON, HTML, CSV, TXT)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }

                        // Or Paste text directly
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Dán văn bản trực tiếp:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("JSON", "XML", "HTML", "CSV", "TXT").forEach { pasteOpt ->
                                    val isSelected = pasteFormat == pasteOpt
                                    Surface(
                                        selected = isSelected,
                                        onClick = { pasteFormat = pasteOpt },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Text(
                                            text = pasteOpt,
                                            color = if (isSelected) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = importedRawText,
                            onValueChange = { newText ->
                                importedRawText = newText
                                importedFormat = pasteFormat.lowercase()
                                importUri = null
                                if (newText.isNotBlank()) {
                                    val result = when (pasteFormat) {
                                        "JSON" -> JsonBackupHelper.parseJson(newText)
                                        "XML" -> com.example.data.backup.MultiFormatBackupHelper.parseXml(newText)
                                        "TXT" -> com.example.data.backup.MultiFormatBackupHelper.parseTxt(newText)
                                        "HTML" -> com.example.data.backup.MultiFormatBackupHelper.parseHtml(newText)
                                        "CSV" -> com.example.data.backup.MultiFormatBackupHelper.parseCsv(newText)
                                        else -> Result.failure(Exception("Không hỗ trợ"))
                                    }
                                    if (result.isSuccess) {
                                        parsedBackupData = result.getOrThrow()
                                        parseErrorMessage = null
                                    } else {
                                        parsedBackupData = null
                                        parseErrorMessage = result.exceptionOrNull()?.message ?: "Văn bản không hợp lệ"
                                    }
                                } else {
                                    parsedBackupData = null
                                    parseErrorMessage = null
                                }
                            },
                            label = { Text("Dán mã nguồn hoặc nội dung văn bản để nạp") },
                            placeholder = { Text("Dán văn bản .$pasteFormat tại đây...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp, max = 150.dp)
                                .testTag("import_json_textfield"),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Validation Result Badge
                        if (parsedBackupData != null) {
                            val data = parsedBackupData!!
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "Dữ liệu hợp lệ! (Định dạng: ${importedFormat.uppercase()})",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Phát hiện ${data.notes.size} ghi chú và ${data.totalCommentsCount} bình luận/phản hồi sẵn sàng nạp.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        } else if (parseErrorMessage != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = parseErrorMessage!!,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }

                        // Import Mode Selection
                        if (parsedBackupData != null) {
                            Text(
                                text = "Chế độ nhập:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    // Option 1: Merge / Append
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { replaceExistingData = false }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = !replaceExistingData,
                                            onClick = { replaceExistingData = false }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Thêm vào danh sách hiện có", fontWeight = FontWeight.SemiBold)
                                            Text(
                                                "Giữ nguyên các ghi chú hiện tại, thêm các ghi chú mới từ tệp dữ liệu.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    // Option 2: Replace all
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { replaceExistingData = true }
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = replaceExistingData,
                                            onClick = { replaceExistingData = true }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text("Thay thế toàn bộ (Ghi đè)", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.error)
                                            Text(
                                                "Xoá tất cả ghi chú hiện có và nạp dữ liệu hoàn toàn mới.",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            // Confirm Import Button
                            Button(
                                onClick = {
                                    val rawText = importedRawText
                                    val ext = importedFormat.lowercase()
                                    val uri = importUri
                                    
                                    isProcessing = true
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val result = if (ext == "docx" && uri != null) {
                                            viewModel.importDocxData(context, uri, replaceExistingData)
                                        } else if (ext == "pptx" && uri != null) {
                                            viewModel.importPptxData(context, uri, replaceExistingData)
                                        } else if (ext == "json") {
                                            viewModel.importJsonData(rawText, replaceExistingData)
                                        } else {
                                            viewModel.importMultiFormatData(rawText, ext, replaceExistingData)
                                        }

                                        withContext(Dispatchers.Main) {
                                            isProcessing = false
                                            if (result.isSuccess) {
                                                val summary = result.getOrThrow()
                                                importSuccessSummary = summary
                                                Toast.makeText(
                                                    context,
                                                    "Đã nhập thành công ${summary.notesImported} ghi chú, ${summary.commentsImported} bình luận và ${summary.repliesImported} phản hồi!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                parseErrorMessage = "Lỗi khi nhập dữ liệu: ${result.exceptionOrNull()?.message}"
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_confirm_import"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (replaceExistingData) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.FileUpload, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (replaceExistingData) "Xác nhận ghi đè & Nhập" else "Bắt đầu nhập dữ liệu",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Success Summary Card
                        if (importSuccessSummary != null) {
                            val summary = importSuccessSummary!!
                            Surface(
                                color = MaterialTheme.colorScheme.tertiaryContainer,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.tertiary
                                        )
                                        Text(
                                            text = "Nhập dữ liệu thành công!",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "• Đã nạp: ${summary.notesImported} ghi chú\n• Đã nạp: ${summary.commentsImported} bình luận gốc\n• Đã nạp: ${summary.repliesImported} phản hồi lồng nhau",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_close_button")
            ) {
                Text("Xong")
            }
        }
    )
}
