package com.example.ui.screens

import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Image
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.utils.PdfExportHelper
import com.example.ui.components.PhotoViewerDialog
import com.example.ui.util.rememberCameraLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.DarkMode
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.example.data.repository.ThemeMode
import com.example.ui.CommentSortOrder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.CommentEntity
import com.example.ui.NotesViewModel
import com.example.ui.components.AuthorAvatar
import com.example.ui.components.AvatarPickerDialog
import com.example.ui.components.CommentThreadItem
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.DiamondGoalDialog
import com.example.ui.components.DiamondStoreDialog
import com.example.ui.components.DiamondTransactionLogDialog
import com.example.ui.components.DiamondTopBarBadge
import com.example.ui.components.EditCommentDialog
import com.example.ui.components.MultiFilesImportExportDialog
import com.example.ui.components.NoteEditorDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StreakBadge
import com.example.ui.components.StreakDialog
import com.example.ui.components.TrashManagerDialog
import com.example.ui.components.UserGuideDialog
import com.example.ui.components.VoiceRecorderDialog
import com.example.ui.components.VoicePlayWidget
import com.example.ui.theme.PinGold
import com.example.ui.theme.PinGoldContainer
import com.example.ui.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NoteDetailScreen(
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier,
    isEmbeddedInSplitPane: Boolean = false
) {
    val note by viewModel.selectedNote.collectAsStateWithLifecycle()
    val commentsState by viewModel.commentsUiState.collectAsStateWithLifecycle()
    val replyingTo by viewModel.replyingTo.collectAsStateWithLifecycle()
    val editingNote by viewModel.editingNote.collectAsStateWithLifecycle()
    val rewardState by viewModel.rewardState.collectAsStateWithLifecycle()
    val diamondTransactions by viewModel.diamondTransactions.collectAsStateWithLifecycle()
    val rateLimitWarning by viewModel.rateLimitWarning.collectAsStateWithLifecycle()
    val autoBackupState by viewModel.autoBackupState.collectAsStateWithLifecycle()
    val streakState by viewModel.streakState.collectAsStateWithLifecycle()
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val activityStats by viewModel.activityStats.collectAsStateWithLifecycle()
    val trashNotes by viewModel.trashNotes.collectAsStateWithLifecycle()
    val trashComments by viewModel.trashComments.collectAsStateWithLifecycle()
    val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()
    val allAvailableLabels by viewModel.allAvailableLabels.collectAsStateWithLifecycle()
    val commentSortOrder by viewModel.commentSortOrder.collectAsStateWithLifecycle()
    val userAvatarType by viewModel.userAvatarType.collectAsStateWithLifecycle()
    val userAvatarValue by viewModel.userAvatarValue.collectAsStateWithLifecycle()
    val userAvatarBgColor by viewModel.userAvatarBgColor.collectAsStateWithLifecycle()

    var commentInput by remember { mutableStateOf("") }
    val commentFocusRequester = remember { FocusRequester() }
    var authorNameInput by remember { mutableStateOf("Đàm Tường Quân") }
    var showAvatarPickerDialog by remember { mutableStateOf(false) }
    var showAuthorEditDialog by remember { mutableStateOf(false) }
    var showTrashDialog by remember { mutableStateOf(false) }
    var showDiamondGoalDialog by remember { mutableStateOf(false) }
    var showDiamondStoreDialog by remember { mutableStateOf(false) }
    var showDiamondHistoryDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showUserGuideDialog by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }
    var commentSortMenuExpanded by remember { mutableStateOf(false) }

    // Deletion confirmation states
    var showDeleteNoteConfirm by remember { mutableStateOf(false) }
    var commentToDelete by remember { mutableStateOf<CommentEntity?>(null) }
    var replyToDelete by remember { mutableStateOf<CommentEntity?>(null) }

    // Comment/Reply editing states
    var commentToEdit by remember { mutableStateOf<CommentEntity?>(null) }
    var replyToEdit by remember { mutableStateOf<CommentEntity?>(null) }

    // JSON export state
    var showExportJsonDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var commentImageUri by remember { mutableStateOf<String?>(null) }
    var commentAudioUri by remember { mutableStateOf<String?>(null) }
    var commentAudioDuration by remember { mutableStateOf<Long?>(null) }
    var showVoiceRecorderForComment by remember { mutableStateOf(false) }
    var activePhotoViewerUri by remember { mutableStateOf<String?>(null) }
    var activePhotoViewerTitle by remember { mutableStateOf("") }

    val launchCameraForComment = rememberCameraLauncher { uri ->
        commentImageUri = uri.toString()
    }

    if (note == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentNote = note!!
    
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val pdfExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val success = PdfExportHelper.exportNoteToPdf(
                    context, uri, currentNote, commentsState.commentThreads
                )
                if (success) {
                    Toast.makeText(context, "Đã xuất PDF thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lỗi khi xuất PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentNote.title,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.selectNote(null) },
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = if (isEmbeddedInSplitPane) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isEmbeddedInSplitPane) "Đóng ghi chú" else "Quay lại"
                        )
                    }
                },
                actions = {
                    // Star/Favorite Note Button
                    IconButton(
                        onClick = { viewModel.toggleNoteBookmarked(currentNote) },
                        modifier = Modifier.testTag("detail_bookmark_note_button")
                    ) {
                        Icon(
                            imageVector = if (currentNote.isBookmarked) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (currentNote.isBookmarked) "Bỏ yêu thích ghi chú" else "Yêu thích ghi chú",
                            tint = if (currentNote.isBookmarked) PinGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Pin Note Button
                    IconButton(
                        onClick = { viewModel.toggleNotePinned(currentNote) },
                        modifier = Modifier.testTag("detail_pin_note_button")
                    ) {
                        Icon(
                            imageVector = if (currentNote.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = if (currentNote.isPinned) "Bỏ ghim ghi chú" else "Ghim ghi chú",
                            tint = if (currentNote.isPinned) PinGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Theme Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("detail_btn_toggle_theme")
                    ) {
                        Icon(
                            imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.WbSunny else Icons.Default.DarkMode,
                            contentDescription = if (themeMode == ThemeMode.DARK) "Chuyển sang Chế độ sáng" else "Chuyển sang Chế độ tối",
                            tint = if (themeMode == ThemeMode.DARK) Color(0xFFFFB300) else MaterialTheme.colorScheme.primary
                        )
                    }

                    // More Menu
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.testTag("detail_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Tuỳ chọn"
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Chỉnh sửa ghi chú") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                viewModel.startEditNote(currentNote)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cửa hàng gói mua (Nâng cấp hạn mức)") },
                            leadingIcon = { Icon(Icons.Default.Diamond, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                menuExpanded = false
                                showDiamondStoreDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Thùng rác & Đã xoá gần đây") },
                            leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                menuExpanded = false
                                showTrashDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Đổi tên người gửi ($authorNameInput)") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                showAuthorEditDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nhập/xuất tệp tin đa định dạng") },
                            leadingIcon = { Icon(Icons.Default.DataObject, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                menuExpanded = false
                                showExportJsonDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Xuất ghi chú sang tệp PDF") },
                            leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                menuExpanded = false
                                val sanitizedTitle = currentNote.title.replace(Regex("[^a-zA-Z0-9_-]"), "_")
                                pdfExportLauncher.launch("${sanitizedTitle}_note.pdf")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hướng dẫn sử dụng (8 bước)") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                menuExpanded = false
                                showUserGuideDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cài đặt & Tự động sao lưu") },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                menuExpanded = false
                                showSettingsDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Chuyển vào Thùng rác", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menuExpanded = false
                                showDeleteNoteConfirm = true
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Comment & Reply input bar
            Surface(
                tonalElevation = 6.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Replying to target notification banner
                    AnimatedVisibility(
                        visible = replyingTo != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        replyingTo?.let { target ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                                    .padding(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Đang trả lời @${target.authorName}: \"${target.content.take(30)}...\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                IconButton(
                                    onClick = { viewModel.setReplyingTo(null) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Huỷ trả lời",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Rate limit & Diamond Reward mini banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "+20 kim cương / bình luận & phản hồi",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = if (rewardState.commentsInCurrentMinute >= (rewardState.maxCommentsPerMinute - 4)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "${rewardState.commentsInCurrentMinute}/${rewardState.maxCommentsPerMinute} cmt/phút",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (rewardState.commentsInCurrentMinute >= (rewardState.maxCommentsPerMinute - 4)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Trường "Tên của bạn" & Tuỳ chỉnh Avatar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Clickable Avatar Preview
                        AuthorAvatar(
                            name = authorNameInput,
                            avatarType = userAvatarType,
                            avatarValue = userAvatarValue,
                            avatarBgColor = userAvatarBgColor,
                            size = 40,
                            onClick = { showAvatarPickerDialog = true },
                            modifier = Modifier.testTag("current_user_avatar_chip")
                        )

                        OutlinedTextField(
                            value = authorNameInput,
                            onValueChange = { authorNameInput = it },
                            label = { Text("Tên của bạn", style = MaterialTheme.typography.labelSmall) },
                            placeholder = { Text("Nhập tên hiển thị...") },
                            trailingIcon = {
                                IconButton(
                                    onClick = { showAvatarPickerDialog = true },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("btn_open_avatar_picker")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = "Tuỳ chỉnh Avatar",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("author_name_input_field"),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        )
                    }

                    // Input bar row: Nội dung bình luận / phản hồi + Nút gửi
                    // Attached photo preview if any
                    if (commentImageUri != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(commentImageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Ảnh đính kèm",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = { commentImageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(20.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Xoá ảnh",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Ảnh đính kèm từ Máy ảnh",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (showVoiceRecorderForComment) {
                        VoiceRecorderDialog(
                            onDismiss = { showVoiceRecorderForComment = false },
                            onResult = { transcript, audioPath, duration ->
                                if (transcript.isNotBlank()) {
                                    if (commentInput.isBlank()) {
                                        commentInput = transcript
                                    } else {
                                        commentInput += " $transcript"
                                    }
                                }
                                if (audioPath != null) {
                                    commentAudioUri = audioPath
                                    commentAudioDuration = duration
                                }
                                showVoiceRecorderForComment = false
                            },
                            titleText = "Ghi âm bình luận"
                        )
                    }

                    if (commentAudioUri != null && commentAudioDuration != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            VoicePlayWidget(
                                audioUri = commentAudioUri!!,
                                durationMs = commentAudioDuration!!,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    commentAudioUri = null
                                    commentAudioDuration = null
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Xoá ghi âm",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            IconButton(
                                onClick = { launchCameraForComment() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("btn_camera_capture_comment")
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PhotoCamera,
                                    contentDescription = "Chụp ảnh đính kèm",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = { showVoiceRecorderForComment = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag("btn_voice_record_comment")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Ghi âm dịch giọng nói",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            placeholder = {
                                Text(
                                    if (replyingTo != null) "Nhập phản hồi cho @${replyingTo?.authorName}..."
                                    else "Nhập bình luận của bạn..."
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(commentFocusRequester)
                                .testTag("comment_input_field"),
                            maxLines = 4,
                            shape = RoundedCornerShape(20.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        )

                        // Send Button
                        val hasInputData = commentInput.isNotBlank() || commentImageUri != null || commentAudioUri != null
                        IconButton(
                            onClick = {
                                if (hasInputData) {
                                    val finalAuthorName = authorNameInput.trim().ifBlank { "Đàm Tường Quân" }
                                    viewModel.addCommentOrReply(
                                        content = commentInput,
                                        authorName = finalAuthorName,
                                        imageUri = commentImageUri,
                                        audioUri = commentAudioUri,
                                        audioDuration = commentAudioDuration
                                    )
                                    commentInput = ""
                                    commentImageUri = null
                                    commentAudioUri = null
                                    commentAudioDuration = null
                                    keyboardController?.hide()
                                }
                            },
                            enabled = hasInputData,
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = if (hasInputData) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = CircleShape
                                )
                                .testTag("send_comment_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Gửi",
                                tint = if (hasInputData) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 600.dp
            val horizontalPadding = if (isWideScreen) ((maxWidth - 840.dp) / 2).coerceAtLeast(24.dp) else 16.dp

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("comments_lazy_column"),
                contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
            // Note Card Section (Tiêu đề ghi chú & Mô tả ghi chú & Ghim ghi chú)
            item(key = "note_card_header") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("detail_note_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (currentNote.isPinned) {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    border = BorderStroke(
                        width = if (currentNote.isPinned) 1.5.dp else 1.dp,
                        color = if (currentNote.isPinned) PinGold.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        // Title & Pinned Status
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Spacer(modifier = Modifier.width(1.dp))

                            if (currentNote.isPinned) {
                                Surface(
                                    shape = CircleShape,
                                    color = PinGoldContainer,
                                    modifier = Modifier.testTag("note_pinned_indicator")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.PushPin,
                                            contentDescription = null,
                                            tint = PinGold,
                                            modifier = Modifier.size(13.dp)
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
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tiêu đề ghi chú
                        Text(
                            text = currentNote.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (currentNote.imageUri != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        activePhotoViewerUri = currentNote.imageUri
                                        activePhotoViewerTitle = currentNote.title
                                    }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(currentNote.imageUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Ảnh ghi chú",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Mô tả ghi chú / Trích dẫn nội dung
                        val descScrollState = rememberScrollState()
                        val isLongDescription = currentNote.description.length > 250
                        val progress = if (descScrollState.maxValue > 0) {
                            descScrollState.value.toFloat() / descScrollState.maxValue
                        } else {
                            1f
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("detail_note_description_box")
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .then(
                                            if (isLongDescription) {
                                                Modifier.heightIn(max = 200.dp)
                                            } else {
                                                Modifier
                                            }
                                        ),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Animated progress quote bar
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(if (isLongDescription) 200.dp else 48.dp)
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(progress)
                                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .then(
                                                if (isLongDescription) Modifier.verticalScroll(descScrollState) else Modifier
                                            )
                                    ) {
                                        Text(
                                            text = currentNote.description.ifBlank { "(Không có mô tả chi tiết)" },
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 24.sp
                                        )
                                    }
                                }

                                // Visual Progress bar & Badge for long descriptions
                                if (isLongDescription) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
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
                                                    imageVector = Icons.Default.Visibility,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp),
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                                Text(
                                                    text = "Đã xem",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            Text(
                                                text = "${(progress * 100).toInt()}%",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        LinearProgressIndicator(
                                            progress = progress,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
                                                .clip(RoundedCornerShape(2.dp)),
                                            color = MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        )
                                    }
                                }
                            }
                        }

                        // Category & Tags Row
                        if (currentNote.category.isNotBlank() || currentNote.tagList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (currentNote.category.isNotBlank()) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.testTag("detail_category_badge")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Folder,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = currentNote.category,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                currentNote.tagList.forEach { tag ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer,
                                        modifier = Modifier.testTag("detail_tag_badge_$tag")
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Text(
                                                text = "#$tag",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Timestamp & Edit Button
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = "Thời gian ghi chú",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = DateUtils.formatDetailedElapsedTime(currentNote.updatedAt),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                                    fontSize = 11.5.sp
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.startEditNote(currentNote) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("edit_note_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Chỉnh sửa ghi chú",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sửa ghi chú")
                                }
                            }
                        }
                    }
                }
            }

            // Comments Section Header
            item(key = "comments_section_header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                                imageVector = Icons.Default.Forum,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Bình luận & Phản hồi",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "${commentsState.totalCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Comment Sort Menu Dropdown Button
                        Box {
                            IconButton(
                                onClick = { commentSortMenuExpanded = true },
                                modifier = Modifier.testTag("btn_sort_comments")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapVert,
                                    contentDescription = "Sắp xếp bình luận",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            DropdownMenu(
                                expanded = commentSortMenuExpanded,
                                onDismissRequest = { commentSortMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Mới nhất trước (Newest)") },
                                    onClick = {
                                        viewModel.setCommentSortOrder(CommentSortOrder.NEWEST_FIRST)
                                        commentSortMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        if (commentSortOrder == CommentSortOrder.NEWEST_FIRST) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Đang chọn",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    },
                                    modifier = Modifier.testTag("menu_sort_comments_newest")
                                )
                                DropdownMenuItem(
                                    text = { Text("Cũ nhất trước (Oldest)") },
                                    onClick = {
                                        viewModel.setCommentSortOrder(CommentSortOrder.OLDEST_FIRST)
                                        commentSortMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        if (commentSortOrder == CommentSortOrder.OLDEST_FIRST) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Đang chọn",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    },
                                    modifier = Modifier.testTag("menu_sort_comments_oldest")
                                )
                                DropdownMenuItem(
                                    text = { Text("Bảng chữ cái A-Z (Alphabetical)") },
                                    onClick = {
                                        viewModel.setCommentSortOrder(CommentSortOrder.ALPHABETICAL)
                                        commentSortMenuExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.SortByAlpha,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        if (commentSortOrder == CommentSortOrder.ALPHABETICAL) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Đang chọn",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    },
                                    modifier = Modifier.testTag("menu_sort_comments_alphabetical")
                                )
                            }
                        }
                    }

                    // Comments Per Page Limit Progress Bar Indicator
                    val commentsCount = commentsState.totalCount
                    val commentsLimit = rewardState.commentsPerPage
                    val commentsProgress = (commentsCount.toFloat() / commentsLimit.toFloat()).coerceIn(0f, 1f)
                    val numberFormatter = NumberFormat.getNumberInstance(Locale.GERMANY)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("comments_per_page_progress_bar")
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
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
                                        imageVector = Icons.Default.Comment,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "Tiến trình Bình luận / Trang:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                Text(
                                    text = "${numberFormatter.format(commentsCount)}/${numberFormatter.format(commentsLimit)} bình luận/trang",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { commentsProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (commentsState.totalCount > 0) {
                            Text(
                                text = "Đang hiển thị ${commentsState.displayedCount}/${commentsState.totalCount} bình luận (${when (commentSortOrder) {
                                    CommentSortOrder.NEWEST_FIRST -> "Mới nhất"
                                    CommentSortOrder.OLDEST_FIRST -> "Cũ nhất"
                                    CommentSortOrder.ALPHABETICAL -> "A-Z"
                                }})",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        } else {
                            Text(
                                text = "Chưa có bình luận nào",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        Text(
                            text = "${commentsState.pageSize} mục/trang",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // CRITICAL REQUIREMENT:
            // "Có thể trả lời 200 bình luận và phản hồi cho mỗi trang sẽ xuất hiện nút Tải bình luận trước."
            // When there are earlier comments exceeding current page of 200:
            if (commentsState.hasEarlierComments) {
                item(key = "load_earlier_comments_btn") {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = { viewModel.loadEarlierComments() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("load_earlier_comments_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardDoubleArrowUp,
                                    contentDescription = "Tải bình luận trước",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Tải bình luận trước (${commentsState.remainingEarlierCount} bình luận cũ hơn)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Mỗi trang hiển thị ${commentsState.pageSize} bình luận & phản hồi theo quy định",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Comments Empty State
            if (commentsState.commentThreads.isEmpty()) {
                item(key = "empty_comments") {
                    FriendlyCommentsEmptyState(
                        onAddCommentClick = { commentFocusRequester.requestFocus() }
                    )
                }
            } else {
                // Render threaded comments (pinned comments first, pinned replies first)
                items(
                    items = commentsState.commentThreads,
                    key = { "thread_${it.comment.id}" }
                ) { thread ->
                    CommentThreadItem(
                        thread = thread,
                        onReplyToComment = { target ->
                            viewModel.setReplyingTo(target)
                        },
                        onTogglePinComment = { target ->
                            viewModel.toggleCommentPinned(target)
                        },
                        onEditComment = { target ->
                            commentToEdit = target
                        },
                        onDeleteComment = { target ->
                            commentToDelete = target
                        },
                        onEditReply = { target ->
                            replyToEdit = target
                        },
                        onDeleteReply = { target ->
                            replyToDelete = target
                        },
                        onToggleBookmarkComment = { target ->
                            viewModel.toggleCommentBookmarked(target)
                        },
                        onToggleArchiveComment = { target ->
                            viewModel.toggleCommentArchived(target)
                        }
                    )
                }
            }
        }
    }
}

    // Confirm Delete Note Dialog
    if (showDeleteNoteConfirm) {
        ConfirmDeleteDialog(
            title = "Chuyển ghi chú vào Thùng rác?",
            message = "Ghi chú \"${currentNote.title}\" sẽ được chuyển vào Thùng rác và lưu trữ trong ${rewardState.trashRetentionDays} ngày trước khi bị xoá vĩnh viễn. Bạn có thể khôi phục lại bất kỳ lúc nào từ Thùng rác.",
            confirmButtonText = "Chuyển vào Thùng rác",
            onConfirm = {
                showDeleteNoteConfirm = false
                viewModel.deleteNote(currentNote.id)
            },
            onDismiss = { showDeleteNoteConfirm = false }
        )
    }

    // Confirm Delete Comment Dialog
    val currentCommentToDelete = commentToDelete
    if (currentCommentToDelete != null) {
        ConfirmDeleteDialog(
            title = "Chuyển bình luận vào Thùng rác?",
            message = "Bình luận này của \"${currentCommentToDelete.authorName}\" và các phản hồi liên quan sẽ được chuyển vào Thùng rác và lưu trữ trong ${rewardState.trashRetentionDays} ngày trước khi bị xoá vĩnh viễn.",
            confirmButtonText = "Chuyển vào Thùng rác",
            onConfirm = {
                viewModel.deleteComment(currentCommentToDelete.id)
                commentToDelete = null
            },
            onDismiss = { commentToDelete = null }
        )
    }

    // Confirm Delete Reply Dialog
    val currentReplyToDelete = replyToDelete
    if (currentReplyToDelete != null) {
        ConfirmDeleteDialog(
            title = "Chuyển phản hồi vào Thùng rác?",
            message = "Phản hồi này của \"${currentReplyToDelete.authorName}\" sẽ được chuyển vào Thùng rác và lưu trữ trong ${rewardState.trashRetentionDays} ngày trước khi bị xoá vĩnh viễn.",
            confirmButtonText = "Chuyển vào Thùng rác",
            onConfirm = {
                viewModel.deleteComment(currentReplyToDelete.id)
                replyToDelete = null
            },
            onDismiss = { replyToDelete = null }
        )
    }

    // Trash & Recent Deleted Dialog
    if (showTrashDialog) {
        TrashManagerDialog(
            trashNotes = trashNotes,
            trashComments = trashComments,
            allNotes = allNotes,
            rewardState = rewardState,
            onRestoreNote = { noteId -> viewModel.restoreNote(noteId) },
            onPermanentlyDeleteNote = { noteId -> viewModel.permanentlyDeleteNote(noteId) },
            onRestoreAllNotes = { viewModel.restoreAllNotes() },
            onEmptyTrashNotes = { viewModel.emptyTrashNotes() },
            onRestoreComment = { commentId -> viewModel.restoreComment(commentId) },
            onPermanentlyDeleteComment = { commentId -> viewModel.permanentlyDeleteComment(commentId) },
            onRestoreAllComments = { viewModel.restoreAllComments() },
            onEmptyTrashComments = { viewModel.emptyTrashComments() },
            onEmptyAllTrash = { viewModel.emptyAllTrash() },
            onOpenStore = { showDiamondStoreDialog = true },
            onDismiss = { showTrashDialog = false }
        )
    }

    // Avatar Picker Dialog for active user
    if (showAvatarPickerDialog) {
        AvatarPickerDialog(
            currentAuthorName = authorNameInput,
            initialAvatarType = userAvatarType,
            initialAvatarValue = userAvatarValue,
            initialAvatarBgColor = userAvatarBgColor,
            onDismiss = { showAvatarPickerDialog = false },
            onSaveAvatar = { type, value, bgColor ->
                viewModel.setUserAvatar(type, value, bgColor)
            }
        )
    }

    // Edit Comment Dialog
    val currentCommentToEdit = commentToEdit
    if (currentCommentToEdit != null) {
        EditCommentDialog(
            comment = currentCommentToEdit,
            isReply = false,
            onDismiss = { commentToEdit = null },
            onSave = { newContent, newAuthor, avatarType, avatarValue, avatarBgColor, imageUri ->
                viewModel.updateComment(
                    currentCommentToEdit.id,
                    newContent,
                    newAuthor,
                    avatarType,
                    avatarValue,
                    avatarBgColor,
                    imageUri
                )
                commentToEdit = null
            }
        )
    }

    // Edit Reply Dialog
    val currentReplyToEdit = replyToEdit
    if (currentReplyToEdit != null) {
        EditCommentDialog(
            comment = currentReplyToEdit,
            isReply = true,
            onDismiss = { replyToEdit = null },
            onSave = { newContent, newAuthor, avatarType, avatarValue, avatarBgColor, imageUri ->
                viewModel.updateComment(
                    currentReplyToEdit.id,
                    newContent,
                    newAuthor,
                    avatarType,
                    avatarValue,
                    avatarBgColor,
                    imageUri
                )
                replyToEdit = null
            }
        )
    }

    // Edit Note Dialog
    if (editingNote != null) {
        NoteEditorDialog(
            initialNote = editingNote,
            availableLabels = allAvailableLabels,
            onDismiss = { viewModel.dismissNoteDialog() },
            onSave = { title, description, isPinned, category, tags, imageUri, audioUri, audioDuration ->
                viewModel.saveNote(title, description, isPinned, category, tags, imageUri, audioUri, audioDuration)
            }
        )
    }

    // Change author name dialog
    if (showAuthorEditDialog) {
        var tempName by remember { mutableStateOf(authorNameInput) }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAuthorEditDialog = false },
            title = { Text("Tên người bình luận") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Tên hiển thị") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (tempName.isNotBlank()) {
                        authorNameInput = tempName.trim()
                    }
                    showAuthorEditDialog = false
                }) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showAuthorEditDialog = false }) {
                    Text("Huỷ")
                }
            }
        )
    }

    // Export single note to Multi-format backup dialog
    if (showExportJsonDialog) {
        MultiFilesImportExportDialog(
            viewModel = viewModel,
            singleNoteToExport = currentNote,
            onDismiss = { showExportJsonDialog = false }
        )
    }

    // Diamond Rewards and Limits Summary Dialog
    if (showDiamondGoalDialog) {
        DiamondGoalDialog(
            rewardState = rewardState,
            onOpenStore = { showDiamondStoreDialog = true },
            onDismiss = { showDiamondGoalDialog = false }
        )
    }

    // Diamond Store (Gói Nạp & Nâng Cấp) Dialog
    if (showDiamondStoreDialog) {
        DiamondStoreDialog(
            rewardState = rewardState,
            onTopUp = { amount -> viewModel.topUpDiamonds(amount) },
            onActivatePackage = { tier -> viewModel.activateStorePackage(tier) },
            onOpenHistory = { showDiamondHistoryDialog = true },
            onDismiss = { showDiamondStoreDialog = false }
        )
    }

    // Diamond Transaction Log Dialog
    if (showDiamondHistoryDialog) {
        DiamondTransactionLogDialog(
            transactions = diamondTransactions,
            rewardState = rewardState,
            onDismiss = { showDiamondHistoryDialog = false }
        )
    }

    // Settings & Auto Daily Backup Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            themeMode = themeMode,
            onSelectThemeMode = { mode -> viewModel.setThemeMode(mode) },
            activityStats = activityStats,
            autoBackupState = autoBackupState,
            onToggleAutoBackup = { enabled -> viewModel.setAutoBackupEnabled(enabled) },
            onManualBackup = { callback -> viewModel.triggerManualBackup(callback) },
            getBackupFiles = { viewModel.getBackupFiles() },
            onDeleteBackupFile = { file -> viewModel.deleteBackupFile(file) },
            onRestoreBackupFile = { file, replaceExisting -> viewModel.restoreFromLocalBackup(file, replaceExisting) },
            onOpenJsonImportExport = { showExportJsonDialog = true },
            onOpenUserGuide = { showUserGuideDialog = true },
            onDismiss = { showSettingsDialog = false }
        )
    }

    // 6-step User Guide Dialog
    if (showUserGuideDialog) {
        UserGuideDialog(
            onDismiss = { showUserGuideDialog = false }
        )
    }

    // Daily Streak Dialog
    if (showStreakDialog) {
        StreakDialog(
            streakState = streakState,
            onCreateNoteClicked = { viewModel.startCreateNote() },
            onDismiss = { showStreakDialog = false }
        )
    }

    // Rate Limit / Reward Warning Alert Dialog
    val warningMsg = rateLimitWarning
    if (warningMsg != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.clearRateLimitWarning() },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            },
            title = {
                Text(
                    text = "Thông báo giới hạn",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = warningMsg,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearRateLimitWarning() },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Đã hiểu")
                }
            }
        )
    }

    if (activePhotoViewerUri != null) {
        PhotoViewerDialog(
            imageUri = activePhotoViewerUri!!,
            title = activePhotoViewerTitle,
            onDismiss = { activePhotoViewerUri = null }
        )
    }
}

@Composable
fun FriendlyCommentsEmptyState(
    onAddCommentClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Multi-layered Speech Bubble Illustration
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background bubble glow
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF81C784).copy(alpha = 0.2f),
                                        Color(0xFF81C784).copy(alpha = 0.0f)
                                    )
                                )
                            )
                        }
                )

                // Overlapping speech bubble
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.Center)
                )

                // A cute heart icon offset to the top-right
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Text(
                text = "Cuộc thảo luận chưa bắt đầu",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Hãy để lại suy nghĩ, phản hồi hoặc câu hỏi đầu tiên của bạn để mở đầu cuộc trò chuyện thú vị!",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                lineHeight = 20.sp,
                modifier = Modifier.widthIn(max = 260.dp)
            )

            Spacer(modifier = Modifier.height(2.dp))

            OutlinedButton(
                onClick = onAddCommentClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Forum, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gửi bình luận đầu tiên", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

