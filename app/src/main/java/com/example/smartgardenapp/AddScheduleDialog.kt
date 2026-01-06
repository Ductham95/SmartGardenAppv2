package com.example.smartgarden

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.smartgardenapp.WateringSchedule
import com.example.smartgardenapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    schedule: WateringSchedule?,
    onDismiss: () -> Unit,
    onSave: (WateringSchedule) -> Unit
) {
    val isEditing = schedule != null
    
    var name by remember { mutableStateOf(schedule?.name ?: "Lịch tưới") }
    var hour by remember { mutableStateOf(schedule?.hour ?: 6) }
    var minute by remember { mutableStateOf(schedule?.minute ?: 0) }
    var selectedDays by remember { mutableStateOf(schedule?.daysOfWeek ?: listOf(1, 2, 3, 4, 5, 6, 7)) }
    var duration by remember { mutableStateOf((schedule?.duration ?: 10000) / 1000) } // Convert to seconds
    
    var showTimePicker by remember { mutableStateOf(false) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // Title
                Text(
                    text = if (isEditing) "Sửa lịch tưới" else "Thêm lịch tưới",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên lịch") },
                    placeholder = { Text("Ví dụ: Tưới sáng") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = TextMuted,
                        focusedLabelColor = GreenPrimary,
                        cursorColor = GreenPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Time picker
                Text(
                    text = "Thời gian",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardGreen.copy(alpha = 0.3f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format("%02d:%02d", hour, minute),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenPrimary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Days of week
                Text(
                    text = "Ngày trong tuần",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                val dayNames = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dayNames.withIndex().toList()) { (index, day) ->
                        val dayValue = index + 1
                        val isSelected = selectedDays.contains(dayValue)
                        
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) {
                                        Brush.linearGradient(
                                            colors = listOf(GradientGreenStart, GradientGreenEnd)
                                        )
                                    } else {
                                        Brush.linearGradient(
                                            colors = listOf(TextMuted.copy(alpha = 0.2f), TextMuted.copy(alpha = 0.2f))
                                        )
                                    }
                                )
                                .clickable {
                                    selectedDays = if (isSelected) {
                                        selectedDays - dayValue
                                    } else {
                                        selectedDays + dayValue
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) TextLight else TextMuted
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Duration slider
                Text(
                    text = "Thời lượng tưới: ${duration}s",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Slider(
                    value = duration.toFloat(),
                    onValueChange = { duration = it.toInt() },
                    valueRange = 5f..300f,
                    steps = 58, // (300-5)/5 steps
                    colors = SliderDefaults.colors(
                        thumbColor = GreenPrimary,
                        activeTrackColor = GreenPrimary,
                        inactiveTrackColor = TextMuted.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Text(
                    text = "Từ 5 giây đến 5 phút",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextSecondary
                        )
                    ) {
                        Text("Hủy")
                    }
                    
                    Button(
                        onClick = {
                            val newSchedule = WateringSchedule(
                                id = schedule?.id ?: java.util.UUID.randomUUID().toString(),
                                name = name.ifBlank { "Lịch tưới" },
                                hour = hour,
                                minute = minute,
                                daysOfWeek = selectedDays.sorted(),
                                duration = duration * 1000,
                                isEnabled = schedule?.isEnabled ?: true
                            )
                            onSave(newSchedule)
                        },
                        enabled = selectedDays.isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = TextLight,
                            disabledContainerColor = TextMuted,
                            disabledContentColor = TextLight
                        )
                    ) {
                        Text(if (isEditing) "Cập nhật" else "Thêm")
                    }
                }
            }
        }
    }
    
    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = hour,
            initialMinute = minute,
            is24Hour = true
        )
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Chọn giờ") },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = CardGreen.copy(alpha = 0.3f),
                        selectorColor = GreenPrimary,
                        clockDialSelectedContentColor = TextLight,
                        clockDialUnselectedContentColor = TextPrimary,
                        timeSelectorSelectedContainerColor = GreenPrimary,
                        timeSelectorUnselectedContainerColor = CardGreen.copy(alpha = 0.3f),
                        timeSelectorSelectedContentColor = TextLight,
                        timeSelectorUnselectedContentColor = TextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        hour = timePickerState.hour
                        minute = timePickerState.minute
                        showTimePicker = false
                    }
                ) {
                    Text("OK", color = GreenPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}
