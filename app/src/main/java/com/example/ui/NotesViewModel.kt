package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.backup.BackupData
import com.example.data.backup.BackupNote
import com.example.data.backup.JsonBackupHelper
import com.example.data.local.AppDatabase
import com.example.data.local.CommentEntity
import com.example.data.local.NoteEntity
import com.example.data.repository.AutoBackupManager
import com.example.data.repository.AutoBackupState
import com.example.data.repository.BackupFileInfo
import com.example.data.repository.DiamondRewardManager
import com.example.data.repository.DiamondRewardState
import com.example.data.repository.DiamondTransaction
import com.example.data.repository.ImportSummary
import com.example.data.repository.NoteRepository
import com.example.data.repository.StorePackageTier
import com.example.data.repository.StreakManager
import com.example.data.repository.StreakState
import com.example.data.repository.ThemeManager
import com.example.data.repository.ThemeMode
import com.example.ui.util.ActivityStats
import com.example.ui.util.ActivityStatsCalculator
import com.example.ui.util.SearchUtils
import com.example.utils.PdfImportHelper
import com.example.utils.PdfParsedData
import com.example.utils.UrlImportHelper
import com.example.utils.UrlParsedNoteData
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CommentWithReplies(
    val comment: CommentEntity,
    val replies: List<CommentEntity>
)

data class NoteCommentsSummary(
    val count: Int = 0,
    val latestComment: CommentEntity? = null
)

data class CommentsUiState(
    val totalCount: Int = 0,
    val displayedCount: Int = 0,
    val hasEarlierComments: Boolean = false,
    val remainingEarlierCount: Int = 0,
    val commentThreads: List<CommentWithReplies> = emptyList(),
    val pageSize: Int = 200
)

enum class NoteSortOrder(val displayName: String) {
    NEWEST_FIRST("Mới nhất trước (Newest)"),
    OLDEST_FIRST("Cũ nhất trước (Oldest)"),
    ALPHABETICAL("Bảng chữ cái A-Z (Alphabetical)"),
    MOST_COMMENTED("Bình luận nhiều nhất (Most Commented)")
}

