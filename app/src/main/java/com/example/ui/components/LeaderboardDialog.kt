package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.DiamondRewardState
import com.example.data.repository.StreakState
import com.example.data.repository.StorePackageTier
import java.text.NumberFormat
import java.util.Locale

private val DiamondGold = Color(0xFFFFB703)
private val DiamondPurple = Color(0xFF9D4EDD)
private val DiamondCyan = Color(0xFF00B4D8)
private val DiamondTeal = Color(0xFF06D6A0)
private val TrophyGold = Color(0xFFFFD700)
private val TrophySilver = Color(0xFFC0C0C0)
private val TrophyBronze = Color(0xFFCD7F32)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val diamonds: Int,
    val tier: StorePackageTier,
    val notesCount: Int,
    val commentsCount: Int,
    val streakDays: Int,
    val isCurrentUser: Boolean = false,
    val avatarColor: Color
)

@Composable
fun LeaderboardDialog(
    rewardState: DiamondRewardState,
    streakState: StreakState,
    onDismiss: () -> Unit,
    onOpenStore: () -> Unit
) {
    val numberFormatter = NumberFormat.getNumberInstance(Locale.GERMANY)
    var selectedTab by remember { mutableStateOf(0) } // 0: Toàn thời gian, 1: Tháng này, 2: Tuần này

    // Construct community leaderboard dynamically incorporating current user stats
    val currentUserDiamonds = rewardState.totalDiamonds
    val currentUserNotes = rewardState.notesCreatedToday + 124 // simulated history notes
    val currentUserComments = rewardState.commentsInCurrentMinute + 450
    val currentUserStreak = streakState.currentStreak.coerceAtLeast(1)

    val baseUsers = listOf(
        LeaderboardUser(1, "Minh Khôi (Pro VIP)", 215400, StorePackageTier.PACKAGE_4, 1850, 4200, 85, false, Color(0xFFE63946)),
        LeaderboardUser(2, "Hà An (Top Contributor)", 164200, StorePackageTier.PACKAGE_3, 1420, 3100, 64, false, Color(0xFF9D4EDD)),
        LeaderboardUser(3, "Đức Trí (Master Creator)", 118500, StorePackageTier.PACKAGE_2, 980, 2400, 45, false, Color(0xFF457B9D)),
        LeaderboardUser(4, "Thu Trang", 78900, StorePackageTier.PACKAGE_1, 650, 1800, 30, false, Color(0xFF2A9D8F)),
        LeaderboardUser(5, "Hoàng Long", 54300, StorePackageTier.PACKAGE_1, 490, 1250, 22, false, Color(0xFFE76F51)),
        LeaderboardUser(6, "Phương Thảo", 38200, StorePackageTier.DEFAULT, 310, 890, 14, false, Color(0xFFF4A261)),
        LeaderboardUser(7, "Gia Hân", 24500, StorePackageTier.DEFAULT, 210, 620, 9, false, Color(0xFF264653)),
        LeaderboardUser(8, "Quang Huy", 15600, StorePackageTier.DEFAULT, 150, 410, 6, false, Color(0xFF8338EC)),
        LeaderboardUser(9, "Thanh Mai", 9400, StorePackageTier.DEFAULT, 95, 280, 4, false, Color(0xFFFF006E))
    )

    // Insert current user into appropriate sorted position
    val currentUserItem = LeaderboardUser(
        rank = 0, // will compute after sorting
        name = "Bạn (Thành viên hiện tại)",
        diamonds = currentUserDiamonds,
        tier = rewardState.currentTier,
        notesCount = currentUserNotes,
        commentsCount = currentUserComments,
        streakDays = currentUserStreak,
        isCurrentUser = true,
        avatarColor = DiamondCyan
    )

    val allUsers = (baseUsers + currentUserItem).sortedByDescending { it.diamonds }
    val rankedUsers = allUsers.mapIndexed { index, user -> user.copy(rank = index + 1) }
    val myUserRank = rankedUsers.first { it.isCurrentUser }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = DiamondGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = "Bảng Xếp Hạng Kim Cương",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Vinh danh thành viên tích cực nhất cộng đồng",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState())
                    .testTag("dialog_leaderboard"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tab Selection Buttons
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    indicator = {},
                    divider = {}
                ) {
                    val tabs = listOf("Toàn thời gian", "Tháng này", "Tuần này")
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .padding(4.dp),
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                // Current User Rank Banner Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
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
                                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "#${myUserRank.rank}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Column {
                                Text(
                                    text = "Thứ hạng của bạn",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${numberFormatter.format(myUserRank.diamonds)} Kim Cương",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Button(
                            onClick = onOpenStore,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Diamond, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tăng hạng", fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "TOP CAO THỦ CỘNG ĐỒNG",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )

                // User Rank Items List
                rankedUsers.forEach { user ->
                    LeaderboardUserRow(user = user, numberFormatter = numberFormatter)
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
private fun LeaderboardUserRow(
    user: LeaderboardUser,
    numberFormatter: NumberFormat
) {
    val rankBadgeColor = when (user.rank) {
        1 -> TrophyGold
        2 -> TrophySilver
        3 -> TrophyBronze
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val rankTextColor = if (user.rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isCurrentUser)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = BorderStroke(
            width = if (user.isCurrentUser) 1.5.dp else 1.dp,
            color = if (user.isCurrentUser)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        )
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
                // Rank Circle
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(rankBadgeColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.rank <= 3) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = "${user.rank}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = rankTextColor
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (user.isCurrentUser) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (user.isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                        if (user.tier != StorePackageTier.DEFAULT) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when (user.tier) {
                                    StorePackageTier.PACKAGE_4 -> DiamondPurple
                                    StorePackageTier.PACKAGE_3 -> DiamondTeal
                                    StorePackageTier.PACKAGE_2 -> DiamondGold
                                    else -> DiamondCyan
                                }
                            ) {
                                Text(
                                    text = user.tier.title,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notes,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${numberFormatter.format(user.notesCount)} ghi chú",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Comment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${numberFormatter.format(user.commentsCount)} cmt",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = Color(0xFFFF5722),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "${user.streakDays}d",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 10.sp,
                                color = Color(0xFFFF5722)
                            )
                        }
                    }
                }
            }

            // Diamonds Count Badge
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Diamond,
                        contentDescription = null,
                        tint = DiamondGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = numberFormatter.format(user.diamonds),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = "Kim cương",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
