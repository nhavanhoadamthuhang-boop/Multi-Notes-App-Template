package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.local.CommentEntity
import com.example.ui.util.rememberCameraLauncher

@Composable
fun EditCommentDialog(
    comment: CommentEntity,
    isReply: Boolean,
    onDismiss: () -> Unit,
    onSave: (newContent: String, newAuthor: String, avatarType: String, avatarValue: String?, avatarBgColor: Long?, imageUri: String?) -> Unit
) {
    var authorName by remember(comment) { mutableStateOf(comment.authorName) }
    var content by remember(comment) { mutableStateOf(comment.content) }
    var avatarType by remember(comment) { mutableStateOf(comment.avatarType) }
    var avatarValue by remember(comment) { mutableStateOf(comment.avatarValue) }
    var avatarBgColor by remember(comment) { mutableStateOf(comment.avatarBgColor) }
    var attachedImageUri by remember(comment) { mutableStateOf(comment.imageUri) }
    var showAvatarPicker by remember { mutableStateOf(false) }
    var showVoiceRecorder by remember { mutableStateOf(false) }
    var isViewingFullPhoto by remember { mutableStateOf(false) }

    val launchCamera = rememberCameraLauncher { uri ->
        attachedImageUri = uri.toString()
    }

    val title = if (isReply) "Chỉnh sửa phản hồi" else "Chỉnh sửa bình luận"

    if (isViewingFullPhoto && attachedImageUri != null) {
        PhotoViewerDialog(
            imageUri = attachedImageUri!!,
            title = "Ảnh bình luận của $authorName",
            onDismiss = { isViewingFullPhoto = false },
            onDeletePhoto = {
                attachedImageUri = null
                isViewingFullPhoto = false
            }
        )
    }

    if (showAvatarPicker) {
        AvatarPickerDialog(
            currentAuthorName = authorName,
            initialAvatarType = avatarType,
            initialAvatarValue = avatarValue,
            initialAvatarBgColor = avatarBgColor,
            onDismiss = { showAvatarPicker = false },
            onSaveAvatar = { type, value, bgColor ->
                avatarType = type
                avatarValue = value
                avatarBgColor = bgColor
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar Preview & Quick Customize Button
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAvatarPicker = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AuthorAvatar(
                                name = authorName,
                                avatarType = avatarType,
                                avatarValue = avatarValue,
                                avatarBgColor = avatarBgColor,
                                size = 36
                            )
                            Column {
                                Text(
                                    text = "Avatar hiển thị",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Nhấn để đổi biểu tượng / ảnh",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showAvatarPicker = true },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_change_avatar_in_edit")
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Đổi avatar", fontSize = 12.sp)
                        }
                    }
                }

                // Trường "Tên của bạn"
                OutlinedTextField(
                    value = authorName,
                    onValueChange = { authorName = it },
                    label = { Text("Tên của bạn") },
                    placeholder = { Text("Nhập tên hiển thị...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_author_name_field"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                if (showVoiceRecorder) {
                    VoiceRecorderDialog(
                        onDismiss = { showVoiceRecorder = false },
                        onResult = { transcript, _, _ ->
                            if (transcript.isNotBlank()) {
                                if (content.isBlank()) {
                                    content = transcript
                                } else {
                                    content += " $transcript"
                                }
                            }
                            showVoiceRecorder = false
                        },
                        titleText = if (isReply) "Ghi âm phản hồi" else "Ghi âm bình luận"
                    )
                }

                // Trường "Nội dung"
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text(if (isReply) "Nội dung phản hồi" else "Nội dung bình luận") },
                    placeholder = { Text("Nhập nội dung...") },
                    trailingIcon = {
                        IconButton(onClick = { showVoiceRecorder = true }) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Ghi âm giọng nói dịch",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_comment_content_field"),
                    minLines = 3,
                    maxLines = 6,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Ảnh chụp từ Camera đính kèm
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Ảnh đính kèm",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        OutlinedButton(
                            onClick = { launchCamera() },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_camera_capture_edit_comment")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Chụp ảnh từ máy ảnh",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (attachedImageUri == null) "Chụp ảnh" else "Đổi ảnh", fontSize = 11.sp)
                        }
                    }

                    if (attachedImageUri != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { isViewingFullPhoto = true }
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(attachedImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Ảnh đính kèm",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                IconButton(
                                    onClick = { attachedImageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.65f))
                                        .testTag("btn_remove_edit_comment_photo")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xoá ảnh đính kèm",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank() || attachedImageUri != null) {
                        onSave(
                            content.trim(),
                            authorName.trim().ifBlank { "Đàm Tường Quân" },
                            avatarType,
                            avatarValue,
                            avatarBgColor,
                            attachedImageUri
                        )
                    }
                },
                enabled = content.isNotBlank() || attachedImageUri != null,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_edit_comment_button")
            ) {
                Text("Lưu thay đổi")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("cancel_edit_comment_button")
            ) {
                Text("Huỷ")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}


