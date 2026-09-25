package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.PinGold
import com.example.ui.theme.PinGoldContainer

data class AvatarData(
    val type: String = "INITIAL", // "INITIAL", "EMOJI", "PRESET", "IMAGE_URI"
    val value: String? = null,
    val bgColor: Long? = null
)

val PRESET_AVATAR_COLORS = listOf(
    0xFF4F46E5, // Indigo
    0xFF06B6D4, // Cyan
    0xFF10B981, // Emerald
    0xFFF59E0B, // Amber
    0xFFEF4444, // Red
    0xFFEC4899, // Pink
    0xFF8B5CF6, // Purple
    0xFF3B82F6, // Blue
    0xFF14B8A6, // Teal
    0xFFF97316, // Orange
    0xFF6366F1, // Violet
    0xFF334155  // Slate
)

val PRESET_EMOJI_CATEGORIES = mapOf(
    "Động vật đáng yêu" to listOf(
        "🐱", "🐶", "🦊", "🐼", "🐯", "🦁", "🐨", "🐰", 
        "🐵", "🐸", "🦉", "🦄", "🐧", "🐬", "🦋", "🐝"
    ),
    "Nhân vật & Phong cách" to listOf(
        "👨‍🚀", "🥷", "🧙‍♂️", "🦸‍♂️", "🎨", "🎮", "🕵️‍♂️", "🎓",
        "👑", "🤖", "🤠", "👩‍💻", "👨‍🎨", "🧑‍🚀", "👸", "🤴"
    ),
    "Biểu tượng & Cảm xúc" to listOf(
        "💎", "⭐", "🔥", "🌸", "🚀", "⚡", "💖", "🎯",
        "🍀", "☕", "📖", "🎵", "🌿", "☀️", "🌙", "✨"
    )
)

@Composable
fun AuthorAvatar(
    name: String,
    isPinned: Boolean = false,
    avatarType: String = "INITIAL",
    avatarValue: String? = null,
    avatarBgColor: Long? = null,
    size: Int = 34,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val initial = name.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"
    val defaultBg = if (isPinned) PinGoldContainer else MaterialTheme.colorScheme.primaryContainer
    val finalBgColor = if (avatarBgColor != null) Color(avatarBgColor) else defaultBg
    val textColor = if (avatarBgColor != null) {
        Color.White
    } else if (isPinned) {
        PinGold
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    val boxModifier = modifier
        .size(size.dp)
        .clip(CircleShape)
        .background(finalBgColor)
        .then(
            if (isPinned) {
                Modifier.border(1.5.dp, PinGold, CircleShape)
            } else {
                Modifier
            }
        )
        .then(
            if (onClick != null) {
                Modifier.clickable { onClick() }
            } else {
                Modifier
            }
        )

    Box(
        modifier = boxModifier,
        contentAlignment = Alignment.Center
    ) {
        when (avatarType) {
            "IMAGE_URI" -> {
                if (!avatarValue.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(avatarValue)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Avatar của $name",
                        modifier = Modifier
                            .size(size.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = (size * 0.42).sp
                    )
                }
            }
            "EMOJI", "PRESET" -> {
                if (!avatarValue.isNullOrBlank()) {
                    Text(
                        text = avatarValue,
                        fontSize = (size * 0.52).sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        fontSize = (size * 0.42).sp
                    )
                }
            }
            else -> { // "INITIAL"
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    fontSize = (size * 0.42).sp
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AvatarPickerDialog(
    currentAuthorName: String,
    initialAvatarType: String,
    initialAvatarValue: String?,
    initialAvatarBgColor: Long?,
    onDismiss: () -> Unit,
    onSaveAvatar: (avatarType: String, avatarValue: String?, avatarBgColor: Long?) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (initialAvatarType == "IMAGE_URI") 2 else if (initialAvatarType in listOf("EMOJI", "PRESET")) 0 else 1) }
    var currentType by remember { mutableStateOf(initialAvatarType) }
    var currentValue by remember { mutableStateOf(initialAvatarValue) }
    var currentBgColor by remember { mutableStateOf(initialAvatarBgColor ?: PRESET_AVATAR_COLORS[0]) }
    var customUrlInput by remember { mutableStateOf(if (initialAvatarType == "IMAGE_URI" && initialAvatarValue?.startsWith("http") == true) initialAvatarValue else "") }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentType = "IMAGE_URI"
            currentValue = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tuỳ chỉnh Avatar",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Live Preview Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Xem trước hiển thị",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AuthorAvatar(
                                name = currentAuthorName,
                                avatarType = currentType,
                                avatarValue = currentValue,
                                avatarBgColor = currentBgColor,
                                size = 56
                            )

                            Column {
                                Text(
                                    text = currentAuthorName.ifBlank { "Tên người dùng" },
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = when (currentType) {
                                        "EMOJI", "PRESET" -> "Avatar Biểu tượng"
                                        "IMAGE_URI" -> "Avatar Ảnh tuỳ chọn"
                                        else -> "Avatar Chữ cái"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Category Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Biểu tượng", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Màu sắc", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Thư viện ảnh", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedTab) {
                    0 -> { // Biểu tượng (Emojis & Presets)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Background Color for Emoji
                            Text(
                                text = "Màu nền avatar:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                PRESET_AVATAR_COLORS.forEach { colorVal ->
                                    val isSelected = currentBgColor == colorVal
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(colorVal))
                                            .clickable { currentBgColor = colorVal }
                                            .then(
                                                if (isSelected) Modifier.border(2.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                                else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            PRESET_EMOJI_CATEGORIES.forEach { (categoryName, emojis) ->
                                Text(
                                    text = categoryName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    emojis.forEach { emoji ->
                                        val isSelected = currentType in listOf("EMOJI", "PRESET") && currentValue == emoji
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                            border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clickable {
                                                    currentType = "EMOJI"
                                                    currentValue = emoji
                                                }
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(text = emoji, fontSize = 22.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    1 -> { // Chữ cái & Màu sắc (Initials & Colors)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Chọn màu nền cho chữ cái đầu:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                PRESET_AVATAR_COLORS.forEach { colorVal ->
                                    val isSelected = currentType == "INITIAL" && currentBgColor == colorVal
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(CircleShape)
                                            .background(Color(colorVal))
                                            .clickable {
                                                currentType = "INITIAL"
                                                currentValue = null
                                                currentBgColor = colorVal
                                            }
                                            .then(
                                                if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                                else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = currentAuthorName.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> { // Thư viện ảnh / Ảnh tuỳ chọn
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("btn_pick_avatar_photo")
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Chọn ảnh từ Thiết bị")
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Hoặc dán liên kết ảnh trực tiếp (URL):",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )

                            OutlinedTextField(
                                value = customUrlInput,
                                onValueChange = {
                                    customUrlInput = it
                                    if (it.isNotBlank()) {
                                        currentType = "IMAGE_URI"
                                        currentValue = it.trim()
                                    }
                                },
                                label = { Text("Đường dẫn ảnh (https://...)") },
                                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_avatar_url_field"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveAvatar(currentType, currentValue, currentBgColor)
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_confirm_avatar_picker")
            ) {
                Text("Áp dụng")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Đóng")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}
