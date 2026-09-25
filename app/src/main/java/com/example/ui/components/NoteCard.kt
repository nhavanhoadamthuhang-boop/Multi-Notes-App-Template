package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.outlined.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoCamera
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.CommentEntity
import com.example.data.local.NoteEntity
import com.example.ui.theme.PinGold
import com.example.ui.theme.PinGoldContainer
import com.example.ui.util.DateUtils

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun NoteCard(
    note: NoteEntity,
    commentCount: Int = 0,
    latestComment: CommentEntity? = null,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCategoryClick: ((String) -> Unit)? = null,
    onTagClick: ((String) -> Unit)? = null,
    onToggleBookmark: (() -> Unit)? = null,
    onToggleCompleted: (() -> Unit)? = null,
    onToggleArchived: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("note_card_${note.id}")
            .combinedClickable(
                onClick = onClick,
                onLongClick = { menuExpanded = true }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (note.isPinned) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (note.isPinned) 1.5.dp else if (note.isBookmarked) 1.2.dp else 1.dp,
            color = if (note.isPinned) PinGold.copy(alpha = 0.5f) else if (note.isBookmarked) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (note.isPinned) 2.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Pinned badge or spacer, and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (note.isPinned) {
                        Surface(
                            shape = CircleShape,
                            color = PinGoldContainer,
                            modifier = Modifier.testTag("pinned_badge_${note.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = "Ghim ghi chú",
                                    tint = PinGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "ĐÃ GHIM",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PinGold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    if (note.isBookmarked) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.testTag("bookmarked_badge_${note.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Bookmark,
                                    contentDescription = "Đã đánh dấu",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "ĐÁNH DẤU",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    if (note.isCompleted) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.testTag("completed_badge_${note.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Hoàn thành",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "HOÀN THÀNH",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    if (!note.isPinned && !note.isBookmarked && !note.isCompleted) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Ghi chú",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Star/Favorite Button
                    if (onToggleBookmark != null) {
                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("bookmark_button_${note.id}")
                        ) {
                            Icon(
                                imageVector = if (note.isBookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = if (note.isBookmarked) "Bỏ yêu thích" else "Yêu thích ghi chú",
                                tint = if (note.isBookmarked) PinGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Quick Pin / Unpin Button
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("pin_button_${note.id}")
                    ) {
                        Icon(
                            imageVector = if (note.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (note.isPinned) "Bỏ ghim ghi chú" else "Ghim ghi chú",
                            tint = if (note.isPinned) PinGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // More Menu Button
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("menu_button_${note.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Tuỳ chọn",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Chỉnh sửa") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )
                        if (onToggleBookmark != null) {
                            DropdownMenuItem(
                                text = { Text(if (note.isBookmarked) "Bỏ yêu thích" else "Yêu thích ghi chú") },
                                leadingIcon = {
                                    Icon(
                                        if (note.isBookmarked) Icons.Outlined.StarBorder else Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = PinGold
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onToggleBookmark()
                                }
                            )
                        }
                        if (onToggleCompleted != null) {
                            DropdownMenuItem(
                                text = { Text(if (note.isCompleted) "Chưa hoàn thành" else "Đánh dấu hoàn thành") },
                                leadingIcon = {
                                    Icon(
                                        if (note.isCompleted) Icons.Outlined.CheckBoxOutlineBlank else Icons.Filled.CheckBox,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onToggleCompleted()
                                }
                            )
                        }
                        if (onToggleArchived != null) {
                            DropdownMenuItem(
                                text = { Text(if (note.isArchived) "Hủy lưu trữ" else "Lưu trữ ghi chú") },
                                leadingIcon = {
                                    Icon(
                                        if (note.isArchived) Icons.Filled.Unarchive else Icons.Filled.Archive,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onToggleArchived()
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text(if (note.isPinned) "Bỏ ghim" else "Ghim lên đầu") },
                            leadingIcon = {
                                Icon(
                                    if (note.isPinned) Icons.Outlined.PushPin else Icons.Filled.PushPin,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onTogglePin()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Xoá ghi chú", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tiêu đề ghi chú
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Ảnh đính kèm của ghi chú nếu có
            if (!note.imageUri.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .testTag("note_attached_image_${note.id}")
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(note.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Ảnh đính kèm ghi chú ${note.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Trích dẫn / Xem trước nội dung ghi chú (Note Snippet / Preview)
            if (note.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_content_snippet_${note.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = RoundedCornerShape(2.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier
                                .width(3.dp)
                                .height(36.dp)
                        ) {}
                        Text(
                            text = note.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Ghi âm giọng nói đính kèm
            if (!note.audioUri.isNullOrBlank() && note.audioDuration != null) {
                Spacer(modifier = Modifier.height(8.dp))
                VoicePlayWidget(
                    audioUri = note.audioUri,
                    durationMs = note.audioDuration,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Category & Tags Row
            if (note.category.isNotBlank() || note.tagList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (note.category.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier
                                .testTag("note_category_badge_${note.id}")
                                .then(
                                    if (onCategoryClick != null) Modifier.clickable { onCategoryClick(note.category) } else Modifier
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Folder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = note.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    note.tagList.forEach { tag ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                            modifier = Modifier
                                .testTag("note_tag_badge_${note.id}_$tag")
                                .then(
                                    if (onTagClick != null) Modifier.clickable { onTagClick(tag) } else Modifier
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "#$tag",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Trích dẫn / Xem trước bình luận (Comment Snippet / Preview)
            if (latestComment != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("note_comment_preview_${note.id}")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (latestComment.isPinned) Icons.Filled.PushPin else Icons.Outlined.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = if (latestComment.isPinned) PinGold else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = if (latestComment.isPinned) "Bình luận đã ghim" else "Bình luận mới nhất",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (latestComment.isPinned) PinGold else MaterialTheme.colorScheme.primary,
                                    fontSize = 10.5.sp
                                )
                            }

                            Text(
                                text = DateUtils.formatDetailedElapsedTime(latestComment.createdAt),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AuthorAvatar(
                                name = latestComment.authorName,
                                avatarType = latestComment.avatarType,
                                avatarValue = latestComment.avatarValue,
                                avatarBgColor = latestComment.avatarBgColor,
                                size = 20
                            )

                            Text(
                                text = latestComment.authorName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.5.sp,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        if (latestComment.content.isNotBlank()) {
                            Text(
                                text = latestComment.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }

                        if (!latestComment.imageUri.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhotoCamera,
                                    contentDescription = "Có ảnh đính kèm",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Đã đính kèm ảnh",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom metadata Row: Note timestamp & Comments count badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = "Thời gian ghi chú",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = DateUtils.formatDetailedElapsedTime(note.updatedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }

                // Comment Count & Status Indicator
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (commentCount > 0) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("note_comment_count_badge_${note.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (commentCount > 0) Icons.Default.Forum else Icons.Outlined.ChatBubbleOutline,
                            contentDescription = null,
                            tint = if (commentCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = if (commentCount > 0) "$commentCount bình luận" else "Chưa có bình luận",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (commentCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (commentCount > 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 10.5.sp
                        )
                    }
                }
            }
        }
    }
}
