package com.example.smartgarden

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartgardenapp.MainViewModel

// --- MÀN HÌNH LOGIN ---
@Composable
fun LoginScreen(viewModel: MainViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Smart Garden", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth()
        )

        if (isError) {
            Text("Đăng nhập thất bại!", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                isLoading = true
                viewModel.login(email, password) { success ->
                    isLoading = false
                    if (success) {
                        navController.navigate("dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        isError = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White)
            else Text("Đăng nhập")
        }
    }
}

// --- MÀN HÌNH DASHBOARD ---
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8)) // Màu nền nhẹ
            .padding(16.dp)
    ) {
        // Header
        Text(
            "Vườn thông minh",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2E7D32)
        )
        Text(
            if (state.isConnected) "Đã kết nối 🟢" else "Mất kết nối 🔴",
            color = if (state.isConnected) Color.Gray else Color.Red
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Grid hiển thị thông số (2 hàng)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SensorCard(
                title = "Nhiệt độ",
                value = "${state.temperature}°C",
                icon = Icons.Default.Thermostat,
                color = Color(0xFFFF7043),
                modifier = Modifier.weight(1f)
            )
            SensorCard(
                title = "Độ ẩm KK",
                value = "${state.humidity}%",
                icon = Icons.Default.Opacity,
                color = Color(0xFF42A5F5),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SensorCard(
                title = "Độ ẩm đất",
                value = "${state.soilMoisture}%",
                icon = Icons.Default.WaterDrop,
                color = Color(0xFF8D6E63),
                modifier = Modifier.weight(1f)
            )
            SensorCard(
                title = "Mực nước",
                value = "${state.tankWaterLevel}%",
                icon = Icons.Default.WaterDrop,
                color = Color(0xFF26C6DA),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Điều khiển Bơm
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Máy bơm nước", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Text(
                        if (state.isPumpOn) "Đang chạy" else "Đang tắt",
                        color = if (state.isPumpOn) Color(0xFF4CAF50) else Color.Gray
                    )
                }
                Switch(
                    checked = state.isPumpOn,
                    onCheckedChange = { viewModel.togglePump(it) }
                )
            }
        }
    }
}

@Composable
fun SensorCard(title: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(title, fontSize = 14.sp, color = Color.Gray)
        }
    }
}