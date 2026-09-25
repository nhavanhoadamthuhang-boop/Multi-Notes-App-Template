package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class DiamondTransactionType {
    NOTE_CREATED,
    COMMENT_CREATED,
    DAILY_CHECKIN,
    TOP_UP,
    PACKAGE_PURCHASE,
    REDEMPTION_CONVERT
}

data class DiamondTransaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val type: DiamondTransactionType
)

enum class StorePackageTier(
    val id: Int,
    val title: String,
    val diamondPrice: Int,
    val maxCommentsPerMinute: Int,
    val maxDailyNotes: Int,
    val maxDailyComments: Int,
    val commentsPerPage: Int,
    val trashRetentionDays: Int,
    val maxStorageGB: Int,
    val badgeLabel: String,
    val description: String
) {
    DEFAULT(
        id = 0,
        title = "Gói Mặc Định",
        diamondPrice = 0,
        maxCommentsPerMinute = 32,
        maxDailyNotes = 4_000,
        maxDailyComments = 8_000,
        commentsPerPage = 200,
        trashRetentionDays = 60,
        maxStorageGB = 8,
        badgeLabel = "Mặc định (Thùng rác 60 ngày • Dung lượng 8 GB)",
        description = "Gói Mặc Định: Miễn phí • 8.000 cmt/ngày • 32 cmt/phút • 200 cmt/trang • 4.000 ghi chú/ngày • Thùng rác 60 ngày • Giới hạn dung lượng 8 GB"
    ),
    PACKAGE_1(
        id = 1,
        title = "Gói Cấp 1 (8.000 Kim Cương)",
        diamondPrice = 8_000,
        maxCommentsPerMinute = 64,
        maxDailyNotes = 8_000,
        maxDailyComments = 16_000,
        commentsPerPage = 400,
        trashRetentionDays = 120,
        maxStorageGB = 16,
        badgeLabel = "Gói Cấp 1 (Thùng rác 120 ngày • Dung lượng 16 GB)",
        description = "Gói Cấp 1 (8.000 💎): 16.000 cmt/ngày • 64 cmt/phút • 400 cmt/trang • 8.000 ghi chú/ngày • Thùng rác 120 ngày • Giới hạn dung lượng 16 GB"
    ),
    PACKAGE_2(
        id = 2,
        title = "Gói Cấp 2 (16.000 Kim Cương)",
        diamondPrice = 16_000,
        maxCommentsPerMinute = 128,
        maxDailyNotes = 16_000,
        maxDailyComments = 32_000,
        commentsPerPage = 800,
        trashRetentionDays = 180,
        maxStorageGB = 24,
        badgeLabel = "Gói Cấp 2 (Thùng rác 180 ngày • Dung lượng 24 GB)",
        description = "Gói Cấp 2 (16.000 💎): 32.000 cmt/ngày • 128 cmt/phút • 800 cmt/trang • 16.000 ghi chú/ngày • Thùng rác 180 ngày • Giới hạn dung lượng 24 GB"
    ),
    PACKAGE_3(
        id = 3,
        title = "Gói Cấp 3 (32.000 Kim Cương)",
        diamondPrice = 32_000,
        maxCommentsPerMinute = 256,
        maxDailyNotes = 32_000,
        maxDailyComments = 64_000,
        commentsPerPage = 1_600,
        trashRetentionDays = 360,
        maxStorageGB = 32,
        badgeLabel = "Gói Cấp 3 (Thùng rác 360 ngày - 1 năm • Dung lượng 32 GB)",
        description = "Gói Cấp 3 (32.000 💎): 64.000 cmt/ngày • 256 cmt/phút • 1.600 cmt/trang • 32.000 ghi chú/ngày • Thùng rác 360 ngày (1 năm) • Giới hạn dung lượng 32 GB"
    ),
    PACKAGE_4(
        id = 4,
        title = "Gói Cấp 4 (64.000 Kim Cương)",
        diamondPrice = 64_000,
        maxCommentsPerMinute = 512,
        maxDailyNotes = 64_000,
        maxDailyComments = 128_000,
        commentsPerPage = 3_200,
        trashRetentionDays = 720,
        maxStorageGB = 64,
        badgeLabel = "Gói Cấp 4 (Thùng rác 720 ngày - 2 năm • Dung lượng 64 GB)",
        description = "Gói Cấp 4 (64.000 💎): 128.000 cmt/ngày • 512 cmt/phút • 3.200 cmt/trang • 64.000 ghi chú/ngày • Thùng rác 720 ngày (2 năm) • Giới hạn dung lượng 64 GB"
    ),
    PACKAGE_5(
        id = 5,
        title = "Gói Cấp 5 (128.000 Kim Cương)",
        diamondPrice = 128_000,
        maxCommentsPerMinute = 1_024,
        maxDailyNotes = 128_000,
        maxDailyComments = 256_000,
        commentsPerPage = 6_400,
        trashRetentionDays = 1_440,
        maxStorageGB = 128,
        badgeLabel = "Gói Cấp 5 (Thùng rác 1.440 ngày - 4 năm • Dung lượng 128 GB)",
        description = "Gói Cấp 5 (128.000 💎): 256.000 cmt/ngày • 1.024 cmt/phút • 6.400 cmt/trang • 128.000 ghi chú/ngày • Thùng rác 1.440 ngày (4 năm) • Giới hạn dung lượng 128 GB"
    ),
    PACKAGE_6(
        id = 6,
        title = "Gói Cấp 6 (256.000 Kim Cương)",
        diamondPrice = 256_000,
        maxCommentsPerMinute = 2_048,
        maxDailyNotes = 256_000,
        maxDailyComments = 512_000,
        commentsPerPage = 12_800,
        trashRetentionDays = 2_880,
        maxStorageGB = 256,
        badgeLabel = "Gói Cấp 6 (Thùng rác 2.880 ngày - 8 năm • Dung lượng 256 GB)",
        description = "Gói Cấp 6 (256.000 💎): 512.000 cmt/ngày • 2.048 cmt/phút • 12.800 cmt/trang • 256.000 ghi chú/ngày • Thùng rác 2.880 ngày (8 năm) • Giới hạn dung lượng 256 GB"
    ),
    PACKAGE_7(
        id = 7,
        title = "Gói Cấp 7 (512.000 Kim Cương)",
        diamondPrice = 512_000,
        maxCommentsPerMinute = 4_096,
        maxDailyNotes = 512_000,
        maxDailyComments = 1_024_000,
        commentsPerPage = 25_600,
        trashRetentionDays = 5_760,
        maxStorageGB = 512,
        badgeLabel = "Gói Cấp 7 (Thùng rác 5.760 ngày - 16 năm • Dung lượng 512 GB)",
        description = "Gói Cấp 7 (512.000 💎): 1.024.000 cmt/ngày • 4.096 cmt/phút • 25.600 cmt/trang • 512.000 ghi chú/ngày • Thùng rác 5.760 ngày (16 năm) • Giới hạn dung lượng 512 GB"
    ),
    PACKAGE_8(
        id = 8,
        title = "Gói Cấp 8 (VIP) (1.024.000 Kim Cương)",
        diamondPrice = 1_024_000,
        maxCommentsPerMinute = 8_192,
        maxDailyNotes = 1_024_000,
        maxDailyComments = 2_048_000,
        commentsPerPage = 51_200,
        trashRetentionDays = 11_520,
        maxStorageGB = 1_024,
        badgeLabel = "Gói Cấp 8 VIP (Thùng rác 11.520 ngày - 32 năm • Dung lượng 1.024 GB)",
        description = "Gói Cấp 8 (VIP) (1.024.000 💎): 2.048.000 cmt/ngày • 8.192 cmt/phút • 51.200 cmt/trang • 1.024.000 ghi chú/ngày • Thùng rác 11.520 ngày (32 năm) • Giới hạn dung lượng 1.024 GB"
    );

    companion object {
        fun fromId(id: Int): StorePackageTier {
            return entries.find { it.id == id } ?: DEFAULT
        }
    }
}

