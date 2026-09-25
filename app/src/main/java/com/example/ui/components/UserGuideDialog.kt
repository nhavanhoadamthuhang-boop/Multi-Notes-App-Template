package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class GuideStep(
    val stepNumber: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val keyPoints: List<String>,
    val proTip: String
)

private val GUIDE_STEPS = listOf(
    GuideStep(
        stepNumber = 1,
        title = "Tạo & Chỉnh sửa Ghi chú",
        subtitle = "Soạn thảo nhanh chóng, phân loại theo thư mục và màu sắc",
        icon = Icons.Default.Edit,
        accentColor = Color(0xFF1976D2), // Blue
        keyPoints = listOf(
            "Nhấn nút dấu cộng (+) hoặc chọn 'Tạo ghi chú mới' từ Trình đơn.",
            "Nhập Tiêu đề và Nội dung chi tiết cần ghi nhớ.",
            "Chọn Danh mục (Công việc, Cá nhân, Học tập, Ý tưởng...) hoặc nhập danh mục mới.",
            "Gán Thẻ tag (ngăn cách bằng dấu phẩy) để tìm kiếm nhanh sau này.",
            "Chọn Màu sắc nhận diện cho thẻ ghi chú theo ý thích cá nhân."
        ),
        proTip = "Bạn có thể vuốt thẻ ghi chú sang trái để chuyển nhanh vào Thùng rác hoặc chạm để xem toàn bộ nội dung."
    ),
    GuideStep(
        stepNumber = 2,
        title = "Ghim & Đánh dấu Bookmark",
        subtitle = "Ưu tiên các ghi chú quan trọng và lọc nhanh khi cần",
        icon = Icons.Default.Bookmark,
        accentColor = Color(0xFFE65100), // Orange / Amber
        keyPoints = listOf(
            "Nhấn biểu tượng 📌 Ghim để cố định ghi chú quan trọng luôn hiển thị trên đầu danh sách.",
            "Nhấn biểu tượng 🔖 Đánh dấu (Bookmark) để lưu vào danh sách quan tâm đặc biệt.",
            "Sử dụng Chip lọc 'Đã đánh dấu' ở thanh danh mục để xem riêng các ghi chú được bookmark.",
            "Ghi chú được ghim sẽ có viền vàng nổi bật giúp nhận diện tức thì."
        ),
        proTip = "Tính năng Đánh dấu cũng hoạt động độc lập trên từng bình luận và phản hồi chi tiết!"
    ),
    GuideStep(
        stepNumber = 3,
        title = "Tìm kiếm & Bộ lọc thông minh",
        subtitle = "Tra cứu tức thì theo nội dung, thư mục và thứ tự linh hoạt",
        icon = Icons.Default.Search,
        accentColor = Color(0xFF00897B), // Teal
        keyPoints = listOf(
            "Thanh tìm kiếm hỗ trợ tra cứu tức thì theo Tiêu đề, Nội dung và Thẻ tag.",
            "Thanh Chip danh mục cho phép lọc nhanh ghi chú theo từng chủ đề công việc.",
            "Lọc nhanh theo Thẻ tag cụ thể bằng cách chạm vào tag trên thẻ ghi chú.",
            "Tùy chọn sắp xếp đa dạng: Mới nhất, Cũ nhất, Tiêu đề A-Z hoặc Z-A."
        ),
        proTip = "Nhấn nút 'Tất cả' trên thanh Chip danh mục để nhanh chóng hủy toàn bộ bộ lọc đang áp dụng."
    ),
    GuideStep(
        stepNumber = 4,
        title = "Bình luận & Phản hồi 2 cấp",
        subtitle = "Thảo luận, bổ sung thông tin và trao đổi theo từng ghi chú",
        icon = Icons.AutoMirrored.Filled.Comment,
        accentColor = Color(0xFF7B1FA2), // Purple
        keyPoints = listOf(
            "Chạm vào ghi chú để mở màn hình chi tiết và danh sách bình luận.",
            "Thêm bình luận mới cùng tên người phản hồi và nội dung chi tiết.",
            "Nhấn nút 'Trả lời' ở mỗi bình luận để tạo Phản hồi cấp 2 lồng nhau.",
            "Ghim bình luận quan trọng lên đầu và gắn bookmark theo dõi.",
            "Dễ dàng chỉnh sửa hoặc xóa bình luận bất cứ lúc nào."
        ),
        proTip = "Bình luận bị xóa sẽ được bảo vệ an toàn trong Thùng rác và có thể khôi phục lại nguyên vẹn!"
    ),
    GuideStep(
        stepNumber = 5,
        title = "Thùng rác & Phục hồi an toàn",
        subtitle = "Bảo vệ dữ liệu, tránh xóa nhầm và khôi phục dễ dàng",
        icon = Icons.Default.DeleteSweep,
        accentColor = Color(0xFFD32F2F), // Red
        keyPoints = listOf(
            "Ghi chú và bình luận khi xóa đều được chuyển vào Thùng rác an toàn.",
            "Thời hạn lưu trữ thùng rác từ 7 đến 90 ngày (hoặc vĩnh viễn tùy gói).",
            "Xem danh sách mục đã xóa, số ngày còn lại trước khi tự động dọn.",
            "Nhấn 'Khôi phục' để đưa ghi chú/bình luận về vị trí ban đầu đầy đủ.",
            "Tùy chọn Xóa vĩnh viễn từng mục hoặc 'Dọn sạch thùng rác'."
        ),
        proTip = "Nâng cấp gói tài khoản bằng Kim Cương để gia hạn thời gian lưu trữ Thùng rác lên đến Vô hạn!"
    ),
    GuideStep(
        stepNumber = 6,
        title = "Kim Cương, Chuỗi ngày & Sao lưu",
        subtitle = "Tích lũy phần thưởng, duy trì thói quen và an toàn dữ liệu",
        icon = Icons.Default.Diamond,
        accentColor = Color(0xFF00ACC1), // Cyan
        keyPoints = listOf(
            "Điểm danh mỗi ngày để nhận ngay 800 Kim Cương và tăng Chuỗi ngày (Streak 🔥).",
            "Dùng Kim Cương mua các Gói VIP (Đồng, Bạc, Vàng, Kim Cương, Vô Hạn) và mở khóa Avatar.",
            "Nhập/xuất tệp tin đa định dạng: Xuất và nhập dữ liệu linh hoạt với nhiều định dạng tệp tin (JSON, XML, XLSX, CSV, DOCX, PPTX, TXT, HTML) an toàn để lưu trữ hoặc chuyển máy.",
            "Bật 'Tự động sao lưu' trong Cài đặt để hệ thống tự động lưu trữ định kỳ.",
            "Xem Biểu đồ xu hướng hoạt động 7 ngày để theo dõi thói quen ghi chú."
        ),
        proTip = "Tính năng Nhập/xuất tệp tin đa định dạng hỗ trợ đầy đủ cả Ghi chú, Bình luận, Phản hồi, Thùng rác và trạng thái Đánh dấu!"
    ),
    GuideStep(
        stepNumber = 7,
        title = "Kho lưu trữ & Quản lý tập trung",
        subtitle = "Lưu trữ ghi chú và bình luận gọn gàng, khôi phục linh hoạt",
        icon = Icons.Default.Archive,
        accentColor = Color(0xFF3F51B5), // Indigo
        keyPoints = listOf(
            "Đưa ghi chú hoặc bình luận vào Kho lưu trữ để ẩn khỏi màn hình chính mà không xóa mất.",
            "Truy cập Kho lưu trữ từ Trình đơn để xem lại danh sách ghi chú và bình luận đã lưu trữ.",
            "Tùy chọn tự động lưu trữ các ghi chú đã hoàn thành hoặc cũ hơn 7 ngày chỉ với một chạm.",
            "Khôi phục lại ghi chú hoặc bình luận về màn hình chính bất cứ lúc nào."
        ),
        proTip = "Kho lưu trữ giúp giữ không gian làm việc của bạn luôn gọn gàng mà vẫn bảo toàn đầy đủ dữ liệu quan trọng!"
    ),
    GuideStep(
        stepNumber = 8,
        title = "Thống kê Hoạt động & Mẹo hay",
        subtitle = "Theo dõi tiến độ, tùy chỉnh giao diện và tối ưu trải nghiệm",
        icon = Icons.Default.AutoAwesome,
        accentColor = Color(0xFFC2185B), // Pink / Rose
        keyPoints = listOf(
            "Xem bảng thống kê tổng số ghi chú, bình luận, mục đã đánh dấu và thùng rác.",
            "Theo dõi biểu đồ hoạt động 7 ngày gần nhất để duy trì thói quen ghi chép đều đặn.",
            "Tùy chỉnh chế độ Giao diện (Sáng / Tối / Theo hệ thống) trong phần Cài đặt.",
            "Khám phá kho phần thưởng Kim Cương và nâng cấp các gói tài khoản cao cấp."
        ),
        proTip = "Duy trì ghi chú và điểm danh đều đặn mỗi ngày để nhận phần thưởng 800 Kim Cương và thăng hạng Streak!"
    )
)

