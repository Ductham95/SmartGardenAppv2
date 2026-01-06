package com.example.smartgarden

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartgardenapp.MainViewModel
import com.example.smartgardenapp.WateringSchedule
import com.example.smartgardenapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: MainViewModel, navController: NavController) {
    val state by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<WateringSchedule?>(null) }
    
    // Load schedules khi vào màn hình
    LaunchedEffect(Unit) {
        viewModel.fetchWateringSchedules()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Lịch tưới tự động",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary,
                    titleContentColor = TextLight,
                    navigationIconContentColor = TextLight
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    editingSchedule = null
                    showAddDialog = true 
                },
                containerColor = GreenPrimary,
                contentColor = TextLight
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm lịch")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BackgroundLight,
                            CardGreen.copy(alpha = 0.2f)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            if (state.wateringSchedules.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                color = GreenPrimary.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(56.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Chưa có lịch tưới nào",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thêm lịch tưới tự động để chăm sóc vườn của bạn",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.wateringSchedules) { schedule ->
                        ScheduleCard(
                            schedule = schedule,
                            onToggle = { enabled ->
                                viewModel.toggleScheduleEnabled(schedule.id, enabled)
                            },
                            onEdit = {
                                editingSchedule = schedule
                                showAddDialog = true
                            },
                            onDelete = {
                                viewModel.deleteWateringSchedule(schedule.id)
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Add/Edit Dialog
    if (showAddDialog) {
        AddScheduleDialog(
            schedule = editingSchedule,
            onDismiss = { 
                showAddDialog = false
                editingSchedule = null
            },
            onSave = { schedule ->
                if (editingSchedule == null) {
                    viewModel.addWateringSchedule(schedule)
                } else {
                    viewModel.updateWateringSchedule(schedule)
                }
                showAddDialog = false
                editingSchedule = null
            }
        )
    }
}

@Composable
fun ScheduleCard(
    schedule: WateringSchedule,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.isEnabled) SurfaceLight else SurfaceLight.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = if (schedule.isEnabled) {
                                    Brush.linearGradient(
                                        colors = listOf(GradientGreenStart, GradientGreenEnd)
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(TextMuted, TextMuted)
                                    )
                                },
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = TextLight,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = schedule.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (schedule.isEnabled) TextPrimary else TextMuted
                        )
                        Text(
                            text = schedule.getTimeString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (schedule.isEnabled) GreenPrimary else TextMuted
                        )
                    }
                }
                
                Switch(
                    checked = schedule.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextLight,
                        checkedTrackColor = GreenPrimary,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = TextMuted.copy(alpha = 0.3f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = TextMuted.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(16.dp))
            
            // Schedule info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    InfoRow(
                        icon = Icons.Default.CalendarToday,
                        label = "Ngày",
                        value = schedule.getDaysString(),
                        enabled = schedule.isEnabled
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(
                        icon = Icons.Default.Timer,
                        label = "Thời lượng",
                        value = "${schedule.duration / 1000}s",
                        enabled = schedule.isEnabled
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GreenPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sửa")
                }
                
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ErrorColor
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Xóa")
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa lịch tưới \"${schedule.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = ErrorColor
                    )
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    enabled: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) GreenPrimary else TextMuted,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label: ",
            fontSize = 14.sp,
            color = if (enabled) TextSecondary else TextMuted
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) TextPrimary else TextMuted
        )
    }
}
