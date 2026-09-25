package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LabelImportant
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.NoteEntity

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ManageLabelsDialog(
    labels: List<String>,
    allNotes: List<NoteEntity>,
    selectedTag: String?,
    onCreateLabel: (String) -> Unit,
    onDeleteLabel: (String) -> Unit,
    onSelectTag: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    var newLabelInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Label,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Tạo & Quản Lý Nhãn",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag("dialog_manage_labels"),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Tạo nhãn mới để phân loại và tìm kiếm ghi chú dễ dàng hơn.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Input box to create new label
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newLabelInput,
                        onValueChange = { newLabelInput = it },
                        placeholder = { Text("Tên nhãn (ví dụ: Dự án, Khẩn cấp)...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_create_label"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            if (newLabelInput.isNotBlank()) {
                                onCreateLabel(newLabelInput)
                                newLabelInput = ""
                            }
                        },
                        enabled = newLabelInput.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_submit_create_label")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tạo")
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "DANH SÁCH NHÃN (${labels.size})",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )

                if (labels.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Chưa có nhãn nào. Hãy nhập tên nhãn phía trên để tạo nhãn đầu tiên!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                } else {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("flow_row_labels_list"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        labels.forEach { labelName ->
                            val isSelected = selectedTag.equals(labelName, ignoreCase = true)
                            val noteCount = allNotes.count { note ->
                                note.tagList.any { it.equals(labelName, ignoreCase = true) }
                            }

                            InputChip(
                                selected = isSelected,
                                onClick = {
                                    onSelectTag(labelName)
                                },
                                label = {
                                    Text(
                                        text = "#$labelName ($noteCount)",
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { onDeleteLabel(labelName) },
                                        modifier = Modifier
                                            .size(20.dp)
                                            .testTag("btn_delete_label_$labelName")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Xoá nhãn $labelName",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = InputChipDefaults.inputChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                                modifier = Modifier.testTag("label_chip_$labelName")
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_close_manage_labels")
            ) {
                Text("Xong")
            }
        }
    )
}
