package com.example

import com.example.data.backup.MultiFormatBackupHelper
import com.example.data.local.CommentEntity
import com.example.data.local.NoteEntity
import com.example.data.repository.StorePackageTier
import com.example.ui.util.DateUtils
import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testDateUtilsFormatWithWeeks() {
    val now = System.currentTimeMillis()
    val formattedNow = DateUtils.formatDetailedElapsedTime(now + 1000)
    assertEquals("0 năm 0 tháng 0 tuần 0 ngày 0 giờ 0 phút 0 giây trước", formattedNow)

    // Test a date 1 year, 1 month, 0 weeks, 0 days, 0 hours, 0 mins, 0 secs ago
    val pastCal = Calendar.getInstance().apply {
      timeInMillis = now
      add(Calendar.YEAR, -1)
      add(Calendar.MONTH, -1)
    }
    val formattedPast = DateUtils.formatDetailedElapsedTime(pastCal.timeInMillis)
    assertTrue(formattedPast.contains("năm"))
    assertTrue(formattedPast.contains("tháng"))
    assertTrue(formattedPast.contains("tuần"))
    assertTrue(formattedPast.contains("ngày"))
    assertTrue(formattedPast.contains("giờ"))
    assertTrue(formattedPast.contains("phút"))
    assertTrue(formattedPast.contains("giây trước"))
  }

  @Test
  fun testStorePackageTiersLevelsAndLimits() {
    val defaultTier = StorePackageTier.DEFAULT
    assertEquals(0, defaultTier.diamondPrice)
    assertEquals(8_000, defaultTier.maxDailyComments)
    assertEquals(32, defaultTier.maxCommentsPerMinute)
    assertEquals(200, defaultTier.commentsPerPage)
    assertEquals(4_000, defaultTier.maxDailyNotes)
    assertEquals(60, defaultTier.trashRetentionDays)
    assertEquals(8, defaultTier.maxStorageGB)

    val tier1 = StorePackageTier.PACKAGE_1
    assertEquals(8_000, tier1.diamondPrice)
    assertEquals(16_000, tier1.maxDailyComments)
    assertEquals(64, tier1.maxCommentsPerMinute)
    assertEquals(400, tier1.commentsPerPage)
    assertEquals(8_000, tier1.maxDailyNotes)
    assertEquals(120, tier1.trashRetentionDays)
    assertEquals(16, tier1.maxStorageGB)

    val tier2 = StorePackageTier.PACKAGE_2
    assertEquals(16_000, tier2.diamondPrice)
    assertEquals(32_000, tier2.maxDailyComments)
    assertEquals(128, tier2.maxCommentsPerMinute)
    assertEquals(800, tier2.commentsPerPage)
    assertEquals(16_000, tier2.maxDailyNotes)
    assertEquals(180, tier2.trashRetentionDays)
    assertEquals(24, tier2.maxStorageGB)

    val tier3 = StorePackageTier.PACKAGE_3
    assertEquals(32_000, tier3.diamondPrice)
    assertEquals(64_000, tier3.maxDailyComments)
    assertEquals(256, tier3.maxCommentsPerMinute)
    assertEquals(1_600, tier3.commentsPerPage)
    assertEquals(32_000, tier3.maxDailyNotes)
    assertEquals(360, tier3.trashRetentionDays)
    assertEquals(32, tier3.maxStorageGB)

    val tier4 = StorePackageTier.PACKAGE_4
    assertEquals(64_000, tier4.diamondPrice)
    assertEquals(128_000, tier4.maxDailyComments)
    assertEquals(512, tier4.maxCommentsPerMinute)
    assertEquals(3_200, tier4.commentsPerPage)
    assertEquals(64_000, tier4.maxDailyNotes)
    assertEquals(720, tier4.trashRetentionDays)
    assertEquals(64, tier4.maxStorageGB)

    val tier5 = StorePackageTier.PACKAGE_5
    assertEquals(128_000, tier5.diamondPrice)
    assertEquals(256_000, tier5.maxDailyComments)
    assertEquals(1_024, tier5.maxCommentsPerMinute)
    assertEquals(6_400, tier5.commentsPerPage)
    assertEquals(128_000, tier5.maxDailyNotes)
    assertEquals(1_440, tier5.trashRetentionDays)
    assertEquals(128, tier5.maxStorageGB)

    val tier6 = StorePackageTier.PACKAGE_6
    assertEquals(256_000, tier6.diamondPrice)
    assertEquals(512_000, tier6.maxDailyComments)
    assertEquals(2_048, tier6.maxCommentsPerMinute)
    assertEquals(12_800, tier6.commentsPerPage)
    assertEquals(256_000, tier6.maxDailyNotes)
    assertEquals(2_880, tier6.trashRetentionDays)
    assertEquals(256, tier6.maxStorageGB)

    val tier7 = StorePackageTier.PACKAGE_7
    assertEquals(512_000, tier7.diamondPrice)
    assertEquals(1_024_000, tier7.maxDailyComments)
    assertEquals(4_096, tier7.maxCommentsPerMinute)
    assertEquals(25_600, tier7.commentsPerPage)
    assertEquals(512_000, tier7.maxDailyNotes)
    assertEquals(5_760, tier7.trashRetentionDays)
    assertEquals(512, tier7.maxStorageGB)

    val tier8 = StorePackageTier.PACKAGE_8
    assertEquals(1_024_000, tier8.diamondPrice)
    assertEquals(2_048_000, tier8.maxDailyComments)
    assertEquals(8_192, tier8.maxCommentsPerMinute)
    assertEquals(51_200, tier8.commentsPerPage)
    assertEquals(1_024_000, tier8.maxDailyNotes)
    assertEquals(11_520, tier8.trashRetentionDays)
    assertEquals(1_024, tier8.maxStorageGB)
  }

  @Test
  fun testXmlExportAndParse() {
    val note = NoteEntity(
      id = 100L,
      title = "Ghi chú XML",
      description = "Nội dung ghi chú kiểm thử",
      category = "Kiểm thử",
      tags = "test,xml",
      createdAt = 1000L,
      updatedAt = 2000L
    )
    val comment = CommentEntity(
      id = 200L,
      noteId = 100L,
      authorName = "Đàm Tường Quân",
      content = "Bình luận thử nghiệm",
      createdAt = 1500L
    )

    val xmlString = MultiFormatBackupHelper.exportToXml(listOf(note), listOf(comment))
    assertTrue(xmlString.contains("<note id=\"100\""))
    assertTrue(xmlString.contains("<title>Ghi chú XML</title>"))
    assertTrue(xmlString.contains("Bình luận thử nghiệm"))

    val parsedResult = MultiFormatBackupHelper.parseXml(xmlString)
    assertTrue(parsedResult.isSuccess)
    val backupData = parsedResult.getOrThrow()
    assertEquals(1, backupData.notes.size)
    assertEquals("Ghi chú XML", backupData.notes[0].title)
    assertEquals(1, backupData.notes[0].comments.size)
    assertEquals("Bình luận thử nghiệm", backupData.notes[0].comments[0].content)
  }
}
