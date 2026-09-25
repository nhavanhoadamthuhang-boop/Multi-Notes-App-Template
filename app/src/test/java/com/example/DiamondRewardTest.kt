package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.DiamondRewardManager
import com.example.data.repository.StorePackageTier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DiamondRewardTest {

    private lateinit var context: Context
    private lateinit var rewardManager: DiamondRewardManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        // Clear prefs for fresh test
        context.getSharedPreferences("diamond_rewards_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        rewardManager = DiamondRewardManager(context)
    }

    @Test
    fun initialization_loadsCorrectInitialValues() {
        val state = rewardManager.rewardState.value
        assertEquals(0, state.totalDiamonds)
        assertEquals(8000, state.targetDiamonds)
        assertEquals(0, state.notesCreatedToday)
        assertEquals(4000, state.maxDailyNotes)
        assertEquals(8000, state.maxDailyComments)
        assertEquals(32, state.maxCommentsPerMinute)
        assertEquals(StorePackageTier.DEFAULT, state.currentTier)
        assertFalse(state.isGoalCompleted)
    }

    @Test
    fun onNoteCreated_incrementsDiamondsAndTodayNotes() {
        assertTrue(rewardManager.canCreateNoteToday())
        val success = rewardManager.onNoteCreated()
        assertTrue(success)

        val state = rewardManager.rewardState.value
        assertEquals(20, state.totalDiamonds)
        assertEquals(1, state.notesCreatedToday)
        assertEquals(1, state.totalNotesCreatedAllTime)
    }

    @Test
    fun onCommentOrReplyCreated_incrementsDiamondsAndTracksRateLimit() {
        assertTrue(rewardManager.canPostCommentNow())
        val success = rewardManager.onCommentOrReplyCreated()
        assertTrue(success)

        val state = rewardManager.rewardState.value
        assertEquals(20, state.totalDiamonds)
        assertEquals(1, state.totalCommentsAllTime)
        assertEquals(1, state.commentsInCurrentMinute)
    }

    @Test
    fun activateStorePackage1_upgradesLimitsProperly() {
        // Top up 8,000 diamonds
        rewardManager.topUpDiamonds(8000)
        assertEquals(8000, rewardManager.rewardState.value.totalDiamonds)

        // Activate Package 1
        val activated = rewardManager.activateStorePackage(StorePackageTier.PACKAGE_1)
        assertTrue(activated)

        val state = rewardManager.rewardState.value
        assertEquals(0, state.totalDiamonds) // 8000 spent
        assertEquals(StorePackageTier.PACKAGE_1, state.currentTier)
        assertEquals(64, state.maxCommentsPerMinute)
        assertEquals(8000, state.maxDailyNotes)
    }

    @Test
    fun activateStorePackage2_upgradesLimitsProperly() {
        // Top up 16,000 diamonds
        rewardManager.topUpDiamonds(16000)
        assertEquals(16000, rewardManager.rewardState.value.totalDiamonds)

        // Activate Package 2
        val activated = rewardManager.activateStorePackage(StorePackageTier.PACKAGE_2)
        assertTrue(activated)

        val state = rewardManager.rewardState.value
        assertEquals(0, state.totalDiamonds) // 16000 spent
        assertEquals(StorePackageTier.PACKAGE_2, state.currentTier)
        assertEquals(128, state.maxCommentsPerMinute)
        assertEquals(16000, state.maxDailyNotes)
    }

    @Test
    fun activateStorePackage_failsWhenInsufficientDiamonds() {
        val success = rewardManager.activateStorePackage(StorePackageTier.PACKAGE_1)
        assertFalse(success)

        val state = rewardManager.rewardState.value
        assertEquals(StorePackageTier.DEFAULT, state.currentTier)
    }

    @Test
    fun claimDailyCheckInReward_claimsDailyDiamondsOncePerDay() {
        assertFalse(rewardManager.rewardState.value.hasCheckedInToday)

        val claimed = rewardManager.claimDailyCheckInReward()
        assertEquals(800, claimed)

        val stateAfterClaim = rewardManager.rewardState.value
        assertEquals(800, stateAfterClaim.totalDiamonds)
        assertTrue(stateAfterClaim.hasCheckedInToday)

        // Attempting second claim on same day returns 0 and keeps total at 800
        val secondClaim = rewardManager.claimDailyCheckInReward()
        assertEquals(0, secondClaim)
        assertEquals(800, rewardManager.rewardState.value.totalDiamonds)
    }

    @Test
    fun transactionLog_recordsEarningsAndPurchases() {
        rewardManager.onNoteCreated()
        rewardManager.onCommentOrReplyCreated()
        rewardManager.claimDailyCheckInReward()

        val txs = rewardManager.transactions.value
        assertEquals(3, txs.size)
        assertEquals(800, txs[0].amount) // latest: daily checkin
        assertEquals(20, txs[1].amount)  // comment
        assertEquals(20, txs[2].amount)  // note
    }
}
