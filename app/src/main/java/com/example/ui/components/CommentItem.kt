package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.CommentWithReplies
import com.example.ui.theme.PinGold
import com.example.ui.theme.PinGoldContainer
import com.example.ui.util.DateUtils

@Composable
fun CommentThreadItem(
    thread: CommentWithReplies,
    onReplyToComment: (CommentEntity) -> Unit,
    onTogglePinComment: (CommentEntity) -> Unit,
    onEditComment: (CommentEntity) -> Unit,
    onDeleteComment: (CommentEntity) -> Unit,
    onEditReply: (CommentEntity) -> Unit,
    onDeleteReply: (CommentEntity) -> Unit,
    onToggleBookmarkComment: ((CommentEntity) -> Unit)? = null,
    onToggleArchiveComment: ((CommentEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("comment_thread_${thread.comment.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (thread.comment.isPinned) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = if (thread.comment.isPinned) 1.5.dp else if (thread.comment.isBookmarked) 1.2.dp else 1.dp,
            color = if (thread.comment.isPinned) PinGold.copy(alpha = 0.6f) else if (thread.comment.isBookmarked) PinGold.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (thread.comment.isPinned) 1.5.dp else 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Main Root Comment
            SingleCommentContent(
                comment = thread.comment,
                isReply = false,
                onReply = { onReplyToComment(thread.comment) },
                onTogglePin = { onTogglePinComment(thread.comment) },
                onToggleBookmark = onToggleBookmarkComment?.let { callback -> { callback(thread.comment) } },
                onToggleArchive = onToggleArchiveComment?.let { callback -> { callback(thread.comment) } },
                onEdit = { onEditComment(thread.comment) },
                onDelete = { onDeleteComment(thread.comment) }
            )

            // Nested Replies if any
            if (thread.replies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    // Vertical thread indent guide line
                    Box(
                        modifier = Modifier
                            .padding(start = 14.dp, end = 12.dp)
                            .width(2.5.dp)
                            .fillMaxHeight()
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(1.dp)
                            )
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        thread.replies.forEach { reply ->
                            ReplyContent(
                                reply = reply,
                                onReply = { onReplyToComment(reply) },
                                onTogglePin = { onTogglePinComment(reply) },
                                onToggleBookmark = onToggleBookmarkComment?.let { callback -> { callback(reply) } },
                                onToggleArchive = onToggleArchiveComment?.let { callback -> { callback(reply) } },
                                onEdit = { onEditReply(reply) },
                                onDelete = { onDeleteReply(reply) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SingleCommentContent(
    comment: CommentEntity,
    isReply: Boolean,
    onReply: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleBookmark: (() -> Unit)? = null,
    onToggleArchive: (() -> Unit)? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Author Row + Pin Badge + Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Author Avatar
                AuthorAvatar(
                    name = comment.authorName,
                    isPinned = comment.isPinned,
                    avatarType = comment.avatarType,
                    avatarValue = comment.avatarValue,
                    avatarBgColor = comment.avatarBgColor,
                    size = 34
                )

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = comment.authorName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (comment.isPinned) {
                            Surface(
                                shape = CircleShape,
                                color = PinGoldContainer,
                                modifier = Modifier.testTag("pinned_comment_badge_${comment.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.PushPin,
                                        contentDescription = "Đã ghim",
                                        tint = PinGold,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "ĐÃ GHIM",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PinGold
                                    )
                                }
                            }
                        }

                        if (comment.isBookmarked) {
                            Surface(
                                shape = CircleShape,
                                color = PinGoldContainer,
                                modifier = Modifier.testTag("bookmarked_comment_badge_${comment.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Đã yêu thích",
                                        tint = PinGold,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = "YÊU THÍCH",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PinGold
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = DateUtils.formatDetailedElapsedTime(comment.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }

            // Overflow Menu 3-dot for Comment Actions
            var menuExpanded by remember { mutableStateOf(false) }

            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("btn_comment_menu_${comment.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Tùy chọn bình luận",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    // 📌 Ghim / Bỏ ghim bình luận
                    DropdownMenuItem(
                        text = { Text(if (comment.isPinned) "Bỏ ghim bình luận" else "Ghim bình luận") },
                        leadingIcon = {
                            Icon(
                                imageVector = if (comment.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                contentDescription = null,
                                tint = if (comment.isPinned) PinGold else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onTogglePin()
                        },
                        modifier = Modifier.testTag("menu_pin_comment_${comment.id}")
                    )

                    // ✏️ Chỉnh sửa bình luận
                    DropdownMenuItem(
                        text = { Text("Chỉnh sửa bình luận") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        },
                        modifier = Modifier.testTag("menu_edit_comment_${comment.id}")
                    )

                    // ⭐ Yêu thích / Bỏ yêu thích bình luận
                    if (onToggleBookmark != null) {
                        DropdownMenuItem(
                            text = { Text(if (comment.isBookmarked) "Bỏ yêu thích" else "Yêu thích bình luận") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (comment.isBookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = null,
                                    tint = if (comment.isBookmarked) PinGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onToggleBookmark()
                            },
                            modifier = Modifier.testTag("menu_bookmark_comment_${comment.id}")
                        )
                    }

                    // 📦 Lưu trữ / Hủy lưu trữ bình luận
                    if (onToggleArchive != null) {
                        DropdownMenuItem(
                            text = { Text(if (comment.isArchived) "Bỏ lưu trữ bình luận" else "Lưu trữ bình luận") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onToggleArchive()
                            },
                            modifier = Modifier.testTag("menu_archive_comment_${comment.id}")
                        )
                    }

                    // 🗑️ Xoá bình luận
                    DropdownMenuItem(
                        text = { Text("Xoá bình luận", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onDelete()
                        },
                        modifier = Modifier.testTag("menu_delete_comment_${comment.id}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content
        if (comment.content.isNotBlank()) {
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )
        }

        // Ghi âm đính kèm
        if (!comment.audioUri.isNullOrBlank() && comment.audioDuration != null) {
            Spacer(modifier = Modifier.height(6.dp))
            VoicePlayWidget(
                audioUri = comment.audioUri,
                durationMs = comment.audioDuration,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (comment.imageUri != null) {
            Spacer(modifier = Modifier.height(8.dp))
            var isViewingFullPhoto by remember { mutableStateOf(false) }

            if (isViewingFullPhoto) {
                PhotoViewerDialog(
                    imageUri = comment.imageUri!!,
                    title = "Ảnh bình luận của ${comment.authorName}",
                    onDismiss = { isViewingFullPhoto = false }
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { isViewingFullPhoto = true }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(comment.imageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Ảnh đính kèm",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Reply action button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onReply() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
                    .testTag("reply_to_comment_btn_${comment.id}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Trả lời",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Trả lời",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ReplyContent(
    reply: CommentEntity,
    onReply: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleBookmark: (() -> Unit)? = null,
    onToggleArchive: (() -> Unit)? = null,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (reply.isPinned) {
            PinGoldContainer.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        },
        border = BorderStroke(
            width = if (reply.isPinned) 1.2.dp else if (reply.isBookmarked) 1.dp else 0.8.dp,
            color = if (reply.isPinned) PinGold.copy(alpha = 0.6f) else if (reply.isBookmarked) PinGold.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reply_item_${reply.id}")
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AuthorAvatar(
                        name = reply.authorName,
                        isPinned = reply.isPinned,
                        avatarType = reply.avatarType,
                        avatarValue = reply.avatarValue,
                        avatarBgColor = reply.avatarBgColor,
                        size = 26
                    )

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = reply.authorName,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )

                            if (reply.replyToAuthor != null) {
                                Text(
                                    text = "→ @${reply.replyToAuthor}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (reply.isPinned) {
                                Surface(
                                    shape = CircleShape,
                                    color = PinGoldContainer,
                                    modifier = Modifier.testTag("pinned_reply_badge_${reply.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PushPin,
                                            contentDescription = "Phản hồi đã ghim",
                                            tint = PinGold,
                                            modifier = Modifier.size(9.dp)
                                        )
                                        Text(
                                            text = "GHIM",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PinGold
                                        )
                                    }
                                }
                            }

                            if (reply.isBookmarked) {
                                Surface(
                                    shape = CircleShape,
                                    color = PinGoldContainer,
                                    modifier = Modifier.testTag("bookmarked_reply_badge_${reply.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Phản hồi đã yêu thích",
                                            tint = PinGold,
                                            modifier = Modifier.size(9.dp)
                                        )
                                        Text(
                                            text = "SAO",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PinGold
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = DateUtils.formatDetailedElapsedTime(reply.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                }

                // Overflow Menu 3-dot for Reply Actions
                var replyMenuExpanded by remember { mutableStateOf(false) }

                Box {
                    IconButton(
                        onClick = { replyMenuExpanded = true },
                        modifier = Modifier
                            .size(30.dp)
                            .testTag("btn_reply_menu_${reply.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Tùy chọn phản hồi",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = replyMenuExpanded,
                        onDismissRequest = { replyMenuExpanded = false }
                    ) {
                        // 📌 Ghim / Bỏ ghim phản hồi
                        DropdownMenuItem(
                            text = { Text(if (reply.isPinned) "Bỏ ghim phản hồi" else "Ghim phản hồi") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (reply.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                    contentDescription = null,
                                    tint = if (reply.isPinned) PinGold else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                replyMenuExpanded = false
                                onTogglePin()
                            },
                            modifier = Modifier.testTag("menu_pin_reply_${reply.id}")
                        )

                        // ✏️ Chỉnh sửa phản hồi
                        DropdownMenuItem(
                            text = { Text("Chỉnh sửa phản hồi") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                replyMenuExpanded = false
                                onEdit()
                            },
                            modifier = Modifier.testTag("menu_edit_reply_${reply.id}")
                        )

                        // ⭐ Yêu thích / Bỏ yêu thích phản hồi
                        if (onToggleBookmark != null) {
                            DropdownMenuItem(
                                text = { Text(if (reply.isBookmarked) "Bỏ yêu thích" else "Yêu thích phản hồi") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = if (reply.isBookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = null,
                                        tint = if (reply.isBookmarked) PinGold else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    replyMenuExpanded = false
                                    onToggleBookmark()
                                },
                                modifier = Modifier.testTag("menu_bookmark_reply_${reply.id}")
                            )
                        }

                        // 📦 Lưu trữ / Hủy lưu trữ phản hồi
                        if (onToggleArchive != null) {
                            DropdownMenuItem(
                                text = { Text(if (reply.isArchived) "Bỏ lưu trữ phản hồi" else "Lưu trữ phản hồi") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Archive,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    replyMenuExpanded = false
                                    onToggleArchive()
                                },
                                modifier = Modifier.testTag("menu_archive_reply_${reply.id}")
                            )
                        }

                        // 🗑️ Xoá phản hồi
                        DropdownMenuItem(
                            text = { Text("Xoá phản hồi", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                replyMenuExpanded = false
                                onDelete()
                            },
                            modifier = Modifier.testTag("menu_delete_reply_${reply.id}")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Reply content
            if (reply.content.isNotBlank()) {
                Text(
                    text = reply.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }

            // Ghi âm đính kèm của phản hồi
            if (!reply.audioUri.isNullOrBlank() && reply.audioDuration != null) {
                Spacer(modifier = Modifier.height(6.dp))
                VoicePlayWidget(
                    audioUri = reply.audioUri,
                    durationMs = reply.audioDuration,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (reply.imageUri != null) {
                Spacer(modifier = Modifier.height(6.dp))
                var isViewingFullPhoto by remember { mutableStateOf(false) }

                if (isViewingFullPhoto) {
                    PhotoViewerDialog(
                        imageUri = reply.imageUri!!,
                        title = "Ảnh phản hồi của ${reply.authorName}",
                        onDismiss = { isViewingFullPhoto = false }
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { isViewingFullPhoto = true }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(reply.imageUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Ảnh đính kèm",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Reply to this reply
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onReply() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("reply_to_reply_btn_${reply.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Reply,
                        contentDescription = "Trả lời",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Trả lời",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
