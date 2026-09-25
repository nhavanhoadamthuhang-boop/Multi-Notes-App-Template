package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.NoteEntity
import com.example.ui.theme.PinGold
import com.example.ui.util.rememberCameraLauncher

private val DEFAULT_CATEGORIES = listOf(
    "Công việc",
    "Cá nhân",
    "Học tập",
    "Ý tưởng",
    "Tài chính",
    "Dự án",
    "Du lịch Việt Nam",
    "aNotepad"
)

private val DEFAULT_TAG_SUGGESTIONS = listOf(
    "Quan trọng",
    "Cần làm việc",
    "Dự án",
    "Tài liệu",
    "Họp báo",
    "Du lịch Việt Nam",
    "Du lịch nước Nga",
    "aNotepad"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteEditorDialog(
    initialNote: NoteEntity?,
    availableLabels: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, isPinned: Boolean, category: String, tags: String, imageUri: String?, audioUri: String?, audioDuration: Long?) -> Unit
) {
    var title by remember { mutableStateOf(initialNote?.title ?: "") }
    var description by remember { mutableStateOf(initialNote?.description ?: "") }
    var isPinned by remember { mutableStateOf(initialNote?.isPinned ?: false) }
    var category by remember { mutableStateOf(initialNote?.category ?: "") }
    var attachedImageUri by remember { mutableStateOf(initialNote?.imageUri) }
    var attachedAudioUri by remember { mutableStateOf(initialNote?.audioUri) }
    var attachedAudioDuration by remember { mutableStateOf(initialNote?.audioDuration) }
    var showVoiceRecorder by remember { mutableStateOf(false) }
    var isViewingFullPhoto by remember { mutableStateOf(false) }

    val launchCamera = rememberCameraLauncher { uri ->
        attachedImageUri = uri.toString()
    }
    
    // Undo / Redo history stacks
    data class EditSnapshot(val title: String, val description: String)
    val undoStack = remember { mutableStateListOf<EditSnapshot>() }
    val redoStack = remember { mutableStateListOf<EditSnapshot>() }

    fun pushUndo(newTitle: String, newDesc: String) {
        if (newTitle != title || newDesc != description) {
            undoStack.add(EditSnapshot(title, description))
            redoStack.clear()
        }
    }
    
    // Tag list state
    val tagList = remember {
        mutableStateListOf<String>().apply {
            if (initialNote != null) {
                addAll(initialNote.tagList)
            }
        }
    }
    var newTagInput by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    fun addTag(tag: String) {
        val cleanTag = tag.trim().removePrefix("#")
        if (cleanTag.isNotBlank() && !tagList.any { it.equals(cleanTag, ignoreCase = true) }) {
            tagList.add(cleanTag)
        }
    }

    if (isViewingFullPhoto && attachedImageUri != null) {
        PhotoViewerDialog(
            imageUri = attachedImageUri!!,
            title = if (title.isNotBlank()) title else "Ảnh ghi chú",
            onDismiss = { isViewingFullPhoto = false },
            onDeletePhoto = {
                attachedImageUri = null
                isViewingFullPhoto = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (initialNote == null) "Tạo ghi chú mới" else "Chỉnh sửa ghi chú",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            if (undoStack.isNotEmpty()) {
                                val current = EditSnapshot(title, description)
                                val previous = undoStack.removeAt(undoStack.size - 1)
                                redoStack.add(current)
                                title = previous.title
                                description = previous.description
                            }
                        },
                        enabled = undoStack.isNotEmpty(),
                        modifier = Modifier.testTag("btn_undo_edit")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Undo,
                            contentDescription = "Hoàn tác (Undo)",
                            tint = if (undoStack.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                    IconButton(
                        onClick = {
                            if (redoStack.isNotEmpty()) {
                                val current = EditSnapshot(title, description)
                                val next = redoStack.removeAt(redoStack.size - 1)
                                undoStack.add(current)
                                title = next.title
                                description = next.description
                            }
                        },
                        enabled = redoStack.isNotEmpty(),
                        modifier = Modifier.testTag("btn_redo_edit")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Redo,
                            contentDescription = "Làm lại (Redo)",
                            tint = if (redoStack.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tiêu đề ghi chú
                OutlinedTextField(
                    value = title,
                    onValueChange = { newTitle ->
                        pushUndo(newTitle, description)
                        title = newTitle
                        if (newTitle.isNotBlank()) titleError = false
                    },
                    label = { Text("Tiêu đề ghi chú") },
                    placeholder = { Text("Nhập tiêu đề cho ghi chú...") },
                    isError = titleError,
                    supportingText = {
                        if (titleError) {
                            Text("Vui lòng nhập tiêu đề ghi chú", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_title_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (showVoiceRecorder) {
                    VoiceRecorderDialog(
                        onDismiss = { showVoiceRecorder = false },
                        onResult = { transcript, audioPath, duration ->
                            if (transcript.isNotBlank()) {
                                pushUndo(title, description)
                                if (description.isBlank()) {
                                    description = transcript
                                } else {
                                    description += " $transcript"
                                }
                            }
                            if (audioPath != null) {
                                attachedAudioUri = audioPath
                                attachedAudioDuration = duration
                            }
                            showVoiceRecorder = false
                        },
                        titleText = "Ghi âm ghi chú"
                    )
                }

                // Tiêu đề phần mô tả với nút Ghi âm giọng nói
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mô tả ghi chú",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    OutlinedButton(
                        onClick = { showVoiceRecorder = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_voice_record_note")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Ghi âm dịch giọng nói",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ghi âm giọng nói", fontSize = 11.sp)
                    }
                }

                // Mô tả ghi chú
                OutlinedTextField(
                    value = description,
                    onValueChange = { newDesc ->
                        pushUndo(title, newDesc)
                        description = newDesc
                    },
                    placeholder = { Text("Nhập nội dung chi tiết của ghi chú...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("note_description_input"),
                    maxLines = 8,
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        val wordCount = if (description.isBlank()) 0 else description.trim().split("\\s+".toRegex()).size
                        val charCount = description.length
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("note_word_char_count"),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Số từ: $wordCount",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Số ký tự: $charCount",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                // Voice Play Widget if audio attached
                if (!attachedAudioUri.isNullOrBlank() && attachedAudioDuration != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VoicePlayWidget(
                            audioUri = attachedAudioUri!!,
                            durationMs = attachedAudioDuration!!,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                attachedAudioUri = null
                                attachedAudioDuration = null
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Xoá ghi âm đính kèm",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Ảnh chụp từ Camera đính kèm
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Hình ảnh đính kèm",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = { launchCamera() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_camera_capture_note")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Chụp ảnh từ máy ảnh",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (attachedImageUri == null) "Chụp ảnh" else "Đổi ảnh", fontSize = 12.sp)
                        }
                    }

                    if (attachedImageUri != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { isViewingFullPhoto = true }
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(attachedImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Ảnh đính kèm ghi chú",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Remove button
                                IconButton(
                                    onClick = { attachedImageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.65f))
                                        .testTag("btn_remove_note_photo")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xoá ảnh đính kèm",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                // Tap to view badge
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "Chạm để phóng to",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Thư mục / Thể loại (Category / Folder)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Thư mục / Thể loại",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Tên thư mục") },
                        placeholder = { Text("Chọn gợi ý bên dưới hoặc tự nhập...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_category_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            if (category.isNotBlank()) {
                                IconButton(onClick = { category = "" }, modifier = Modifier.size(24.dp)) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xoá thư mục",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    )

                    // Gợi ý danh mục / thư mục
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        DEFAULT_CATEGORIES.forEach { cat ->
                            val isSelected = category.equals(cat, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    category = if (isSelected) "" else cat
                                },
                                label = { Text(cat, fontSize = 12.sp) },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("category_chip_$cat"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }

                // Thẻ nhãn (Tags / Labels)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Label,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Thẻ nhãn (Tags / Labels)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Danh sách tags đã chọn
                    if (tagList.isNotEmpty()) {
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("selected_tags_flow_row"),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            tagList.forEach { tagItem ->
                                InputChip(
                                    selected = true,
                                    onClick = { tagList.remove(tagItem) },
                                    label = { Text("#$tagItem", fontSize = 12.sp) },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Xoá thẻ $tagItem",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("input_chip_tag_$tagItem"),
                                    colors = InputChipDefaults.inputChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )
                            }
                        }
                    }

                    // Nhập tag mới
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newTagInput,
                            onValueChange = { newTagInput = it },
                            placeholder = { Text("Nhập tên nhãn (ví dụ: khẩn cấp)...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("new_tag_input_field"),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (newTagInput.isNotBlank()) {
                                    addTag(newTagInput)
                                    newTagInput = ""
                                }
                            },
                            enabled = newTagInput.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_add_tag")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.size(4.dp))
                            Text("Thêm")
                        }
                    }

                    // Gợi ý tag & nhãn đã tạo
                    val combinedSuggestions = (availableLabels + DEFAULT_TAG_SUGGESTIONS).map { it.trim().removePrefix("#") }.filter { it.isNotBlank() }.distinctBy { it.lowercase() }
                    val remainingSuggestions = combinedSuggestions.filter { sug ->
                        !tagList.any { it.equals(sug, ignoreCase = true) }
                    }
                    if (remainingSuggestions.isNotEmpty()) {
                        Text(
                            text = "Nhãn sẵn có & Gợi ý:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            remainingSuggestions.forEach { sug ->
                                SuggestionChip(
                                    onClick = { addTag(sug) },
                                    label = { Text("+#$sug", fontSize = 11.sp) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("suggestion_chip_$sug")
                                )
                            }
                        }
                    }
                }

                // Ghim ghi chú
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Ghim ghi chú",
                            tint = if (isPinned) PinGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Ghim ghi chú lên đầu",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isPinned) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }

                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        modifier = Modifier.testTag("pin_note_switch"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.surface,
                            checkedTrackColor = PinGold
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        val tagsString = tagList.joinToString(",")
                        onSave(title, description, isPinned, category, tagsString, attachedImageUri, attachedAudioUri, attachedAudioDuration)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_note_button")
            ) {
                Text("Lưu ghi chú")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("cancel_note_button")
            ) {
                Text("Huỷ")
            }
        }
    )
}


