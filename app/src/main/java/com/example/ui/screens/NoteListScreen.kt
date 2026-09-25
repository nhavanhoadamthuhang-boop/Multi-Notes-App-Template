package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.LeaderboardDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawBehind
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.NoteEntity
import com.example.data.repository.ThemeMode
import com.example.ui.NoteSortOrder
import com.example.ui.NotesViewModel
import com.example.ui.components.ActivityTrendCard
import com.example.ui.components.ConfirmDeleteDialog
import com.example.ui.components.DailyCheckInCard
import com.example.ui.components.DiamondGoalDialog
import com.example.ui.components.DiamondProgressCard
import com.example.ui.components.ArchiveManagerDialog
import com.example.ui.components.DiamondStoreDialog
import com.example.ui.components.DiamondTransactionLogDialog
import com.example.ui.components.DiamondTopBarBadge
import com.example.ui.components.ImportPdfUrlDialog
import com.example.ui.components.MultiFilesImportExportDialog
import com.example.ui.components.MainMenuDrawerContent
import com.example.ui.components.ManageLabelsDialog
import com.example.ui.components.NoteCard
import com.example.ui.components.NoteEditorDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StreakBadge
import com.example.ui.components.StreakCalendarProgressBar
import com.example.ui.components.StreakDialog
import com.example.ui.components.TrashManagerDialog
import com.example.ui.components.UserGuideDialog
import com.example.ui.components.SampleDialog
import com.example.ui.theme.PinGold
import kotlinx.coroutines.launch

