package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoDelete
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.DiamondRewardState
import com.example.data.repository.DiamondTransaction
import com.example.data.repository.DiamondTransactionType
import com.example.data.repository.StorePackageTier
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DiamondCyan = Color(0xFF00B4D8)
private val DiamondBlue = Color(0xFF0077B6)
private val DiamondGold = Color(0xFFFFB703)
private val DiamondPurple = Color(0xFF9D4EDD)
private val DiamondTeal = Color(0xFF06D6A0)
private val DiamondRed = Color(0xFFE63946)
private val DiamondOrange = Color(0xFFFF6D00)
private val DiamondIndigo = Color(0xFF3D5AFE)
private val DiamondEmerald = Color(0xFF00C853)
private val DiamondRuby = Color(0xFFD50000)

@Composable
fun DiamondTopBarBadge(
    rewardState: DiamondRewardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormatter = NumberFormat.getNumberInstance(Locale.GERMANY)

    val tierColor = when (rewardState.currentTier) {
        StorePackageTier.PACKAGE_8 -> DiamondRuby
        StorePackageTier.PACKAGE_7 -> DiamondEmerald
        StorePackageTier.PACKAGE_6 -> DiamondIndigo
        StorePackageTier.PACKAGE_5 -> DiamondOrange
        StorePackageTier.PACKAGE_4 -> DiamondRed
        StorePackageTier.PACKAGE_3 -> DiamondTeal
        StorePackageTier.PACKAGE_2 -> DiamondPurple
        StorePackageTier.PACKAGE_1 -> DiamondGold
        else -> if (rewardState.isGoalCompleted) DiamondGold else MaterialTheme.colorScheme.primary
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = tierColor.copy(alpha = 0.18f),
        border = BorderStroke(1.dp, tierColor.copy(alpha = 0.8f)),
        modifier = modifier.testTag("badge_diamond_topbar")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = when (rewardState.currentTier) {
                    StorePackageTier.PACKAGE_8, StorePackageTier.PACKAGE_7, StorePackageTier.PACKAGE_6 -> Icons.Default.Stars
                    StorePackageTier.PACKAGE_5, StorePackageTier.PACKAGE_4 -> Icons.Default.Diamond
                    StorePackageTier.PACKAGE_3 -> Icons.Default.FlashOn
                    StorePackageTier.PACKAGE_2 -> Icons.Default.Stars
                    StorePackageTier.PACKAGE_1 -> Icons.Default.EmojiEvents
                    else -> if (rewardState.isGoalCompleted) Icons.Default.EmojiEvents else Icons.Default.Diamond
                },
                contentDescription = "Kim cương",
                tint = tierColor,
                modifier = Modifier.size(16.dp)
            )
            AnimatedContent(
                targetState = rewardState.totalDiamonds,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
                    } else {
                        (slideInVertically { height -> -height } + fadeIn()).togetherWith(slideOutVertically { height -> height } + fadeOut())
                    }
                },
                label = "diamond_counter_anim"
            ) { count ->
                Text(
                    text = "${numberFormatter.format(count)} kim cương",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (rewardState.currentTier != StorePackageTier.DEFAULT) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (rewardState.currentTier) {
                        StorePackageTier.PACKAGE_4 -> DiamondRed
                        StorePackageTier.PACKAGE_3 -> DiamondTeal
                        StorePackageTier.PACKAGE_2 -> DiamondPurple
                        else -> DiamondGold
                    }
                ) {
                    Text(
                        text = when (rewardState.currentTier) {
                            StorePackageTier.PACKAGE_4 -> "VIP 4"
                            StorePackageTier.PACKAGE_3 -> "VIP 3"
                            StorePackageTier.PACKAGE_2 -> "VIP 2"
                            else -> "VIP 1"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DiamondProgressCard(
    rewardState: DiamondRewardState,
    onClick: () -> Unit,
    onOpenStore: () -> Unit,
    onOpenHistory: (() -> Unit)? = null,
    onOpenLeaderboard: (() -> Unit)? = null,
    onClaimCheckIn: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val numberFormatter = NumberFormat.getNumberInstance(Locale.GERMANY)
    val progress = (rewardState.totalDiamonds.toFloat() / rewardState.targetDiamonds.toFloat()).coerceIn(0f, 1f)
    val progressPercent = (progress * 100).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("card_diamond_progress"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (rewardState.currentTier) {
                StorePackageTier.PACKAGE_2 -> DiamondPurple.copy(alpha = 0.08f)
                StorePackageTier.PACKAGE_1 -> MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when (rewardState.currentTier) {
                StorePackageTier.PACKAGE_2 -> DiamondPurple.copy(alpha = 0.5f)
                StorePackageTier.PACKAGE_1 -> DiamondGold.copy(alpha = 0.6f)
                else -> if (rewardState.isGoalCompleted) DiamondGold.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header row with Diamond Icon, Goal & Store button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = when (rewardState.currentTier) {
                                    StorePackageTier.PACKAGE_8 -> DiamondRuby.copy(alpha = 0.2f)
                                    StorePackageTier.PACKAGE_7 -> DiamondEmerald.copy(alpha = 0.2f)
                                    StorePackageTier.PACKAGE_6 -> DiamondIndigo.copy(alpha = 0.2f)
                                    StorePackageTier.PACKAGE_5 -> DiamondOrange.copy(alpha = 0.2f)
                                    StorePackageTier.PACKAGE_4 -> DiamondRed.copy(alpha = 0.2f)
                                    StorePackageTier.PACKAGE_3 -> DiamondTeal.copy(alpha = 0.2f)
                                    StorePackageTier.PACKAGE_2 -> DiamondPurple.copy(alpha = 0.2f)
                                    StorePackageTier.PACKAGE_1 -> DiamondGold.copy(alpha = 0.2f)
                                    else -> DiamondCyan.copy(alpha = 0.2f)
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (rewardState.currentTier) {
                                StorePackageTier.PACKAGE_8 -> Icons.Default.Stars
                                StorePackageTier.PACKAGE_7 -> Icons.Default.EmojiEvents
                                StorePackageTier.PACKAGE_6 -> Icons.Default.Stars
                                StorePackageTier.PACKAGE_5 -> Icons.Default.Diamond
                                StorePackageTier.PACKAGE_4 -> Icons.Default.Diamond
                                StorePackageTier.PACKAGE_3 -> Icons.Default.FlashOn
                                StorePackageTier.PACKAGE_2 -> Icons.Default.Stars
                                StorePackageTier.PACKAGE_1 -> Icons.Default.EmojiEvents
                                else -> Icons.Default.Diamond
                            },
                            contentDescription = "Mục tiêu kim cương",
                            tint = when (rewardState.currentTier) {
                                StorePackageTier.PACKAGE_8 -> DiamondRuby
                                StorePackageTier.PACKAGE_7 -> DiamondEmerald
                                StorePackageTier.PACKAGE_6 -> DiamondIndigo
                                StorePackageTier.PACKAGE_5 -> DiamondOrange
                                StorePackageTier.PACKAGE_4 -> DiamondRed
                                StorePackageTier.PACKAGE_3 -> DiamondTeal
                                StorePackageTier.PACKAGE_2 -> DiamondPurple
                                StorePackageTier.PACKAGE_1 -> DiamondGold
                                else -> DiamondCyan
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${numberFormatter.format(rewardState.totalDiamonds)} kim cương",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (rewardState.currentTier != StorePackageTier.DEFAULT) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = when (rewardState.currentTier) {
                                        StorePackageTier.PACKAGE_8 -> DiamondRuby
                                        StorePackageTier.PACKAGE_7 -> DiamondEmerald
                                        StorePackageTier.PACKAGE_6 -> DiamondIndigo
                                        StorePackageTier.PACKAGE_5 -> DiamondOrange
                                        StorePackageTier.PACKAGE_4 -> DiamondRed
                                        StorePackageTier.PACKAGE_3 -> DiamondTeal
                                        StorePackageTier.PACKAGE_2 -> DiamondPurple
                                        else -> DiamondGold
                                    }
                                ) {
                                    Text(
                                        text = rewardState.currentTier.title,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "+20 KC/ghi chú & cmt • Điểm danh: +800 KC",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onOpenLeaderboard != null) {
                        OutlinedButton(
                            onClick = onOpenLeaderboard,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("button_open_leaderboard")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = DiamondGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Xếp hạng",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (onOpenHistory != null) {
                        OutlinedButton(
                            onClick = onOpenHistory,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("button_open_history")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Lịch sử",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Cửa Hàng Button
                    FilledTonalButton(
                        onClick = onOpenStore,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("button_open_store")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cửa hàng",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Daily Check-In Banner inside card if not checked in
            if (!rewardState.hasCheckedInToday && onClaimCheckIn != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    onClick = onClaimCheckIn,
                    shape = RoundedCornerShape(12.dp),
                    color = DiamondGold.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, DiamondGold.copy(alpha = 0.8f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("banner_claim_checkin_inside_card")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = null,
                                tint = DiamondGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Điểm danh hôm nay để nhận +800 kim cương!",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DiamondGold
                        ) {
                            Text(
                                text = "Điểm danh ngay",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("progress_diamonds_goal"),
                color = when (rewardState.currentTier) {
                    StorePackageTier.PACKAGE_8 -> DiamondRuby
                    StorePackageTier.PACKAGE_7 -> DiamondEmerald
                    StorePackageTier.PACKAGE_6 -> DiamondIndigo
                    StorePackageTier.PACKAGE_5 -> DiamondOrange
                    StorePackageTier.PACKAGE_4 -> DiamondRed
                    StorePackageTier.PACKAGE_3 -> DiamondTeal
                    StorePackageTier.PACKAGE_2 -> DiamondPurple
                    StorePackageTier.PACKAGE_1 -> DiamondGold
                    else -> DiamondCyan
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Limit Progress Indicators Grid
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Storage Capacity Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
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
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Dung lượng lưu trữ:",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = if (rewardState.usedStorageMB < 1024f) 
                                "${numberFormatter.format(rewardState.usedStorageMB.toInt())} MB / ${numberFormatter.format(rewardState.maxStorageGB)} GB"
                            else 
                                "${String.format(java.util.Locale.getDefault(), "%.2f", rewardState.usedStorageGB)} GB / ${numberFormatter.format(rewardState.maxStorageGB)} GB",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { rewardState.storageProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(2.5.dp))
                            .testTag("progress_storage_capacity"),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                }

                // 2. Comments Per Page Progress Bar
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
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
                                imageVector = Icons.Default.Comment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Bình luận / Trang:",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${numberFormatter.format(rewardState.commentsPerPage)} bình luận/trang",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    LinearProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    )
                }

                // 2. Today's Notes & Comment Speed Progress Bars Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Today's Notes Limit Progress Bar
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NoteAdd,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Ghi chú ngày:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${numberFormatter.format(rewardState.notesCreatedToday)}/${numberFormatter.format(rewardState.maxDailyNotes)}",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (rewardState.notesCreatedToday.toFloat() / rewardState.maxDailyNotes.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    }

                    // Comment Rate Speed Progress Bar
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = if (rewardState.commentsInCurrentMinute >= (rewardState.maxCommentsPerMinute - 4)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Tốc độ cmt:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${rewardState.commentsInCurrentMinute}/${rewardState.maxCommentsPerMinute}/phút",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (rewardState.commentsInCurrentMinute >= (rewardState.maxCommentsPerMinute - 4)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (rewardState.commentsInCurrentMinute.toFloat() / rewardState.maxCommentsPerMinute.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = if (rewardState.commentsInCurrentMinute >= (rewardState.maxCommentsPerMinute - 4)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                            trackColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyCheckInCard(
    rewardState: DiamondRewardState,
    onClaimCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasCheckedIn = rewardState.hasCheckedInToday

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_daily_checkin"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasCheckedIn)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (hasCheckedIn)
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            else
                DiamondGold
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = if (hasCheckedIn) Color(0xFFE8F5E9) else DiamondGold.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hasCheckedIn) Icons.Default.CheckCircle else Icons.Default.Stars,
                        contentDescription = "Điểm danh mỗi ngày",
                        tint = if (hasCheckedIn) Color(0xFF2E7D32) else DiamondGold,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Điểm Danh Mỗi Ngày",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (hasCheckedIn) Color(0xFFC8E6C9) else DiamondGold
                        ) {
                            Text(
                                text = "+800 KC",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (hasCheckedIn) Color(0xFF1B5E20) else Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (hasCheckedIn)
                            "Bạn đã điểm danh hôm nay! Gói Mặc Định active: 8.000 ghi chú/ngày • 32 cmt/phút • 200 cmt/trang • Thùng rác 60 ngày."
                        else
                            "Điểm danh nhận ngay +800 KC & Gói Mặc Định: 8.000 ghi chú/ngày • 32 cmt/phút • 200 cmt/trang • Thùng rác 60 ngày!",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = if (hasCheckedIn) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (!hasCheckedIn) {
                Button(
                    onClick = onClaimCheckIn,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DiamondGold),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("btn_claim_daily_checkin")
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Điểm danh",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Đã nhận",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DiamondGoalDialog(
    rewardState: DiamondRewardState,
    onOpenStore: () -> Unit,
    onClaimCheckIn: (() -> Unit)? = null,
    onRedeemContent: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val numberFormatter = NumberFormat.getNumberInstance(Locale.GERMANY)
    val progress = (rewardState.totalDiamonds.toFloat() / rewardState.targetDiamonds.toFloat()).coerceIn(0f, 1f)
    val progressPercent = (progress * 100).toInt()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = DiamondCyan,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Tích Lũy & Hạn Mức",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag("dialog_diamond_summary"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Daily Check-In Card
                if (onClaimCheckIn != null) {
                    DailyCheckInCard(
                        rewardState = rewardState,
                        onClaimCheckIn = onClaimCheckIn
                    )
                }

                // Goal Card Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TỔNG KIM CƯƠNG HIỆN CÓ",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${numberFormatter.format(rewardState.totalDiamonds)} kim cương",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Text(
                            text = "Tiến độ: $progressPercent% mục tiêu 8.000 kim cương",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = if (rewardState.isGoalCompleted) DiamondGold else DiamondCyan,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }

                // Current Tier Info Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Gói hiện tại: ${rewardState.currentTier.title}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = rewardState.currentTier.badgeLabel,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "• Giới hạn đăng bình luận: ${rewardState.commentsPostedToday} / ${numberFormatter.format(rewardState.maxDailyComments)} bình luận & phản hồi/ngày (Hôm nay)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "• Tốc độ bình luận: ${rewardState.maxCommentsPerMinute} bình luận & phản hồi/phút (Hiện tại: ${rewardState.commentsInCurrentMinute})",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "• Giới hạn tạo ghi chú: ${numberFormatter.format(rewardState.maxDailyNotes)} ghi chú/ngày (Hôm nay: ${numberFormatter.format(rewardState.notesCreatedToday)})",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "• Thời gian lưu thùng rác: ${rewardState.trashRetentionDays} ngày trước khi xoá vĩnh viễn",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // 20 Diamonds Redemption Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DiamondCyan.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, DiamondCyan.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = null,
                                tint = DiamondCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Quy Đổi 20 Kim Cương / Mục",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "• Quy đổi 1 ghi chú mới = 20 viên kim cương 💎\n• Quy đổi 1 bình luận mới = 20 viên kim cương 💎\n• Quy đổi 1 phản hồi mới = 20 viên kim cương 💎\n• Tổng số mục đã đóng góp: ${numberFormatter.format(rewardState.totalNotesCreatedAllTime)} ghi chú, ${numberFormatter.format(rewardState.totalCommentsAllTime)} bình luận & phản hồi.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (onRedeemContent != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            FilledTonalButton(
                                onClick = onRedeemContent,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Quy đổi dữ liệu hiện có (+20 💎/mục)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Quick store banner button
                Button(
                    onClick = {
                        onDismiss()
                        onOpenStore()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mở Cửa Hàng Gói Mua & Nâng Cấp")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
fun DiamondStoreDialog(
    rewardState: DiamondRewardState,
    onTopUp: (Int) -> Unit,
    onActivatePackage: (StorePackageTier) -> Unit,
    onTopUpAndActivate: ((StorePackageTier) -> Unit)? = null,
    onClaimCheckIn: (() -> Unit)? = null,
    onOpenHistory: (() -> Unit)? = null,
    onRedeemContent: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val numberFormatter = NumberFormat.getNumberInstance(Locale.GERMANY)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Cửa Hàng Gói Mua",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag("dialog_diamond_store"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Daily Check-In Card
                if (onClaimCheckIn != null) {
                    DailyCheckInCard(
                        rewardState = rewardState,
                        onClaimCheckIn = onClaimCheckIn
                    )
                }

                // Balance Header Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Số dư hiện tại",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${numberFormatter.format(rewardState.totalDiamonds)} kim cương",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (onOpenHistory != null) {
                                Surface(
                                    onClick = onOpenHistory,
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = "Lịch sử",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = rewardState.currentTier.title,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                // 20-Diamond Redemption Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DiamondCyan.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, DiamondCyan.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CurrencyExchange,
                                contentDescription = null,
                                tint = DiamondCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Quy Đổi Nội Dung: 20 💎 / Mục",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = "• Quy đổi 1 ghi chú mới = +20 viên kim cương 💎\n• Quy đổi 1 bình luận mới = +20 viên kim cương 💎\n• Quy đổi 1 phản hồi mới = +20 viên kim cương 💎",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (onRedeemContent != null) {
                            FilledTonalButton(
                                onClick = onRedeemContent,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Quy đổi dữ liệu hiện có (+20 💎/mục)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Text(
                    text = "CÁC GÓI NÂNG CẤP HẠN MỨC (CẤP 1 - 8 & MẶC ĐỊNH)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )

                // Package Default Card (Free)
                StorePackageCard(
                    tier = StorePackageTier.DEFAULT,
                    isActive = rewardState.currentTier == StorePackageTier.DEFAULT,
                    userDiamonds = rewardState.totalDiamonds,
                    accentColor = MaterialTheme.colorScheme.secondary,
                    icon = Icons.Default.Inventory2,
                    onActivate = { onActivatePackage(StorePackageTier.DEFAULT) }
                )

                // Package 1 Card (8.000 Diamonds)
                StorePackageCard(
                    tier = StorePackageTier.PACKAGE_1,
                    isActive = rewardState.currentTier == StorePackageTier.PACKAGE_1,
                    userDiamonds = rewardState.totalDiamonds,
                    accentColor = DiamondGold,
                    icon = Icons.Default.EmojiEvents,
                    onActivate = { onActivatePackage(StorePackageTier.PACKAGE_1) }
                )

                // Package 2 Card (16.000 Diamonds)
                StorePackageCard(
                    tier = StorePackageTier.PACKAGE_2,
                    isActive = rewardState.currentTier == StorePackageTier.PACKAGE_2,
                    userDiamonds = rewardState.totalDiamonds,
                    accentColor = DiamondPurple,
                    icon = Icons.Default.Stars,
                    onActivate = { onActivatePackage(StorePackageTier.PACKAGE_2) }
                )

                // Package 3 Card (32.000 Diamonds)
                StorePackageCard(
                    tier = StorePackageTier.PACKAGE_3,
                    isActive = rewardState.currentTier == StorePackageTier.PACKAGE_3,
                    userDiamonds = rewardState.totalDiamonds,
                    accentColor = DiamondTeal,
                    icon = Icons.Default.FlashOn,
                    onActivate = { onActivatePackage(StorePackageTier.PACKAGE_3) }
                )

                // Package 4 Card (64.000 Diamonds)
                StorePackageCard(
                    tier = StorePackageTier.PACKAGE_4,
                    isActive = rewardState.currentTier == StorePackageTier.PACKAGE_4,
                    userDiamonds = rewardState.totalDiamonds,
                    accentColor = DiamondRed,
                    icon = Icons.Default.Diamond,
                    onActivate = { onActivatePackage(StorePackageTier.PACKAGE_4) }
                )

                // Package 5 Card (128.000 Diamonds)
                StorePackageCard(
                    tier = StorePackageTier.PACKAGE_5,
                    isActive = rewardState.currentTier == StorePackageTier.PACKAGE_5,
                    userDiamonds = rewardState.totalDiamonds,
                    accentColor = DiamondOrange,
                    icon = Icons.Default.Diamond,
                    onActivate = { onActivatePackage(StorePackageTier.PACKAGE_5) }
                )

                // Package 6 Card (256.000 Diamonds)
                StorePackageCard(
                    tier = StorePackageTier.PACKAGE_6,
                    isActive = rewardState.currentTier == StorePackageTier.PACKAGE_6,
                    userDiamonds = rewardState.totalDiamonds,
                    accentColor = DiamondIndigo,
                    icon = Icons.Default.Stars,
                    onActivate = { onActivatePackage(StorePackageTier.PACKAGE_6) }
                )

                // Package 7 Card (512.000 Diamonds)
                StorePackageCard(
                    tier = StorePackageTier.PACKAGE_7,
                    isActive = rewardState.currentTier == StorePackageTier.PACKAGE_7,
                    userDiamonds = rewardState.totalDiamonds,
                    accentColor = DiamondEmerald,
                    icon = Icons.Default.EmojiEvents,
                    onActivate = { onActivatePackage(StorePackageTier.PACKAGE_7) }
                )

                // Package 8 Card (1.024.000 Diamonds - VIP)
                StorePackageCard(
                    tier = StorePackageTier.PACKAGE_8,
                    isActive = rewardState.currentTier == StorePackageTier.PACKAGE_8,
                    userDiamonds = rewardState.totalDiamonds,
                    accentColor = DiamondRuby,
                    icon = Icons.Default.Stars,
                    onActivate = { onActivatePackage(StorePackageTier.PACKAGE_8) }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "NẠP KIM CƯƠNG NHANH",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )

                // Quick Top Up Buttons Row 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onTopUp(8000) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+8k KC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onTopUp(16000) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+16k KC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onTopUp(32000) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+32k KC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onTopUp(64000) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+64k KC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Quick Top Up Buttons Row 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onTopUp(128000) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+128k KC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onTopUp(256000) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+256k KC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onTopUp(512000) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+512k KC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onTopUp(1024000) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+1.024k KC", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Đóng")
            }
        }
    )
}

@Composable
private fun StorePackageCard(
    tier: StorePackageTier,
    isActive: Boolean,
    userDiamonds: Int,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onActivate: () -> Unit
) {
    val numberFormatter = NumberFormat.getNumberInstance(Locale.GERMANY)
    val canAfford = userDiamonds >= tier.diamondPrice

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) accentColor.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(
            width = if (isActive) 2.dp else 1.dp,
            color = if (isActive) accentColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header: Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(accentColor.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = tier.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = accentColor, modifier = Modifier.size(13.dp))
                            Text(
                                text = if (tier.diamondPrice == 0) "Miễn phí hoàn toàn" else "${numberFormatter.format(tier.diamondPrice)} Kim Cương",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                    }
                }

                if (isActive) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = accentColor
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Text(
                                text = "Đang áp dụng",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Benefits Grid Block
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Feature Grid Row 1
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Forum, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                            Text(
                                text = "${numberFormatter.format(tier.maxDailyComments)} cmt/ngày",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.NoteAdd, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                            Text(
                                text = "${numberFormatter.format(tier.maxDailyNotes)} ghi chú/ngày",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Feature Grid Row 2
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(15.dp))
                            Text(
                                text = "${tier.maxCommentsPerMinute} cmt/phút",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.AutoDelete, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(15.dp))
                            Text(
                                text = "Thùng rác ${tier.trashRetentionDays} ngày",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Storage Progress Bar Row
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(15.dp))
                                Text(
                                    text = "Dung lượng lưu trữ:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "${numberFormatter.format(tier.maxStorageGB)} GB",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (tier.maxStorageGB.toFloat() / 1024f).coerceIn(0.01f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .testTag("progress_package_storage_${tier.id}"),
                            color = accentColor,
                            trackColor = accentColor.copy(alpha = 0.15f)
                        )
                    }
                }
            }

            // Action Button
            if (!isActive) {
                Button(
                    onClick = onActivate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) accentColor else MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = if (canAfford) Icons.Default.CheckCircle else Icons.Default.Diamond,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (canAfford)
                            "Kích hoạt gói (${numberFormatter.format(tier.diamondPrice)} 💎)"
                        else
                            "Cần thêm ${numberFormatter.format(tier.diamondPrice - userDiamonds)} 💎 để mua",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DiamondTransactionLogDialog(
    transactions: List<DiamondTransaction>,
    rewardState: DiamondRewardState,
    onDismiss: () -> Unit
) {
    val numberFormatter = NumberFormat.getNumberInstance(Locale.GERMANY)
    val dateTimeFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())

    var selectedFilter by remember { mutableStateOf(0) }

    val filteredTransactions = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            1 -> transactions.filter { it.amount > 0 }
            2 -> transactions.filter { it.amount < 0 }
            else -> transactions
        }
    }

    val totalEarned = remember(transactions) {
        transactions.filter { it.amount > 0 }.sumOf { it.amount }
    }
    val totalSpent = remember(transactions) {
        transactions.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = "Lịch Sử Giao Dịch",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dialog_diamond_transaction_log"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Balance & Summary Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Số dư hiện tại", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    numberFormatter.format(rewardState.totalDiamonds),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    imageVector = Icons.Default.Diamond,
                                    contentDescription = "Kim Cương",
                                    tint = Color(0xFFFFB703),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Đã nhận", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32))
                                Text("+${numberFormatter.format(totalEarned)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Đã dùng", style = MaterialTheme.typography.labelSmall, color = Color(0xFFC62828))
                                Text("-${numberFormatter.format(totalSpent)}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color(0xFFC62828))
                            }
                        }
                    }
                }

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Tất cả", "Cộng (+)", "Trừ (-)").forEachIndexed { index, label ->
                        FilterChip(
                            selected = selectedFilter == index,
                            onClick = { selectedFilter = index },
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }

                // List or Empty State
                if (filteredTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.History, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(36.dp))
                            Text("Chưa có lịch sử giao dịch nào", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 320.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredTransactions, key = { it.id }) { tx ->
                            TransactionItemRow(tx = tx, numberFormatter = numberFormatter, dateTimeFormat = dateTimeFormat)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
private fun TransactionItemRow(
    tx: DiamondTransaction,
    numberFormatter: NumberFormat,
    dateTimeFormat: SimpleDateFormat
) {
    val isPositive = tx.amount >= 0
    val accentColor = if (isPositive) Color(0xFF2E7D32) else Color(0xFFC62828)
    val formattedTime = remember(tx.timestamp) { dateTimeFormat.format(Date(tx.timestamp)) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (tx.type) {
                            DiamondTransactionType.NOTE_CREATED -> Icons.Default.NoteAdd
                            DiamondTransactionType.COMMENT_CREATED -> Icons.Default.Comment
                            DiamondTransactionType.DAILY_CHECKIN -> Icons.Default.Stars
                            DiamondTransactionType.TOP_UP -> Icons.Default.Diamond
                            DiamondTransactionType.PACKAGE_PURCHASE -> Icons.Default.ShoppingBag
                            DiamondTransactionType.REDEMPTION_CONVERT -> Icons.Default.CurrencyExchange
                        },
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = accentColor.copy(alpha = 0.12f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isPositive) "+${numberFormatter.format(tx.amount)}" else "-${numberFormatter.format(kotlin.math.abs(tx.amount))}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentColor
                    )
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = "Kim Cương",
                        tint = accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}