@Composable
fun UserGuideDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    initialStepIndex: Int = 0
) {
    var currentStepIndex by remember { mutableIntStateOf(initialStepIndex.coerceIn(0, GUIDE_STEPS.size - 1)) }
    val currentStep = GUIDE_STEPS[currentStepIndex]
    val isLastStep = currentStepIndex == GUIDE_STEPS.size - 1
    val isFirstStep = currentStepIndex == 0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .heightIn(max = 720.dp)
                .clip(RoundedCornerShape(28.dp))
                .testTag("dialog_user_guide"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Hướng Dẫn Sử Dụng",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "8 bước làm chủ ứng dụng Ghi chú",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("guide_btn_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Step Pills / Stepper
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(GUIDE_STEPS) { index, step ->
                        val isSelected = index == currentStepIndex
                        val isCompleted = index < currentStepIndex

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = when {
                                isSelected -> step.accentColor
                                isCompleted -> step.accentColor.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { currentStepIndex = index }
                                .testTag("guide_step_tab_${index + 1}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = step.accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                } else {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isSelected) Color.White.copy(alpha = 0.3f) else step.accentColor.copy(alpha = 0.2f),
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${step.stepNumber}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else step.accentColor
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "Bước ${step.stepNumber}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = when {
                                        isSelected -> Color.White
                                        isCompleted -> step.accentColor
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(14.dp))

                // Step Content with Animation
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState.stepNumber > initialState.stepNumber) {
                            (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> -width } + fadeOut()
                            )
                        } else {
                            (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                slideOutHorizontally { width -> width } + fadeOut()
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    label = "GuideStepTransition"
                ) { step ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Step Hero Banner
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = step.accentColor.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, step.accentColor.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = step.accentColor,
                                    modifier = Modifier.size(52.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = step.icon,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = step.accentColor
                                        ) {
                                            Text(
                                                text = "BƯỚC ${step.stepNumber}/8",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = step.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = step.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Key Points Section
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Nội dung chính & Thao tác:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            step.keyPoints.forEachIndexed { idx, point ->
                                Row(
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = step.accentColor.copy(alpha = 0.15f),
                                        modifier = Modifier
                                            .size(22.dp)
                                            .padding(top = 2.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${idx + 1}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = step.accentColor
                                            )
                                        }
                                    }
                                    Text(
                                        text = point,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Pro Tip Box
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFFFF8E1), // Warm amber background
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFF57C00),
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = "Mẹo hữu ích:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = step.proTip,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF424242)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Navigation Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!isFirstStep) {
                        OutlinedButton(
                            onClick = {
                                if (currentStepIndex > 0) currentStepIndex--
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("guide_btn_prev")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Trước")
                        }
                    } else {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("guide_btn_skip")
                        ) {
                            Text("Bỏ qua", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    // Step Indicator Dots
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GUIDE_STEPS.forEachIndexed { i, s ->
                            val active = i == currentStepIndex
                            Box(
                                modifier = Modifier
                                    .size(if (active) 10.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (active) s.accentColor else MaterialTheme.colorScheme.outlineVariant
                                    )
                            )
                        }
                    }

                    // Next or Finish Button
                    Button(
                        onClick = {
                            if (isLastStep) {
                                onDismiss()
                            } else {
                                currentStepIndex++
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = currentStep.accentColor
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("guide_btn_next")
                    ) {
                        Text(
                            text = if (isLastStep) "Hoàn tất & Bắt đầu" else "Tiếp theo",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (isLastStep) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