enum class CommentSortOrder(val displayName: String) {
    NEWEST_FIRST("Mới nhất trước (Newest)"),
    OLDEST_FIRST("Cũ nhất trước (Oldest)"),
    ALPHABETICAL("Bảng chữ cái A-Z (Alphabetical)")
}

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository
    private val rewardManager: DiamondRewardManager = DiamondRewardManager(application)
    private val autoBackupManager: AutoBackupManager = AutoBackupManager(application)
    private val streakManager: StreakManager = StreakManager(application)
    private val themeManager: ThemeManager = ThemeManager(application)

    val rewardState: StateFlow<DiamondRewardState> = rewardManager.rewardState
    val diamondTransactions: StateFlow<List<DiamondTransaction>> = rewardManager.transactions
    val autoBackupState: StateFlow<AutoBackupState> = autoBackupManager.backupState
    val streakState: StateFlow<StreakState> = streakManager.streakState
    val themeMode: StateFlow<ThemeMode> = themeManager.themeMode

    fun setThemeMode(mode: ThemeMode) {
        themeManager.setThemeMode(mode)
    }

    fun toggleTheme() {
        themeManager.toggleLightDark()
    }

    init {
        val db = AppDatabase.getDatabase(application)
        repository = NoteRepository(db.noteDao(), db.commentDao())
        viewModelScope.launch {
            autoBackupManager.checkAndRunDailyBackup(repository)
            repository.purgeExpiredTrash(rewardState.value.trashRetentionDays)
        }
        recordInteraction()
    }

    fun recordInteraction() {
        streakManager.recordInteraction()
    }

    // Status message for Snackbars/Dialogs
    private val _importExportMessage = MutableStateFlow<String?>(null)
    val importExportMessage: StateFlow<String?> = _importExportMessage.asStateFlow()

    fun setImportExportMessage(message: String?) {
        _importExportMessage.value = message
    }

    fun clearImportExportMessage() {
        _importExportMessage.value = null
    }

    // Warning message for Rate Limits & Diamond celebrations
    private val _rateLimitWarning = MutableStateFlow<String?>(null)
    val rateLimitWarning: StateFlow<String?> = _rateLimitWarning.asStateFlow()

    fun clearRateLimitWarning() {
        _rateLimitWarning.value = null
    }

    fun setRateLimitWarning(msg: String?) {
        _rateLimitWarning.value = msg
    }

    // Sort order for notes
    val sortOrder = MutableStateFlow(NoteSortOrder.NEWEST_FIRST)

    fun setSortOrder(order: NoteSortOrder) {
        sortOrder.value = order
    }

    fun toggleSortOrder() {
        sortOrder.value = when (sortOrder.value) {
            NoteSortOrder.NEWEST_FIRST -> NoteSortOrder.OLDEST_FIRST
            NoteSortOrder.OLDEST_FIRST -> NoteSortOrder.ALPHABETICAL
            NoteSortOrder.ALPHABETICAL -> NoteSortOrder.MOST_COMMENTED
            NoteSortOrder.MOST_COMMENTED -> NoteSortOrder.NEWEST_FIRST
        }
    }

    // Sort order for comments
    val commentSortOrder = MutableStateFlow(CommentSortOrder.OLDEST_FIRST)

    fun setCommentSortOrder(order: CommentSortOrder) {
        commentSortOrder.value = order
    }

    fun toggleCommentSortOrder() {
        commentSortOrder.value = when (commentSortOrder.value) {
            CommentSortOrder.OLDEST_FIRST -> CommentSortOrder.NEWEST_FIRST
            CommentSortOrder.NEWEST_FIRST -> CommentSortOrder.ALPHABETICAL
            CommentSortOrder.ALPHABETICAL -> CommentSortOrder.OLDEST_FIRST
        }
    }

    // Search query for notes
    val searchQuery = MutableStateFlow("")

    // Selected category/folder filter (null means All)
    val selectedCategory = MutableStateFlow<String?>(null)

    // Selected tag/label filter (null means All)
    val selectedTag = MutableStateFlow<String?>(null)

    // Filter by bookmarked notes only
    val onlyBookmarked = MutableStateFlow(false)

    fun toggleOnlyBookmarked() {
        onlyBookmarked.value = !onlyBookmarked.value
    }

    fun setOnlyBookmarked(bookmarked: Boolean) {
        onlyBookmarked.value = bookmarked
    }

    // Trash flows
    val trashNotes: StateFlow<List<NoteEntity>> = repository.trashNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashComments: StateFlow<List<CommentEntity>> = repository.trashComments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val trashNotesCount: StateFlow<Int> = repository.trashNotesCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val trashCommentsCount: StateFlow<Int> = repository.trashCommentsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalTrashCount: StateFlow<Int> = combine(trashNotesCount, trashCommentsCount) { n, c -> n + c }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // All notes from repository
    val allNotes: StateFlow<List<NoteEntity>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All comments from repository
    val allComments: StateFlow<List<CommentEntity>> = repository.allComments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Summary of comments per note (count and latest/pinned comment preview)
    val noteCommentsSummary: StateFlow<Map<Long, NoteCommentsSummary>> = allComments.map { comments ->
        val activeComments = comments.filter { !it.isDeleted }
        val grouped = activeComments.groupBy { it.noteId }
        grouped.mapValues { (_, list) ->
            val pinned = list.firstOrNull { it.isPinned }
            val latest = pinned ?: list.maxByOrNull { it.createdAt }
            NoteCommentsSummary(
                count = list.size,
                latestComment = latest
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Calculated note and comment activity trend statistics
    val activityStats: StateFlow<ActivityStats> = combine(allNotes, allComments) { notes, comments ->
        ActivityStatsCalculator.computeStats(notes, comments)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ActivityStatsCalculator.computeStats(emptyList()))

    // All active categories across notes
    val activeCategories: StateFlow<List<String>> = allNotes.combine(flowOf(Unit)) { notes, _ ->
        notes.map { it.category.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val labelPrefs = application.getSharedPreferences("custom_labels_prefs", Context.MODE_PRIVATE)

    private val _customLabels = MutableStateFlow<List<String>>(loadCustomLabels())
    val customLabels: StateFlow<List<String>> = _customLabels.asStateFlow()

    private fun loadCustomLabels(): List<String> {
        val saved = labelPrefs.getStringSet("key_created_labels", null)
        if (saved != null && saved.isNotEmpty()) {
            // Also append new defaults if not already present
            val defaultList = listOf(
                "Quan trọng", "Công việc", "Cá nhân", "Khẩn cấp", "Học tập", 
                "Ý tưởng", "Tài chính", "Cần làm việc", "Dự án", "Tài liệu", 
                "Họp báo", "Du lịch Việt Nam", "Du lịch nước Nga", "aNotepad"
            )
            val updated = (saved + defaultList).toSet()
            saveCustomLabels(updated.toList())
            return updated.toList().sorted()
        }
        val defaults = listOf(
            "Quan trọng", "Công việc", "Cá nhân", "Khẩn cấp", "Học tập", 
            "Ý tưởng", "Tài chính", "Cần làm việc", "Dự án", "Tài liệu", 
            "Họp báo", "Du lịch Việt Nam", "Du lịch nước Nga", "aNotepad"
        )
        saveCustomLabels(defaults)
        return defaults.sorted()
    }

    private fun saveCustomLabels(labels: List<String>) {
        labelPrefs.edit().putStringSet("key_created_labels", labels.toSet()).apply()
    }

    fun createCustomLabel(labelName: String): Boolean {
        val trimmed = labelName.trim().removePrefix("#")
        if (trimmed.isBlank()) return false
        val current = _customLabels.value.toMutableList()
        if (!current.any { it.equals(trimmed, ignoreCase = true) }) {
            current.add(trimmed)
            current.sort()
            _customLabels.value = current
            saveCustomLabels(current)
        }
        return true
    }

    fun deleteCustomLabel(labelName: String) {
        val current = _customLabels.value.toMutableList()
        val removed = current.removeAll { it.equals(labelName, ignoreCase = true) }
        if (removed) {
            _customLabels.value = current
            saveCustomLabels(current)
        }
    }

    // All active tags across notes
    val activeTags: StateFlow<List<String>> = allNotes.combine(flowOf(Unit)) { notes, _ ->
        notes.flatMap { it.tagList }
            .distinct()
            .sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All available labels (combining created custom labels and active tags from notes)
    val allAvailableLabels: StateFlow<List<String>> = combine(customLabels, activeTags) { custom, active ->
        (custom + active).map { it.trim().removePrefix("#") }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sorted()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Archived notes flow
    val archivedNotes: StateFlow<List<NoteEntity>> = allNotes.map { notes ->
        notes.filter { it.isArchived }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val archivedComments: StateFlow<List<CommentEntity>> = repository.archivedComments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered notes (separated into pinned and unpinned, sorted according to sortOrder)
    val filteredNotes = combine(
        allNotes,
        noteCommentsSummary,
        combine(searchQuery, selectedCategory, selectedTag, onlyBookmarked, sortOrder) { query, category, tag, bookmarkedOnly, sort ->
            NoteFilterState(query, category, tag, bookmarkedOnly, sort)
        }
    ) { notes, commentsSummary, filter ->
        val query = filter.query
        val category = filter.category
        val tag = filter.tag
        val bookmarkedOnly = filter.bookmarkedOnly
        val sort = filter.sort

        val filtered = notes.filter { note ->
            // Exclude archived notes from the main list view
            if (note.isArchived) return@filter false

            // Bookmarked filter
            val matchesBookmarked = !bookmarkedOnly || note.isBookmarked

            // Category filter
            val matchesCategory = category.isNullOrBlank() || note.category.equals(category, ignoreCase = true)

            // Tag filter
            val matchesTag = tag.isNullOrBlank() || note.tagList.any { it.equals(tag, ignoreCase = true) }

            // Search query filter
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                SearchUtils.matches(note.title, query) ||
                SearchUtils.matches(note.description, query) ||
                SearchUtils.matches(note.category, query) ||
                note.tagList.any { SearchUtils.matches(it, query) }
            }

            matchesBookmarked && matchesCategory && matchesTag && matchesQuery
        }

        val sorted = when (sort) {
            NoteSortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.createdAt }
            NoteSortOrder.OLDEST_FIRST -> filtered.sortedBy { it.createdAt }
            NoteSortOrder.ALPHABETICAL -> filtered.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.title.trim() })
            NoteSortOrder.MOST_COMMENTED -> filtered.sortedByDescending { commentsSummary[it.id]?.count ?: 0 }
        }

        val pinned = sorted.filter { it.isPinned }
        val unpinned = sorted.filter { !it.isPinned }
        Pair(pinned, unpinned)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(emptyList(), emptyList()))

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun clearSearchQuery() {
        searchQuery.value = ""
    }

    fun selectCategory(category: String?) {
        selectedCategory.value = if (selectedCategory.value == category) null else category
    }

    fun selectTag(tag: String?) {
        selectedTag.value = if (selectedTag.value == tag) null else tag
    }

    fun clearCategoryFilter() {
        selectedCategory.value = null
    }

    fun clearTagFilter() {
        selectedTag.value = null
    }

    fun clearAllFilters() {
        selectedCategory.value = null
        selectedTag.value = null
        searchQuery.value = ""
    }

    // Selected Note for detail view
    private val _selectedNoteId = MutableStateFlow<Long?>(null)
    val selectedNoteId: StateFlow<Long?> = _selectedNoteId.asStateFlow()

    val selectedNote: StateFlow<NoteEntity?> = _selectedNoteId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getNote(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current page display limit for comments & replies (default 200, dynamically updated based on active tier)
    private val _visibleCommentsLimit = MutableStateFlow(200)
    val visibleCommentsLimit: StateFlow<Int> = _visibleCommentsLimit.asStateFlow()

    // Comments & Replies UI state with dynamic page sizing and sorting
    val commentsUiState: StateFlow<CommentsUiState> = combine(
        _selectedNoteId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.getComments(id)
        },
        _visibleCommentsLimit,
        commentSortOrder,
        rewardState.map { it.currentTier }
    ) { allItems, limit, sort, tier ->
        val tierPageSize = tier.commentsPerPage
        val totalCount = allItems.size
        // Determine window of items to display:
        // By default, showing up to 'limit' items (e.g. the most recent tierPageSize items).
        // If totalCount > limit, earlier comments can be loaded via "Tải bình luận trước"
        val hasEarlier = totalCount > limit
        val displayedItems = if (hasEarlier) {
            allItems.takeLast(limit)
        } else {
            allItems
        }
        val remainingEarlier = if (hasEarlier) totalCount - limit else 0

        // Separate into root comments and replies
        val roots = displayedItems.filter { it.parentId == null }
        val replies = displayedItems.filter { it.parentId != null }

        // Group replies by parentId
        val repliesByParent = replies.groupBy { it.parentId }

        // Build comparators respecting sort order while keeping pinned and bookmarked at the top
        val rootComparator = when (sort) {
            CommentSortOrder.NEWEST_FIRST -> compareByDescending<CommentEntity> { it.isPinned }.thenByDescending { it.isBookmarked }.thenByDescending { it.createdAt }
            CommentSortOrder.OLDEST_FIRST -> compareByDescending<CommentEntity> { it.isPinned }.thenByDescending { it.isBookmarked }.thenBy { it.createdAt }
            CommentSortOrder.ALPHABETICAL -> compareByDescending<CommentEntity> { it.isPinned }.thenByDescending { it.isBookmarked }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.content.trim() }
        }

        val replyComparator = when (sort) {
            CommentSortOrder.NEWEST_FIRST -> compareByDescending<CommentEntity> { it.isPinned }.thenByDescending { it.isBookmarked }.thenByDescending { it.createdAt }
            CommentSortOrder.OLDEST_FIRST -> compareByDescending<CommentEntity> { it.isPinned }.thenByDescending { it.isBookmarked }.thenBy { it.createdAt }
            CommentSortOrder.ALPHABETICAL -> compareByDescending<CommentEntity> { it.isPinned }.thenByDescending { it.isBookmarked }.thenBy(String.CASE_INSENSITIVE_ORDER) { it.content.trim() }
        }

        // Build threads:
        val threads = roots
            .sortedWith(rootComparator)
            .map { root ->
                val threadReplies = (repliesByParent[root.id] ?: emptyList())
                    .sortedWith(replyComparator)
                CommentWithReplies(comment = root, replies = threadReplies)
            }

        CommentsUiState(
            totalCount = totalCount,
            displayedCount = displayedItems.size,
            hasEarlierComments = hasEarlier,
            remainingEarlierCount = remainingEarlier,
            commentThreads = threads,
            pageSize = tierPageSize
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CommentsUiState())

    // Active reply target (comment or reply)
    private val _replyingTo = MutableStateFlow<CommentEntity?>(null)
    val replyingTo: StateFlow<CommentEntity?> = _replyingTo.asStateFlow()

    // Active user avatar settings
    private val _userAvatarType = MutableStateFlow("INITIAL")
    val userAvatarType: StateFlow<String> = _userAvatarType.asStateFlow()

    private val _userAvatarValue = MutableStateFlow<String?>(null)
    val userAvatarValue: StateFlow<String?> = _userAvatarValue.asStateFlow()

    private val _userAvatarBgColor = MutableStateFlow<Long?>(null)
    val userAvatarBgColor: StateFlow<Long?> = _userAvatarBgColor.asStateFlow()

    fun setUserAvatar(type: String, value: String?, bgColor: Long?) {
        _userAvatarType.value = type
        _userAvatarValue.value = value
        _userAvatarBgColor.value = bgColor
    }

    // Note creation / edit dialog state
    private val _editingNote = MutableStateFlow<NoteEntity?>(null)
    val editingNote: StateFlow<NoteEntity?> = _editingNote.asStateFlow()

    private val _isCreatingNewNote = MutableStateFlow(false)
    val isCreatingNewNote: StateFlow<Boolean> = _isCreatingNewNote.asStateFlow()

    fun selectNote(noteId: Long?) {
        _selectedNoteId.value = noteId
        val tier = rewardState.value.currentTier
        val tierPageSize = tier.commentsPerPage
        _visibleCommentsLimit.value = tierPageSize
        _replyingTo.value = null
    }

    fun loadEarlierComments() {
        val tier = rewardState.value.currentTier
        val tierPageSize = tier.commentsPerPage
        _visibleCommentsLimit.value += tierPageSize
    }

    fun startCreateNote() {
        _isCreatingNewNote.value = true
        _editingNote.value = null
    }

    fun startEditNote(note: NoteEntity) {
        _editingNote.value = note
        _isCreatingNewNote.value = false
    }

    fun dismissNoteDialog() {
        _isCreatingNewNote.value = false
        _editingNote.value = null
    }

    fun saveNote(
        title: String,
        description: String,
        isPinned: Boolean,
        category: String = "",
        tags: String = "",
        imageUri: String? = null,
        audioUri: String? = null,
        audioDuration: Long? = null
    ) {
        viewModelScope.launch {
            // Register any new tags as custom labels
            if (tags.isNotBlank()) {
                tags.split(",").forEach { tag ->
                    val cleanTag = tag.trim().removePrefix("#")
                    if (cleanTag.isNotBlank()) {
                        createCustomLabel(cleanTag)
                    }
                }
            }

            val currentEditing = _editingNote.value
            if (currentEditing != null) {
                repository.updateNote(
                    currentEditing.copy(
                        title = title.trim(),
                        description = description.trim(),
                        isPinned = isPinned,
                        category = category.trim(),
                        tags = tags.trim(),
                        imageUri = imageUri,
                        audioUri = audioUri,
                        audioDuration = audioDuration
                    )
                )
                dismissNoteDialog()
            } else {
                // Check daily limit for notes
                val maxNotes = rewardState.value.maxDailyNotes
                if (!rewardManager.canCreateNoteToday()) {
                    _rateLimitWarning.value = "Đã đạt giới hạn $maxNotes ghi chú/ngày. Hãy nâng cấp gói mua tại Cửa hàng hoặc quay lại vào ngày mai!"
                    return@launch
                }

                val newId = repository.insertNote(
                    title = title.trim(),
                    description = description.trim(),
                    isPinned = isPinned,
                    category = category.trim(),
                    tags = tags.trim(),
                    imageUri = imageUri,
                    audioUri = audioUri,
                    audioDuration = audioDuration
                )

                // Accumulate 1 diamond for new note
                rewardManager.onNoteCreated()
                dismissNoteDialog()
            }
            recordInteraction()
        }
    }

    fun attachPhotoToNote(noteId: Long, imageUri: String?) {
        viewModelScope.launch {
            repository.updateNoteImageUri(noteId, imageUri)
            recordInteraction()
        }
    }

    fun toggleNotePinned(note: NoteEntity) {
        viewModelScope.launch {
            repository.toggleNotePinned(note.id, note.isPinned)
            recordInteraction()
        }
    }

    fun toggleNoteBookmarked(note: NoteEntity) {
        viewModelScope.launch {
            repository.toggleNoteBookmarked(note.id, note.isBookmarked)
            recordInteraction()
        }
    }

    fun toggleNoteCompleted(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isCompleted = !note.isCompleted))
            recordInteraction()
        }
    }

    fun toggleNoteArchived(note: NoteEntity) {
        viewModelScope.launch {
            repository.updateNote(note.copy(isArchived = !note.isArchived))
            recordInteraction()
        }
    }

    fun toggleCommentArchived(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleCommentArchived(comment)
            recordInteraction()
        }
    }

    fun archiveCompletedOrOlderNotes() {
        viewModelScope.launch {
            val notesList = repository.getAllNotesDirect()
            val now = System.currentTimeMillis()
            val olderCutoff = now - (7 * 24 * 60 * 60 * 1000) // 7 days
            notesList.forEach { note ->
                if (!note.isArchived && (note.isCompleted || note.createdAt < olderCutoff)) {
                    repository.updateNote(note.copy(isArchived = true))
                }
            }
            recordInteraction()
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.moveNoteToTrash(noteId)
            if (_selectedNoteId.value == noteId) {
                _selectedNoteId.value = null
            }
            _importExportMessage.value = "Đã chuyển ghi chú vào Thùng rác (lưu trong ${rewardState.value.trashRetentionDays} ngày)"
            recordInteraction()
        }
    }

    fun restoreNote(noteId: Long) {
        viewModelScope.launch {
            repository.restoreNoteFromTrash(noteId)
            _importExportMessage.value = "Đã khôi phục ghi chú thành công!"
            recordInteraction()
        }
    }

    fun permanentlyDeleteNote(noteId: Long) {
        viewModelScope.launch {
            repository.permanentlyDeleteNote(noteId)
            if (_selectedNoteId.value == noteId) {
                _selectedNoteId.value = null
            }
            _importExportMessage.value = "Đã xoá vĩnh viễn ghi chú."
            recordInteraction()
        }
    }

    fun restoreAllNotes() {
        viewModelScope.launch {
            repository.restoreAllNotesFromTrash()
            _importExportMessage.value = "Đã khôi phục toàn bộ ghi chú từ Thùng rác!"
            recordInteraction()
        }
    }

    fun emptyTrashNotes() {
        viewModelScope.launch {
            repository.emptyTrashNotes()
            _importExportMessage.value = "Đã dọn sạch thùng rác ghi chú."
            recordInteraction()
        }
    }

    fun setReplyingTo(target: CommentEntity?) {
        _replyingTo.value = target
    }

    fun addCommentOrReply(
        content: String,
        authorName: String,
        avatarType: String = _userAvatarType.value,
        avatarValue: String? = _userAvatarValue.value,
        avatarBgColor: Long? = _userAvatarBgColor.value,
        imageUri: String? = null,
        audioUri: String? = null,
        audioDuration: Long? = null
    ) {
        val noteId = _selectedNoteId.value ?: return
        if (content.isBlank() && imageUri.isNullOrBlank() && audioUri.isNullOrBlank()) return

        // Check daily limit for comments & replies
        val maxDailyComments = rewardState.value.maxDailyComments
        if (!rewardManager.canPostCommentToday()) {
            val tierTitle = rewardState.value.currentTier.title
            _rateLimitWarning.value = "Đã đạt giới hạn $maxDailyComments bình luận & phản hồi/ngày của $tierTitle. Hãy nâng cấp gói mua tại Cửa hàng hoặc quay lại vào ngày mai!"
            return
        }

        // Check rate limit for comments & replies
        val maxComments = rewardState.value.maxCommentsPerMinute
        if (!rewardManager.canPostCommentNow()) {
            val waitSec = rewardManager.getRemainingSecondsForCommentRateLimit()
            _rateLimitWarning.value = "Đã đạt giới hạn $maxComments bình luận và phản hồi/phút. Vui lòng chờ $waitSec giây hoặc nâng cấp gói mua tại Cửa hàng!"
            return
        }

        val replying = _replyingTo.value
        viewModelScope.launch {
            if (replying != null) {
                // If replying to a reply, find its root parent or attach to parent
                val parentId = replying.parentId ?: replying.id
                repository.insertComment(
                    noteId = noteId,
                    content = content.trim(),
                    authorName = authorName.ifBlank { "Đàm Tường Quân" },
                    parentId = parentId,
                    replyToAuthor = replying.authorName,
                    avatarType = avatarType,
                    avatarValue = avatarValue,
                    avatarBgColor = avatarBgColor,
                    imageUri = imageUri,
                    audioUri = audioUri,
                    audioDuration = audioDuration
                )
            } else {
                repository.insertComment(
                    noteId = noteId,
                    content = content.trim(),
                    authorName = authorName.ifBlank { "Đàm Tường Quân" },
                    parentId = null,
                    replyToAuthor = null,
                    avatarType = avatarType,
                    avatarValue = avatarValue,
                    avatarBgColor = avatarBgColor,
                    imageUri = imageUri,
                    audioUri = audioUri,
                    audioDuration = audioDuration
                )
            }
            // Accumulate 1 diamond for comment/reply
            rewardManager.onCommentOrReplyCreated()
            _replyingTo.value = null
            recordInteraction()
        }
    }

    fun attachPhotoToComment(commentId: Long, imageUri: String?) {
        viewModelScope.launch {
            repository.updateCommentImageUri(commentId, imageUri)
            recordInteraction()
        }
    }

    fun toggleCommentPinned(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleCommentPinned(comment.id, comment.isPinned)
            recordInteraction()
        }
    }

    fun toggleCommentBookmarked(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleCommentBookmarked(comment.id, comment.isBookmarked)
            recordInteraction()
        }
    }

    fun updateComment(
        commentId: Long,
        newContent: String,
        newAuthorName: String,
        avatarType: String = "INITIAL",
        avatarValue: String? = null,
        avatarBgColor: Long? = null,
        imageUri: String? = null
    ) {
        viewModelScope.launch {
            repository.updateComment(
                commentId,
                newContent,
                newAuthorName,
                avatarType,
                avatarValue,
                avatarBgColor,
                imageUri
            )
            recordInteraction()
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            repository.moveCommentToTrash(commentId)
            if (_replyingTo.value?.id == commentId) {
                _replyingTo.value = null
            }
            _importExportMessage.value = "Đã chuyển bình luận vào Thùng rác (lưu trong ${rewardState.value.trashRetentionDays} ngày)"
            recordInteraction()
        }
    }

    fun restoreComment(commentId: Long) {
        viewModelScope.launch {
            repository.restoreCommentFromTrash(commentId)
            _importExportMessage.value = "Đã khôi phục bình luận thành công!"
            recordInteraction()
        }
    }

    fun permanentlyDeleteComment(commentId: Long) {
        viewModelScope.launch {
            repository.permanentlyDeleteComment(commentId)
            if (_replyingTo.value?.id == commentId) {
                _replyingTo.value = null
            }
            _importExportMessage.value = "Đã xoá vĩnh viễn bình luận."
            recordInteraction()
        }
    }

    fun restoreAllComments() {
        viewModelScope.launch {
            repository.restoreAllCommentsFromTrash()
            _importExportMessage.value = "Đã khôi phục toàn bộ bình luận từ Thùng rác!"
            recordInteraction()
        }
    }

    fun emptyTrashComments() {
        viewModelScope.launch {
            repository.emptyTrashComments()
            _importExportMessage.value = "Đã dọn sạch thùng rác bình luận."
            recordInteraction()
        }
    }

    fun emptyAllTrash() {
        viewModelScope.launch {
            repository.emptyAllTrash()
            _importExportMessage.value = "Đã dọn sạch toàn bộ thùng rác!"
            recordInteraction()
        }
    }

    fun purgeExpiredTrash() {
        viewModelScope.launch {
            repository.purgeExpiredTrash(rewardState.value.trashRetentionDays)
        }
    }

    // Store & Check-in operations
    fun claimDailyCheckIn() {
        val claimedAmount = rewardManager.claimDailyCheckInReward()
        if (claimedAmount > 0) {
            _importExportMessage.value = "Điểm danh thành công! Bạn nhận được +$claimedAmount kim cương!"
            recordInteraction()
        } else {
            _rateLimitWarning.value = "Bạn đã điểm danh hôm nay rồi! Hãy quay lại vào ngày mai để nhận thêm 800 kim cương nhé."
        }
    }

    fun topUpDiamonds(amount: Int) {
        rewardManager.topUpDiamonds(amount)
        _importExportMessage.value = "Đã nạp thành công $amount kim cương vào tài khoản!"
    }

    fun activateStorePackage(tier: StorePackageTier): Boolean {
        val currentDiamonds = rewardState.value.totalDiamonds
        val success = rewardManager.activateStorePackage(tier)
        if (success) {
            _importExportMessage.value = "Kích hoạt thành công ${tier.title}! Giới hạn mới: ${tier.maxDailyComments} cmt/ngày, ${tier.maxCommentsPerMinute} cmt/phút, ${tier.maxDailyNotes} ghi chú/ngày."
        } else {
            val missing = (tier.diamondPrice - currentDiamonds).coerceAtLeast(0)
            val numberFormatter = java.text.NumberFormat.getNumberInstance(java.util.Locale.GERMANY)
            val formattedMissing = numberFormatter.format(missing)
            val packageLabel = if (tier.id in 1..8) "Gói ${tier.id}" else tier.title
            _rateLimitWarning.value = "Cần thêm $formattedMissing kim cương để mua gói nạp $packageLabel"
        }
        return success
    }

    /**
     * Quy đổi toàn bộ nội dung hiện có (ghi chú, bình luận, phản hồi) thành 20 kim cương / mục.
     */
    suspend fun redeemAllContentForDiamonds(): Int {
        val notes = repository.getAllNotesDirect()
        val comments = repository.getAllCommentsDirect()
        val roots = comments.filter { it.parentId == null }
        val replies = comments.filter { it.parentId != null }
        val earned = rewardManager.redeemContentForDiamonds(notes.size, roots.size, replies.size)
        if (earned > 0) {
            _importExportMessage.value = "Quy đổi thành công: +$earned kim cương (20 💎/mục) từ ${notes.size} ghi chú, ${roots.size} bình luận và ${replies.size} phản hồi!"
        }
        return earned
    }

    suspend fun exportAllNotesJson(): String {
        val notes = repository.getAllNotesDirect()
        val comments = repository.getAllCommentsDirect()
        return JsonBackupHelper.exportAllDataToJson(notes, comments)
    }

    suspend fun exportSingleNoteJson(noteId: Long): String? {
        val note = repository.getNoteByIdDirect(noteId) ?: return null
        val comments = repository.getCommentsForNoteDirect(noteId)
        return JsonBackupHelper.exportSingleNoteToJson(note, comments)
    }

    suspend fun importJsonData(jsonString: String, replaceExisting: Boolean): Result<ImportSummary> {
        val parseResult = JsonBackupHelper.parseJson(jsonString)
        if (parseResult.isFailure) {
            return Result.failure(parseResult.exceptionOrNull() ?: Exception("Lỗi phân tích cú pháp JSON"))
        }
        val backupData = parseResult.getOrThrow()
        return try {
            val summary = repository.importBackupData(backupData, replaceExisting)
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportAllNotesXml(): String {
        val notes = repository.getAllNotesDirect()
        val comments = repository.getAllCommentsDirect()
        return com.example.data.backup.MultiFormatBackupHelper.exportToXml(notes, comments)
    }

    suspend fun exportAllNotesXlsx(outputStream: java.io.OutputStream) {
        val notes = repository.getAllNotesDirect()
        val comments = repository.getAllCommentsDirect()
        com.example.data.backup.MultiFormatBackupHelper.exportToXlsx(outputStream, notes, comments)
    }

    suspend fun exportAllNotesTxt(): String {
        val notes = repository.getAllNotesDirect()
        val comments = repository.getAllCommentsDirect()
        return com.example.data.backup.MultiFormatBackupHelper.exportToTxt(notes, comments)
    }

    suspend fun exportAllNotesHtml(): String {
        val notes = repository.getAllNotesDirect()
        val comments = repository.getAllCommentsDirect()
        return com.example.data.backup.MultiFormatBackupHelper.exportToHtml(notes, comments)
    }

    suspend fun exportAllNotesCsv(): String {
        val notes = repository.getAllNotesDirect()
        val comments = repository.getAllCommentsDirect()
        return com.example.data.backup.MultiFormatBackupHelper.exportToCsv(notes, comments)
    }

    suspend fun exportAllNotesDocx(outputStream: java.io.OutputStream) {
        val notes = repository.getAllNotesDirect()
        val comments = repository.getAllCommentsDirect()
        com.example.data.backup.MultiFormatBackupHelper.exportToDocx(outputStream, notes, comments)
    }

    suspend fun exportAllNotesPptx(outputStream: java.io.OutputStream) {
        val notes = repository.getAllNotesDirect()
        val comments = repository.getAllCommentsDirect()
        com.example.data.backup.MultiFormatBackupHelper.exportToPptx(outputStream, notes, comments)
    }

    suspend fun importMultiFormatData(text: String, format: String, replaceExisting: Boolean): Result<ImportSummary> {
        val parseResult = when (format.lowercase()) {
            "xml" -> com.example.data.backup.MultiFormatBackupHelper.parseXml(text)
            "txt" -> com.example.data.backup.MultiFormatBackupHelper.parseTxt(text)
            "html" -> com.example.data.backup.MultiFormatBackupHelper.parseHtml(text)
            "csv" -> com.example.data.backup.MultiFormatBackupHelper.parseCsv(text)
            else -> return Result.failure(IllegalArgumentException("Định dạng không được hỗ trợ: $format"))
        }
        if (parseResult.isFailure) {
            return Result.failure(parseResult.exceptionOrNull() ?: Exception("Lỗi phân tích cú pháp tệp tin"))
        }
        val backupData = parseResult.getOrThrow()
        return try {
            val summary = repository.importBackupData(backupData, replaceExisting)
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importXlsxData(context: Context, uri: Uri, replaceExisting: Boolean): Result<ImportSummary> {
        val parseResult = com.example.data.backup.MultiFormatBackupHelper.parseXlsx(context, uri)
        if (parseResult.isFailure) {
            return Result.failure(parseResult.exceptionOrNull() ?: Exception("Lỗi phân tích cú pháp tệp tin Excel .xlsx"))
        }
        val backupData = parseResult.getOrThrow()
        return try {
            val summary = repository.importBackupData(backupData, replaceExisting)
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importDocxData(context: Context, uri: Uri, replaceExisting: Boolean): Result<ImportSummary> {
        val parseResult = com.example.data.backup.MultiFormatBackupHelper.parseDocx(context, uri)
        if (parseResult.isFailure) {
            return Result.failure(parseResult.exceptionOrNull() ?: Exception("Lỗi phân tích cú pháp tệp tin DOCX"))
        }
        val backupData = parseResult.getOrThrow()
        return try {
            val summary = repository.importBackupData(backupData, replaceExisting)
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importPptxData(context: Context, uri: Uri, replaceExisting: Boolean): Result<ImportSummary> {
        val parseResult = com.example.data.backup.MultiFormatBackupHelper.parsePptx(context, uri)
        if (parseResult.isFailure) {
            return Result.failure(parseResult.exceptionOrNull() ?: Exception("Lỗi phân tích cú pháp tệp tin PPTX"))
        }
        val backupData = parseResult.getOrThrow()
        return try {
            val summary = repository.importBackupData(backupData, replaceExisting)
            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Auto Backup & Local Backups operations
    fun setAutoBackupEnabled(enabled: Boolean) {
        autoBackupManager.setAutoBackupEnabled(enabled)
        if (enabled) {
            viewModelScope.launch {
                autoBackupManager.checkAndRunDailyBackup(repository)
            }
        }
    }

    fun triggerManualBackup(onFinished: (Result<File>) -> Unit = {}) {
        viewModelScope.launch {
            val result = autoBackupManager.performBackup(repository, isAutomatic = false)
            if (result.isSuccess) {
                _importExportMessage.value = "Đã sao lưu thành công tệp: ${result.getOrNull()?.name}"
            } else {
                _importExportMessage.value = "Lỗi khi sao lưu: ${result.exceptionOrNull()?.localizedMessage}"
            }
            onFinished(result)
        }
    }

    fun getBackupFiles(): List<BackupFileInfo> {
        return autoBackupManager.getBackupFiles()
    }

    fun deleteBackupFile(file: File): Boolean {
        val deleted = autoBackupManager.deleteBackupFile(file)
        if (deleted) {
            _importExportMessage.value = "Đã xóa bản sao lưu ${file.name}"
        }
        return deleted
    }

    suspend fun restoreFromLocalBackup(file: File, replaceExisting: Boolean): Result<ImportSummary> {
        val result = autoBackupManager.restoreFromBackupFile(file, repository, replaceExisting)
        if (result.isSuccess) {
            val summary = result.getOrThrow()
            _importExportMessage.value = "Khôi phục thành công: ${summary.notesImported} ghi chú, ${summary.commentsImported} bình luận, ${summary.repliesImported} phản hồi!"
        }
        return result
    }

    // PDF Import operations
    suspend fun fetchPdfPreview(context: Context, uri: Uri): Result<PdfParsedData> {
        return PdfImportHelper.parsePdfUri(context, uri)
    }

    suspend fun importPdf(
        context: Context,
        uri: Uri,
        customTitle: String? = null,
        customDescription: String? = null,
        customCategory: String? = null
    ): Result<ImportSummary> {
        val parseResult = PdfImportHelper.parsePdfUri(context, uri)
        if (parseResult.isFailure) {
            val err = parseResult.exceptionOrNull() ?: Exception("Lỗi khi đọc tệp PDF")
            _importExportMessage.value = "Lỗi nhập PDF: ${err.message}"
            return Result.failure(err)
        }

        val parsedData = parseResult.getOrThrow()
        val originalNote = parsedData.note
        val finalNote = originalNote.copy(
            title = customTitle?.trim()?.ifBlank { null } ?: originalNote.title,
            description = customDescription?.trim() ?: originalNote.description,
            category = customCategory?.trim()?.ifBlank { null } ?: originalNote.category
        )

        return try {
            val backupData = BackupData(notes = listOf(finalNote))
            val summary = repository.importBackupData(backupData, replaceExisting = false)
            rewardManager.onNoteCreated()
            _importExportMessage.value = "Đã nhập thành công 1 ghi chú, ${summary.commentsImported} bình luận, ${summary.repliesImported} phản hồi từ tệp PDF!"
            recordInteraction()
            Result.success(summary)
        } catch (e: Exception) {
            _importExportMessage.value = "Lỗi lưu ghi chú từ PDF: ${e.localizedMessage}"
            Result.failure(e)
        }
    }

    // URL Import operations
    suspend fun fetchUrlPreview(url: String): Result<UrlParsedNoteData> {
        return UrlImportHelper.importFromUrl(url)
    }

    suspend fun importFromUrl(
        url: String,
        customTitle: String? = null,
        customDescription: String? = null,
        customCategory: String? = null
    ): Result<ImportSummary> {
        val parseResult = UrlImportHelper.importFromUrl(url)
        if (parseResult.isFailure) {
            val err = parseResult.exceptionOrNull() ?: Exception("Lỗi khi tải từ URL")
            _importExportMessage.value = "Lỗi nhập URL: ${err.message}"
            return Result.failure(err)
        }

        val parsedData = parseResult.getOrThrow()
        val originalNote = parsedData.note
        val finalNote = originalNote.copy(
            title = customTitle?.trim()?.ifBlank { null } ?: originalNote.title,
            description = customDescription?.trim() ?: originalNote.description,
            category = customCategory?.trim()?.ifBlank { null } ?: originalNote.category
        )

        return try {
            val backupData = BackupData(notes = listOf(finalNote))
            val summary = repository.importBackupData(backupData, replaceExisting = false)
            rewardManager.onNoteCreated()
            _importExportMessage.value = "Đã nhập thành công ghi chú từ URL (${parsedData.pageTitle})!"
            recordInteraction()
            Result.success(summary)
        } catch (e: Exception) {
            _importExportMessage.value = "Lỗi lưu ghi chú từ URL: ${e.localizedMessage}"
            Result.failure(e)
        }
    }

    suspend fun saveImportedNote(note: BackupNote): Result<ImportSummary> {
        return try {
            val backupData = BackupData(notes = listOf(note))
            val summary = repository.importBackupData(backupData, replaceExisting = false)
            rewardManager.onNoteCreated()
            _importExportMessage.value = "Đã lưu ghi chú thành công (+20 💎)!"
            recordInteraction()
            Result.success(summary)
        } catch (e: Exception) {
            _importExportMessage.value = "Lỗi lưu ghi chú: ${e.localizedMessage}"
            Result.failure(e)
        }
    }

    fun importSampleNote(title: String, content: String, category: String, tags: String) {
        viewModelScope.launch {
            if (tags.isNotBlank()) {
                tags.split(",").forEach { tag ->
                    val cleanTag = tag.trim().removePrefix("#")
                    if (cleanTag.isNotBlank()) {
                        createCustomLabel(cleanTag)
                    }
                }
            }

            try {
                repository.insertNote(
                    title = title.trim(),
                    description = content.trim(),
                    isPinned = false,
                    category = category.trim(),
                    tags = tags.trim(),
                    imageUri = null,
                    audioUri = null,
                    audioDuration = null
                )
                rewardManager.onNoteCreated()
                _importExportMessage.value = "Đã nhập thành công mẫu: '$title' (+20 💎)!"
                recordInteraction()
            } catch (e: Exception) {
                _importExportMessage.value = "Lỗi nhập mẫu ghi chú: ${e.localizedMessage}"
            }
        }
    }
}

private data class NoteFilterState(
    val query: String,
    val category: String?,
    val tag: String?,
    val bookmarkedOnly: Boolean,
    val sort: NoteSortOrder
)