data class DiamondRewardState(
    val totalDiamonds: Int = 0,
    val targetDiamonds: Int = 8000,
    val notesCreatedToday: Int = 0,
    val maxDailyNotes: Int = 4000,
    val commentsPostedToday: Int = 0,
    val maxDailyComments: Int = 8000,
    val commentsInCurrentMinute: Int = 0,
    val maxCommentsPerMinute: Int = 32,
    val totalNotesCreatedAllTime: Int = 0,
    val totalCommentsAllTime: Int = 0,
    val isGoalCompleted: Boolean = false,
    val currentTier: StorePackageTier = StorePackageTier.DEFAULT,
    val hasCheckedInToday: Boolean = false
) {
    val trashRetentionDays: Int
        get() = currentTier.trashRetentionDays

    val commentsPerPage: Int
        get() = currentTier.commentsPerPage

    val remainingDailyComments: Int
        get() = (maxDailyComments - commentsPostedToday).coerceAtLeast(0)

    val remainingDailyNotes: Int
        get() = (maxDailyNotes - notesCreatedToday).coerceAtLeast(0)

    val maxStorageGB: Int
        get() = currentTier.maxStorageGB

    val usedStorageMB: Float
        get() = (totalNotesCreatedAllTime * 0.25f + totalCommentsAllTime * 0.05f + 12.5f).coerceAtMost(maxStorageGB * 1024f)

    val usedStorageGB: Float
        get() = usedStorageMB / 1024f

    val storageProgress: Float
        get() = (usedStorageGB / maxStorageGB.toFloat()).coerceIn(0.01f, 1f)
}

class DiamondRewardManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("diamond_rewards_prefs", Context.MODE_PRIVATE)

    companion object {
        const val DIAMOND_GOAL = 8000
        const val ONE_MINUTE_MS = 60_000L
        const val DAILY_CHECK_IN_DIAMONDS = 800
        const val DIAMONDS_PER_NOTE = 20
        const val DIAMONDS_PER_COMMENT = 20
        const val DIAMONDS_PER_REPLY = 20

        private const val KEY_TOTAL_DIAMONDS = "key_total_diamonds"
        private const val KEY_TOTAL_NOTES_ALL_TIME = "key_total_notes_all_time"
        private const val KEY_TOTAL_COMMENTS_ALL_TIME = "key_total_comments_all_time"
        private const val KEY_DAY_KEY = "key_current_day"
        private const val KEY_TODAY_NOTES_COUNT = "key_today_notes_count"
        private const val KEY_TODAY_COMMENTS_COUNT = "key_today_comments_count"
        private const val KEY_ACTIVE_TIER_ID = "key_active_tier_id"
        private const val KEY_LAST_CHECK_IN_DATE = "key_last_check_in_date"
        private const val KEY_TRANSACTIONS_JSON = "key_transactions_json"
        private const val MAX_TRANSACTIONS = 100
    }

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
    private val commentTimestamps = mutableListOf<Long>()

    private val _rewardState = MutableStateFlow(loadCurrentState())
    val rewardState: StateFlow<DiamondRewardState> = _rewardState.asStateFlow()

    private val _transactions = MutableStateFlow<List<DiamondTransaction>>(loadTransactions())
    val transactions: StateFlow<List<DiamondTransaction>> = _transactions.asStateFlow()

    private fun getTodayKey(): String = dateFormat.format(Date())

    private fun loadTransactions(): List<DiamondTransaction> {
        val rawJson = prefs.getString(KEY_TRANSACTIONS_JSON, null) ?: return emptyList()
        val list = mutableListOf<DiamondTransaction>()
        try {
            val array = JSONArray(rawJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DiamondTransaction(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        title = obj.optString("title", ""),
                        amount = obj.optInt("amount", 0),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        type = try {
                            DiamondTransactionType.valueOf(obj.optString("type", DiamondTransactionType.NOTE_CREATED.name))
                        } catch (e: Exception) {
                            DiamondTransactionType.NOTE_CREATED
                        }
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveTransactions(transactions: List<DiamondTransaction>) {
        val array = JSONArray()
        transactions.take(MAX_TRANSACTIONS).forEach { tx ->
            val obj = JSONObject()
            obj.put("id", tx.id)
            obj.put("title", tx.title)
            obj.put("amount", tx.amount)
            obj.put("timestamp", tx.timestamp)
            obj.put("type", tx.type.name)
            array.put(obj)
        }
        prefs.edit().putString(KEY_TRANSACTIONS_JSON, array.toString()).apply()
    }

    @Synchronized
    private fun recordTransaction(title: String, amount: Int, type: DiamondTransactionType) {
        val current = loadTransactions().toMutableList()
        current.add(0, DiamondTransaction(title = title, amount = amount, timestamp = System.currentTimeMillis(), type = type))
        saveTransactions(current)
        _transactions.value = current
    }

    private fun loadCurrentState(): DiamondRewardState {
        val totalDiamonds = prefs.getInt(KEY_TOTAL_DIAMONDS, 0)
        val totalNotes = prefs.getInt(KEY_TOTAL_NOTES_ALL_TIME, 0)
        val totalComments = prefs.getInt(KEY_TOTAL_COMMENTS_ALL_TIME, 0)
        val tierId = prefs.getInt(KEY_ACTIVE_TIER_ID, 0)
        val tier = StorePackageTier.fromId(tierId)

        val savedDay = prefs.getString(KEY_DAY_KEY, "")
        val currentDay = getTodayKey()
        val todayNotes = if (savedDay == currentDay) {
            prefs.getInt(KEY_TODAY_NOTES_COUNT, 0)
        } else {
            0
        }
        val todayComments = if (savedDay == currentDay) {
            prefs.getInt(KEY_TODAY_COMMENTS_COUNT, 0)
        } else {
            0
        }

        val lastCheckIn = prefs.getString(KEY_LAST_CHECK_IN_DATE, "")
        val hasCheckedInToday = (lastCheckIn == currentDay)

        pruneOldCommentTimestamps()

        return DiamondRewardState(
            totalDiamonds = totalDiamonds,
            targetDiamonds = DIAMOND_GOAL,
            notesCreatedToday = todayNotes,
            maxDailyNotes = tier.maxDailyNotes,
            commentsPostedToday = todayComments,
            maxDailyComments = tier.maxDailyComments,
            commentsInCurrentMinute = commentTimestamps.size,
            maxCommentsPerMinute = tier.maxCommentsPerMinute,
            totalNotesCreatedAllTime = totalNotes,
            totalCommentsAllTime = totalComments,
            isGoalCompleted = totalDiamonds >= DIAMOND_GOAL,
            currentTier = tier,
            hasCheckedInToday = hasCheckedInToday
        )
    }

    private fun pruneOldCommentTimestamps() {
        val now = System.currentTimeMillis()
        val threshold = now - ONE_MINUTE_MS
        commentTimestamps.removeAll { it < threshold }
    }

    /**
     * Check if user can create a note today under their tier's daily notes limit.
     */
    fun canCreateNoteToday(): Boolean {
        refreshDailyCountsIfNeeded()
        return _rewardState.value.notesCreatedToday < _rewardState.value.maxDailyNotes
    }

    /**
     * Check if user can post a comment/reply under both daily limit and rate limit per minute.
     */
    fun canPostCommentNow(): Boolean {
        refreshDailyCountsIfNeeded()
        if (_rewardState.value.commentsPostedToday >= _rewardState.value.maxDailyComments) {
            return false
        }
        pruneOldCommentTimestamps()
        return commentTimestamps.size < _rewardState.value.maxCommentsPerMinute
    }

    fun canPostCommentToday(): Boolean {
        refreshDailyCountsIfNeeded()
        return _rewardState.value.commentsPostedToday < _rewardState.value.maxDailyComments
    }

    fun getRemainingSecondsForCommentRateLimit(): Int {
        pruneOldCommentTimestamps()
        if (commentTimestamps.size < _rewardState.value.maxCommentsPerMinute) return 0
        val oldestInWindow = commentTimestamps.firstOrNull() ?: return 0
        val remainingMs = (oldestInWindow + ONE_MINUTE_MS) - System.currentTimeMillis()
        return ((remainingMs / 1000) + 1).coerceAtLeast(1).toInt()
    }

    private fun refreshDailyCountsIfNeeded() {
        val currentDay = getTodayKey()
        val savedDay = prefs.getString(KEY_DAY_KEY, "")
        if (savedDay != currentDay) {
            prefs.edit()
                .putString(KEY_DAY_KEY, currentDay)
                .putInt(KEY_TODAY_NOTES_COUNT, 0)
                .putInt(KEY_TODAY_COMMENTS_COUNT, 0)
                .apply()
            updateState()
        }
    }

    /**
     * Record a newly created note. Increments diamond count by +20 and records daily count.
     */
    @Synchronized
    fun onNoteCreated(): Boolean {
        refreshDailyCountsIfNeeded()
        val currentState = _rewardState.value
        val currentTodayCount = currentState.notesCreatedToday
        if (currentTodayCount >= currentState.maxDailyNotes) {
            return false
        }

        val newTodayCount = currentTodayCount + 1
        val newTotalDiamonds = currentState.totalDiamonds + DIAMONDS_PER_NOTE
        val newTotalNotes = currentState.totalNotesCreatedAllTime + 1

        prefs.edit()
            .putString(KEY_DAY_KEY, getTodayKey())
            .putInt(KEY_TODAY_NOTES_COUNT, newTodayCount)
            .putInt(KEY_TOTAL_DIAMONDS, newTotalDiamonds)
            .putInt(KEY_TOTAL_NOTES_ALL_TIME, newTotalNotes)
            .apply()

        recordTransaction("Tạo ghi chú mới (+20 💎)", DIAMONDS_PER_NOTE, DiamondTransactionType.NOTE_CREATED)
        updateState()
        return true
    }

    /**
     * Record a newly posted comment or reply. Increments diamond count by +20 and records rate limit timestamp.
     */
    @Synchronized
    fun onCommentOrReplyCreated(): Boolean {
        refreshDailyCountsIfNeeded()
        pruneOldCommentTimestamps()
        val currentState = _rewardState.value
        if (currentState.commentsPostedToday >= currentState.maxDailyComments) {
            return false
        }
        if (commentTimestamps.size >= currentState.maxCommentsPerMinute) {
            return false
        }

        val now = System.currentTimeMillis()
        commentTimestamps.add(now)

        val newTodayComments = currentState.commentsPostedToday + 1
        val newTotalDiamonds = currentState.totalDiamonds + DIAMONDS_PER_COMMENT
        val newTotalComments = currentState.totalCommentsAllTime + 1

        prefs.edit()
            .putString(KEY_DAY_KEY, getTodayKey())
            .putInt(KEY_TODAY_COMMENTS_COUNT, newTodayComments)
            .putInt(KEY_TOTAL_DIAMONDS, newTotalDiamonds)
            .putInt(KEY_TOTAL_COMMENTS_ALL_TIME, newTotalComments)
            .apply()

        recordTransaction("Bình luận / Phản hồi (+20 💎)", DIAMONDS_PER_COMMENT, DiamondTransactionType.COMMENT_CREATED)
        updateState()
        return true
    }

    /**
     * Quy đổi tổng số ghi chú, bình luận và phản hồi thành 20 kim cương / mục.
     * Quy đổi 1 ghi chú, 1 bình luận, 1 phản hồi = 20 viên kim cương.
     */
    @Synchronized
    fun redeemContentForDiamonds(notesCount: Int, commentsCount: Int, repliesCount: Int): Int {
        val totalItems = notesCount + commentsCount + repliesCount
        if (totalItems <= 0) return 0
        val diamondsEarned = totalItems * 20
        val newTotalDiamonds = _rewardState.value.totalDiamonds + diamondsEarned

        prefs.edit()
            .putInt(KEY_TOTAL_DIAMONDS, newTotalDiamonds)
            .apply()

        recordTransaction(
            "Quy đổi $totalItems mục ($notesCount ghi chú, $commentsCount bình luận, $repliesCount phản hồi) (+${diamondsEarned} 💎)",
            diamondsEarned,
            DiamondTransactionType.REDEMPTION_CONVERT
        )
        updateState()
        return diamondsEarned
    }

    /**
     * Claim daily check-in reward (+800 diamonds). Returns amount claimed (800 or 0 if already claimed today).
     */
    @Synchronized
    fun claimDailyCheckInReward(): Int {
        val currentState = _rewardState.value
        if (currentState.hasCheckedInToday) {
            return 0
        }

        val todayKey = getTodayKey()
        val newTotalDiamonds = currentState.totalDiamonds + DAILY_CHECK_IN_DIAMONDS

        prefs.edit()
            .putString(KEY_LAST_CHECK_IN_DATE, todayKey)
            .putInt(KEY_TOTAL_DIAMONDS, newTotalDiamonds)
            .apply()

        recordTransaction("Điểm danh hàng ngày (+800 💎)", DAILY_CHECK_IN_DIAMONDS, DiamondTransactionType.DAILY_CHECKIN)
        updateState()
        return DAILY_CHECK_IN_DIAMONDS
    }

    /**
     * Top up diamonds directly in store
     */
    @Synchronized
    fun topUpDiamonds(amount: Int) {
        if (amount <= 0) return
        val newTotalDiamonds = _rewardState.value.totalDiamonds + amount
        prefs.edit()
            .putInt(KEY_TOTAL_DIAMONDS, newTotalDiamonds)
            .apply()

        recordTransaction("Nạp Kim Cương (+$amount 💎)", amount, DiamondTransactionType.TOP_UP)
        updateState()
    }

    /**
     * Purchase/activate a store package tier using existing diamonds
     */
    @Synchronized
    fun activateStorePackage(tier: StorePackageTier): Boolean {
        val currentDiamonds = _rewardState.value.totalDiamonds
        if (currentDiamonds < tier.diamondPrice) {
            return false
        }

        val newTotalDiamonds = currentDiamonds - tier.diamondPrice
        prefs.edit()
            .putInt(KEY_TOTAL_DIAMONDS, newTotalDiamonds)
            .putInt(KEY_ACTIVE_TIER_ID, tier.id)
            .apply()

        recordTransaction("Kích hoạt ${tier.title} (-${tier.diamondPrice} 💎)", -tier.diamondPrice, DiamondTransactionType.PACKAGE_PURCHASE)
        updateState()
        return true
    }

    fun refreshRateLimitTracker() {
        pruneOldCommentTimestamps()
        updateState()
    }

    private fun updateState() {
        _rewardState.value = loadCurrentState()
    }
}