private val DEFAULT_SUGGESTED_CATEGORIES = listOf(
    "Công việc",
    "Cá nhân",
    "Học tập",
    "Ý tưởng",
    "Tài chính",
    "Dự án",
    "Du lịch Việt Nam",
    "Du lịch nước Nga",
    "Họp báo",
    "aNotepad"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    viewModel: NotesViewModel,
    modifier: Modifier = Modifier,
    isEmbeddedInSplitPane: Boolean = false
) {
    val (pinnedNotes, unpinnedNotes) = viewModel.filteredNotes.collectAsStateWithLifecycle().value
    val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedTag by viewModel.selectedTag.collectAsStateWithLifecycle()
    val onlyBookmarked by viewModel.onlyBookmarked.collectAsStateWithLifecycle()
    val activeCategories by viewModel.activeCategories.collectAsStateWithLifecycle()
    val activeTags by viewModel.activeTags.collectAsStateWithLifecycle()
    val allAvailableLabels by viewModel.allAvailableLabels.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val isCreatingNewNote by viewModel.isCreatingNewNote.collectAsStateWithLifecycle()
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
    val totalTrashCount by viewModel.totalTrashCount.collectAsStateWithLifecycle()
    val noteCommentsSummary by viewModel.noteCommentsSummary.collectAsStateWithLifecycle()
    val archivedNotes by viewModel.archivedNotes.collectAsStateWithLifecycle()
    val archivedComments by viewModel.archivedComments.collectAsStateWithLifecycle()

    var noteToDelete by remember { mutableStateOf<NoteEntity?>(null) }
    var showTrashDialog by remember { mutableStateOf(false) }
    var showArchiveDialog by remember { mutableStateOf(false) }
    var showImportExportDialog by remember { mutableStateOf(false) }
    var showImportPdfUrlDialog by remember { mutableStateOf(false) }
    var showDiamondGoalDialog by remember { mutableStateOf(false) }
    var showDiamondStoreDialog by remember { mutableStateOf(false) }
    var showDiamondHistoryDialog by remember { mutableStateOf(false) }
    var showLeaderboardDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showUserGuideDialog by remember { mutableStateOf(false) }
    var showStreakDialog by remember { mutableStateOf(false) }
    var showManageLabelsDialog by remember { mutableStateOf(false) }
    var showSampleDialog by remember { mutableStateOf(false) }
    var showActivityTrendsCard by remember { mutableStateOf(false) }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var topBarMenuExpanded by remember { mutableStateOf(false) }

    // Merge active categories and default categories for discoverability
    val displayCategories = remember(activeCategories) {
        val merged = (activeCategories + DEFAULT_SUGGESTED_CATEGORIES).distinct()
        merged
    }

    val isAnyFilterActive = searchQuery.isNotBlank() || selectedCategory != null || selectedTag != null
    val totalNotesCount = pinnedNotes.size + unpinnedNotes.size
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val favoriteNotes = remember(allNotes) { allNotes.filter { it.isBookmarked } }
    
    val exportFavoritesPdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                val success = com.example.utils.PdfExportHelper.exportFavoritesToPdf(
                    context, uri, favoriteNotes
                )
                if (success) {
                    Toast.makeText(context, "Đã xuất danh sách Yêu thích ra tệp tin PDF thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lỗi khi xuất tệp tin PDF danh sách Yêu thích", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MainMenuDrawerContent(
                totalNotesCount = totalNotesCount,
                pinnedNotesCount = pinnedNotes.size,
                totalTrashCount = totalTrashCount,
                categories = displayCategories,
                selectedCategory = selectedCategory,
                onSelectCategory = { cat -> viewModel.selectCategory(cat) },
                rewardState = rewardState,
                streakState = streakState,
                themeMode = themeMode,
                isTrendsVisible = showActivityTrendsCard,
                onToggleTrends = { showActivityTrendsCard = !showActivityTrendsCard },
                onToggleTheme = { viewModel.toggleTheme() },
                onOpenCreateNote = { viewModel.startCreateNote() },
                onOpenTrash = { showTrashDialog = true },
                onOpenArchive = { showArchiveDialog = true },
                onOpenStore = { showDiamondStoreDialog = true },
                onOpenLeaderboard = { showLeaderboardDialog = true },
                onOpenStreak = { showStreakDialog = true },
                onClaimDailyReward = { viewModel.claimDailyCheckIn() },
                onOpenManageLabels = { showManageLabelsDialog = true },
                onOpenImportPdfUrl = { showImportPdfUrlDialog = true },
                onOpenJsonBackup = { showImportExportDialog = true },
                onOpenSettings = { showSettingsDialog = true },
                onOpenUserGuide = { showUserGuideDialog = true },
                onOpenSamples = { showSampleDialog = true },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                },
                                modifier = Modifier.testTag("btn_main_menu")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Trình đơn chính (Menu)",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            DiamondTopBarBadge(
                                rewardState = rewardState,
                                onClick = { showDiamondStoreDialog = true },
                                modifier = Modifier.padding(start = 2.dp)
                            )
                        }
                    },
                title = {},
                actions = {
                    // Sorting dropdown menu
                    Box {
                        IconButton(
                            onClick = { sortMenuExpanded = true },
                            modifier = Modifier.testTag("btn_sort_notes")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SwapVert,
                                contentDescription = "Sắp xếp ghi chú theo ngày tạo",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Mới nhất trước (Newest)") },
                                onClick = {
                                    viewModel.setSortOrder(NoteSortOrder.NEWEST_FIRST)
                                    sortMenuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (sortOrder == NoteSortOrder.NEWEST_FIRST) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Đang chọn",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier.testTag("menu_sort_newest_first")
                            )
                            DropdownMenuItem(
                                text = { Text("Cũ nhất trước (Oldest)") },
                                onClick = {
                                    viewModel.setSortOrder(NoteSortOrder.OLDEST_FIRST)
                                    sortMenuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (sortOrder == NoteSortOrder.OLDEST_FIRST) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Đang chọn",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier.testTag("menu_sort_oldest_first")
                            )
                            DropdownMenuItem(
                                text = { Text("Bảng chữ cái A-Z (Alphabetical)") },
                                onClick = {
                                    viewModel.setSortOrder(NoteSortOrder.ALPHABETICAL)
                                    sortMenuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.SortByAlpha,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (sortOrder == NoteSortOrder.ALPHABETICAL) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Đang chọn",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier.testTag("menu_sort_alphabetical")
                            )
                            DropdownMenuItem(
                                text = { Text("Bình luận nhiều nhất (Most Commented)") },
                                onClick = {
                                    viewModel.setSortOrder(NoteSortOrder.MOST_COMMENTED)
                                    sortMenuExpanded = false
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Forum,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (sortOrder == NoteSortOrder.MOST_COMMENTED) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Đang chọn",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier.testTag("menu_sort_most_commented")
                            )
                        }
                    }

                    StreakBadge(
                        streakState = streakState,
                        onClick = { showStreakDialog = true },
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    // Theme Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("btn_toggle_theme")
                    ) {
                        Icon(
                            imageVector = if (themeMode == ThemeMode.DARK) Icons.Default.WbSunny else Icons.Default.DarkMode,
                            contentDescription = if (themeMode == ThemeMode.DARK) "Chuyển sang Chế độ sáng" else "Chuyển sang Chế độ tối",
                            tint = if (themeMode == ThemeMode.DARK) Color(0xFFFFB300) else MaterialTheme.colorScheme.primary
                        )
                    }

                    // Unified Overflow Menu Button (Removes overcrowded icon buttons)
                    Box {
                        IconButton(
                            onClick = { topBarMenuExpanded = true },
                            modifier = Modifier.testTag("btn_top_bar_menu")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Tùy chọn khác"
                            )
                        }

                        DropdownMenu(
                            expanded = topBarMenuExpanded,
                            onDismissRequest = { topBarMenuExpanded = false }
                        ) {
                            // 1. Cửa hàng gói mua
                            DropdownMenuItem(
                                text = { Text("Cửa hàng gói mua") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    showDiamondStoreDialog = true
                                },
                                modifier = Modifier.testTag("menu_item_diamond_store")
                            )

                            // 2. Hướng dẫn sử dụng (8 bước)
                            DropdownMenuItem(
                                text = { Text("Hướng dẫn sử dụng (8 bước)") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    showUserGuideDialog = true
                                },
                                modifier = Modifier.testTag("menu_item_user_guide")
                            )

                            // 3. Thùng rác & Đã xoá gần đây (kèm đếm số lượng)
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (totalTrashCount > 0) "Thùng rác & Đã xoá ($totalTrashCount)" else "Thùng rác & Đã xoá gần đây"
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = null,
                                        tint = if (totalTrashCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    showTrashDialog = true
                                },
                                modifier = Modifier.testTag("menu_item_trash")
                            )

                            // 4. Nhập từ PDF & URL
                            DropdownMenuItem(
                                text = { Text("Nhập từ tệp PDF & URL") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    showImportPdfUrlDialog = true
                                },
                                modifier = Modifier.testTag("menu_item_import_pdf_url")
                            )

                            // 5. Bật/tắt biểu đồ xu hướng hoạt động
                            DropdownMenuItem(
                                text = {
                                    Text(if (showActivityTrendsCard) "Tắt biểu đồ xu hướng" else "Bật biểu đồ xu hướng hoạt động")
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.tertiary
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    showActivityTrendsCard = !showActivityTrendsCard
                                },
                                modifier = Modifier.testTag("menu_item_activity_trends")
                            )

                            // 5. Nhập/xuất tệp tin đa định dạng
                            DropdownMenuItem(
                                text = { Text("Nhập/xuất tệp tin đa định dạng") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.DataObject,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    showImportExportDialog = true
                                },
                                modifier = Modifier.testTag("menu_item_json_backup")
                            )

                            // Xuất các Ghi chú Yêu thích ra PDF
                            DropdownMenuItem(
                                text = { Text("Xuất Yêu thích ra tệp tin PDF") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = PinGold
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    if (favoriteNotes.isEmpty()) {
                                        Toast.makeText(context, "Không có ghi chú yêu thích nào để xuất!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        exportFavoritesPdfLauncher.launch("Favorite_Notes_Diary.pdf")
                                    }
                                },
                                modifier = Modifier.testTag("menu_item_export_favorites_pdf")
                            )

                            // 6. Cài đặt hệ thống
                            DropdownMenuItem(
                                text = { Text("Cài đặt hệ thống") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    topBarMenuExpanded = false
                                    showSettingsDialog = true
                                },
                                modifier = Modifier.testTag("menu_item_settings")
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.startCreateNote() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("add_note_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tạo ghi chú mới")
                    Text("Tạo ghi chú", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isMultiColumn = maxWidth >= 520.dp && !isEmbeddedInSplitPane
            val gridColumns = if (isMultiColumn) GridCells.Adaptive(minSize = 280.dp) else GridCells.Fixed(1)

            LazyVerticalGrid(
                columns = gridColumns,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("notes_list"),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 1. Search Box Header Item
                item(key = "hdr_search", span = { GridItemSpan(maxLineSpan) }) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.updateSearchQuery(it) },
                            placeholder = {
                                Text(
                                    "Tìm theo tiêu đề, mô tả, thẻ #nhãn...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Tìm kiếm ghi chú",
                                    tint = if (searchQuery.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(
                                        onClick = { viewModel.clearSearchQuery() },
                                        modifier = Modifier.testTag("clear_search_button")
                                    ) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Xoá nội dung tìm kiếm",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_note_input"),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                // 2. Daily Streak Banner
                item(key = "hdr_streak", span = { GridItemSpan(maxLineSpan) }) {
                    StreakCalendarProgressBar(
                        streakState = streakState,
                        onClick = { showStreakDialog = true }
                    )
                }

                // 3. Diamond Reward Progress Card
                item(key = "hdr_diamond", span = { GridItemSpan(maxLineSpan) }) {
                    DiamondProgressCard(
                        rewardState = rewardState,
                        onClick = { showDiamondGoalDialog = true },
                        onOpenStore = { showDiamondStoreDialog = true },
                        onOpenHistory = { showDiamondHistoryDialog = true },
                        onOpenLeaderboard = { showLeaderboardDialog = true },
                        onClaimCheckIn = { viewModel.claimDailyCheckIn() }
                    )
                }

                // 4. Activity Trends Line Chart Banner
                item(key = "hdr_trends", span = { GridItemSpan(maxLineSpan) }) {
                    AnimatedVisibility(
                        visible = showActivityTrendsCard,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        ActivityTrendCard(
                            stats = activityStats
                        )
                    }
                }

                // 5. Category Filter Chips Row
                item(key = "hdr_categories", span = { GridItemSpan(maxLineSpan) }) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .testTag("category_filter_row"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Quick Sort Order Toggle Chip
                        item(key = "sort_order_chip") {
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.toggleSortOrder() },
                                leadingIcon = {
                                    Icon(
                                        imageVector = when (sortOrder) {
                                            NoteSortOrder.NEWEST_FIRST -> Icons.Default.ArrowDownward
                                            NoteSortOrder.OLDEST_FIRST -> Icons.Default.ArrowUpward
                                            NoteSortOrder.ALPHABETICAL -> Icons.Default.SortByAlpha
                                            NoteSortOrder.MOST_COMMENTED -> Icons.Default.Forum
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = when (sortOrder) {
                                            NoteSortOrder.NEWEST_FIRST -> "Mới nhất"
                                            NoteSortOrder.OLDEST_FIRST -> "Cũ nhất"
                                            NoteSortOrder.ALPHABETICAL -> "A-Z"
                                            NoteSortOrder.MOST_COMMENTED -> "Nhiều bình luận"
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("filter_chip_sort_toggle"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        // "Tất cả" Chip
                        item(key = "category_all") {
                            val isAllSelected = selectedCategory == null && !onlyBookmarked
                            FilterChip(
                                selected = isAllSelected,
                                onClick = {
                                    viewModel.clearCategoryFilter()
                                    if (onlyBookmarked) viewModel.toggleOnlyBookmarked()
                                },
                                label = { Text("Tất cả (${allNotes.size})") },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("filter_chip_all_categories"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }

                        // "Yêu thích" Chip
                        item(key = "only_bookmarked_chip") {
                            val bookmarkedCount = allNotes.count { it.isBookmarked }
                            FilterChip(
                                selected = onlyBookmarked,
                                onClick = { viewModel.toggleOnlyBookmarked() },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp),
                                        tint = if (onlyBookmarked) PinGold else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                label = { Text("Yêu thích ($bookmarkedCount)") },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("filter_chip_only_bookmarked"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }

                        // Category Chips
                        items(displayCategories, key = { "cat_$it" }) { cat ->
                            val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                            val count = allNotes.count { it.category.equals(cat, ignoreCase = true) }
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectCategory(cat) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Folder,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = {
                                    Text(if (count > 0) "$cat ($count)" else cat)
                                },
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.testTag("filter_chip_category_$cat"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }

                // 6. Tag Filter Chips Row
                val displayLabels = (allAvailableLabels + activeTags).map { it.trim().removePrefix("#") }.filter { it.isNotBlank() }.distinctBy { it.lowercase() }
                if (displayLabels.isNotEmpty()) {
                    item(key = "hdr_tags", span = { GridItemSpan(maxLineSpan) }) {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tag_filter_row"),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item(key = "action_manage_labels") {
                                SuggestionChip(
                                    onClick = { showManageLabelsDialog = true },
                                    label = { Text("+ Quản lý nhãn", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Label,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.testTag("chip_manage_labels")
                                )
                            }
                            items(displayLabels, key = { "tag_$it" }) { tag ->
                                val isSelected = selectedTag.equals(tag, ignoreCase = true)
                                val count = allNotes.count { note -> note.tagList.any { it.equals(tag, ignoreCase = true) } }
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.selectTag(tag) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.Label,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    },
                                    label = {
                                        Text("#$tag ($count)", fontSize = 12.sp)
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.testTag("filter_chip_tag_$tag"),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                )
                            }
                        }
                    }
                }

                // 7. Active Filter Status Banner
                if (isAnyFilterActive) {
                    item(key = "hdr_filter_banner", span = { GridItemSpan(maxLineSpan) }) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    val filterSummary = buildString {
                                        append("Đang lọc: $totalNotesCount kết quả")
                                        if (selectedCategory != null) append(" • Thư mục: '$selectedCategory'")
                                        if (selectedTag != null) append(" • Thẻ: '#$selectedTag'")
                                        if (searchQuery.isNotBlank()) append(" • Từ khóa: '$searchQuery'")
                                    }
                                    Text(
                                        text = filterSummary,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = if (totalNotesCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                        maxLines = 1
                                    )
                                }

                                TextButton(
                                    onClick = { viewModel.clearAllFilters() },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                    modifier = Modifier.testTag("clear_all_filters_button")
                                ) {
                                    Text("Xoá bộ lọc", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }

                // 8. Empty State OR Notes List
                if (totalNotesCount == 0) {
                    item(key = "hdr_empty_state", span = { GridItemSpan(maxLineSpan) }) {
                        FriendlyNotesEmptyState(
                            isFilterActive = isAnyFilterActive,
                            onActionClick = { viewModel.startCreateNote() },
                            onClearFilters = { viewModel.clearAllFilters() },
                            searchQuery = searchQuery,
                            selectedCategory = selectedCategory,
                            selectedTag = selectedTag
                        )
                    }
                } else {
                    // Pinned Section Header & Notes
                    if (pinnedNotes.isNotEmpty()) {
                        item(key = "pinned_section_header", span = { GridItemSpan(maxLineSpan) }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = null,
                                    tint = PinGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "ĐÃ GHIM (${pinnedNotes.size})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PinGold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        items(pinnedNotes, key = { "pinned_${it.id}" }) { note ->
                            val summary = noteCommentsSummary[note.id]
                            NoteCard(
                                note = note,
                                commentCount = summary?.count ?: 0,
                                latestComment = summary?.latestComment,
                                onClick = { viewModel.selectNote(note.id) },
                                onTogglePin = { viewModel.toggleNotePinned(note) },
                                onToggleBookmark = { viewModel.toggleNoteBookmarked(note) },
                                onToggleCompleted = { viewModel.toggleNoteCompleted(note) },
                                onToggleArchived = { viewModel.toggleNoteArchived(note) },
                                onEdit = { viewModel.startEditNote(note) },
                                onDelete = { noteToDelete = note },
                                onCategoryClick = { viewModel.selectCategory(it) },
                                onTagClick = { viewModel.selectTag(it) }
                            )
                        }
                    }

                    // Unpinned Section Header & Notes
                    if (unpinnedNotes.isNotEmpty()) {
                        item(key = "unpinned_section_header", span = { GridItemSpan(maxLineSpan) }) {
                            val headerTitle = if (pinnedNotes.isNotEmpty()) "GHI CHÚ KHÁC" else "TẤT CẢ GHI CHÚ"
                            Text(
                                text = "$headerTitle (${unpinnedNotes.size})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                            )
                        }

                        items(unpinnedNotes, key = { "unpinned_${it.id}" }) { note ->
                            val summary = noteCommentsSummary[note.id]
                            NoteCard(
                                note = note,
                                commentCount = summary?.count ?: 0,
                                latestComment = summary?.latestComment,
                                onClick = { viewModel.selectNote(note.id) },
                                onTogglePin = { viewModel.toggleNotePinned(note) },
                                onToggleBookmark = { viewModel.toggleNoteBookmarked(note) },
                                onToggleCompleted = { viewModel.toggleNoteCompleted(note) },
                                onToggleArchived = { viewModel.toggleNoteArchived(note) },
                                onEdit = { viewModel.startEditNote(note) },
                                onDelete = { noteToDelete = note },
                                onCategoryClick = { viewModel.selectCategory(it) },
                                onTagClick = { viewModel.selectTag(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirm Delete Note Dialog
    val currentNoteToDelete = noteToDelete
    if (currentNoteToDelete != null) {
        ConfirmDeleteDialog(
            title = "Chuyển ghi chú vào Thùng rác?",
            message = "Ghi chú \"${currentNoteToDelete.title}\" sẽ được chuyển vào Thùng rác và lưu trữ trong ${rewardState.trashRetentionDays} ngày trước khi bị xoá vĩnh viễn. Bạn có thể khôi phục lại bất kỳ lúc nào từ Thùng rác.",
            confirmButtonText = "Chuyển vào Thùng rác",
            onConfirm = {
                viewModel.deleteNote(currentNoteToDelete.id)
                noteToDelete = null
            },
            onDismiss = { noteToDelete = null }
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

    // Archive Manager Dialog
    if (showArchiveDialog) {
        ArchiveManagerDialog(
            archivedNotes = archivedNotes,
            archivedComments = archivedComments,
            onDismiss = { showArchiveDialog = false },
            onRestoreNote = { note -> viewModel.toggleNoteArchived(note) },
            onDeleteNote = { note -> viewModel.deleteNote(note.id) },
            onToggleCompletedNote = { note -> viewModel.toggleNoteCompleted(note) },
            onBulkArchiveNotes = { viewModel.archiveCompletedOrOlderNotes() },
            onRestoreComment = { comment -> viewModel.toggleCommentArchived(comment) },
            onDeleteComment = { comment -> viewModel.deleteComment(comment.id) }
        )
    }

    // Dialog for creating or editing notes
    if (isCreatingNewNote || editingNote != null) {
        NoteEditorDialog(
            initialNote = editingNote,
            availableLabels = allAvailableLabels,
            onDismiss = { viewModel.dismissNoteDialog() },
            onSave = { title, description, isPinned, category, tags, imageUri, audioUri, audioDuration ->
                viewModel.saveNote(title, description, isPinned, category, tags, imageUri, audioUri, audioDuration)
            }
        )
    }

    // Dialog for managing custom labels
    if (showManageLabelsDialog) {
        ManageLabelsDialog(
            labels = allAvailableLabels,
            allNotes = allNotes,
            selectedTag = selectedTag,
            onCreateLabel = { name -> viewModel.createCustomLabel(name) },
            onDeleteLabel = { name -> viewModel.deleteCustomLabel(name) },
            onSelectTag = { tag -> viewModel.selectTag(tag) },
            onDismiss = { showManageLabelsDialog = false }
        )
    }

    // Multi-format Import/Export Backup Dialog
    if (showImportExportDialog) {
        MultiFilesImportExportDialog(
            viewModel = viewModel,
            onDismiss = { showImportExportDialog = false }
        )
    }

    // PDF & URL Import Dialog
    if (showImportPdfUrlDialog) {
        ImportPdfUrlDialog(
            viewModel = viewModel,
            onDismiss = { showImportPdfUrlDialog = false }
        )
    }

    // Diamond Rewards and Limits Summary Dialog
    if (showDiamondGoalDialog) {
        DiamondGoalDialog(
            rewardState = rewardState,
            onOpenStore = { showDiamondStoreDialog = true },
            onClaimCheckIn = { viewModel.claimDailyCheckIn() },
            onRedeemContent = {
                coroutineScope.launch {
                    viewModel.redeemAllContentForDiamonds()
                }
            },
            onDismiss = { showDiamondGoalDialog = false }
        )
    }

    // Diamond Store (Gói Nạp & Nâng Cấp) Dialog
    if (showDiamondStoreDialog) {
        DiamondStoreDialog(
            rewardState = rewardState,
            onTopUp = { amount -> viewModel.topUpDiamonds(amount) },
            onActivatePackage = { tier -> viewModel.activateStorePackage(tier) },
            onClaimCheckIn = { viewModel.claimDailyCheckIn() },
            onOpenHistory = { showDiamondHistoryDialog = true },
            onRedeemContent = {
                coroutineScope.launch {
                    viewModel.redeemAllContentForDiamonds()
                }
            },
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

    // Leaderboard Dialog
    if (showLeaderboardDialog) {
        LeaderboardDialog(
            rewardState = rewardState,
            streakState = streakState,
            onDismiss = { showLeaderboardDialog = false },
            onOpenStore = {
                showLeaderboardDialog = false
                showDiamondStoreDialog = true
            }
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
            onOpenJsonImportExport = { showImportExportDialog = true },
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
            onClaimDailyCheckIn = { viewModel.claimDailyCheckIn() },
            hasCheckedInToday = rewardState.hasCheckedInToday,
            onDismiss = { showStreakDialog = false }
        )
    }

    // Sample Dialog
    if (showSampleDialog) {
        SampleDialog(
            onImportNote = { title, content, category, tags ->
                viewModel.importSampleNote(title, content, category, tags)
            },
            onDismiss = { showSampleDialog = false }
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
                    text = "Thông báo",
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
}
}

@Composable
fun FriendlyNotesEmptyState(
    isFilterActive: Boolean,
    onActionClick: () -> Unit,
    onClearFilters: () -> Unit,
    searchQuery: String,
    selectedCategory: String?,
    selectedTag: String?
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariantColor = MaterialTheme.colorScheme.onSurfaceVariant
    val density = androidx.compose.ui.platform.LocalDensity.current
    val px4 = with(density) { 4.dp.toPx() }
    val px3 = with(density) { 3.dp.toPx() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Multi-layered Illustration of Overlapping Papers
        Box(
            modifier = Modifier
                .size(160.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            // Background glow/blob
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD54F).copy(alpha = 0.25f),
                                    Color(0xFFFFD54F).copy(alpha = 0.0f)
                                )
                            )
                        )
                    }
            )

            // Overlapping Note Cards
            // Card 1 (Bottom, tilted right)
            Surface(
                modifier = Modifier
                    .rotate(15f)
                    .size(width = 75.dp, height = 95.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {}

            // Card 2 (Middle, tilted left)
            Surface(
                modifier = Modifier
                    .rotate(-10f)
                    .size(width = 80.dp, height = 100.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f))
            ) {}

            // Card 3 (Top, straight with content)
            Surface(
                modifier = Modifier
                    .size(85.dp, 105.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Title line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(6.dp)
                            .drawBehind {
                                drawRoundRect(
                                    color = primaryColor.copy(alpha = 0.7f),
                                    cornerRadius = CornerRadius(px4)
                                )
                            }
                    )
                    // Content lines
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(if (index == 2) 0.5f else 0.9f)
                                .height(4.dp)
                                .drawBehind {
                                    drawRoundRect(
                                        color = onSurfaceVariantColor.copy(alpha = 0.3f),
                                        cornerRadius = CornerRadius(px3)
                                    )
                                }
                        )
                    }
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Small decorative icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            imageVector = if (isFilterActive) Icons.Default.Search else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isFilterActive) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Title text
        Text(
            text = if (isFilterActive) "Không tìm thấy ghi chú phù hợp" else "Bàn làm việc của bạn đang trống",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        // Description text
        val emptyMessage = when {
            selectedCategory != null && selectedTag != null ->
                "Không có ghi chú nào trong thư mục \"$selectedCategory\" có gắn thẻ \"#$selectedTag\"."
            selectedCategory != null ->
                "Chưa có ghi chú nào trong thư mục \"$selectedCategory\"."
            selectedTag != null ->
                "Chưa có ghi chú nào được gắn thẻ \"#$selectedTag\"."
            searchQuery.isNotBlank() ->
                "Không tìm thấy ghi chú nào khớp với từ khóa \"$searchQuery\"."
            else ->
                "Hãy gieo một ý tưởng nhỏ! Hãy bấm nút bên dưới để viết những ghi chép, kế hoạch, hoặc chia sẻ suy nghĩ đầu tiên của bạn."
        }

        Text(
            text = emptyMessage,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            lineHeight = 22.sp,
            modifier = Modifier.widthIn(max = 280.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (!isFilterActive) {
            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_empty_create_note")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bắt đầu ghi chú")
            }
        } else {
            OutlinedButton(
                onClick = onClearFilters,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_reset_all_filters")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Xóa bộ lọc")
            }
        }
    }
}

