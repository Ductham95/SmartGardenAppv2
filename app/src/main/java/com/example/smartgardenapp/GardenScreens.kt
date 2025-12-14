package com.example.smartgarden

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartgardenapp.MainViewModel
import com.example.smartgardenapp.ui.components.*
import com.example.smartgardenapp.ui.theme.*

// --- MÀN HÌNH LOGIN ---
@Composable
fun LoginScreen(viewModel: MainViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    // Animation for content
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundLight,
                        CardGreen.copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        // Floating decoration
        FloatingPlantDecoration(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 60.dp, end = 20.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { -50 }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo/Icon
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(16.dp, CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(GradientGreenStart, GradientGreenEnd)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Yard,
                            contentDescription = "Smart Garden",
                            tint = TextLight,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Smart Garden",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                    Text(
                        text = "Vườn thông minh của bạn",
                        fontSize = 16.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 200)) + slideInVertically(tween(800, delayMillis = 200)) { 50 }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Đăng nhập",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Chào mừng bạn trở lại!",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        StyledTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                isError = false
                            },
                            label = "Email",
                            leadingIcon = Icons.Outlined.Email,
                            isError = isError
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                isError = false
                            },
                            label = { Text("Mật khẩu") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = GreenPrimary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility
                                            else Icons.Default.VisibilityOff,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = TextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None
                                else PasswordVisualTransformation(),
                            isError = isError,
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = TextMuted,
                                focusedLabelColor = GreenPrimary,
                                cursorColor = GreenPrimary,
                                errorBorderColor = ErrorColor,
                                errorLabelColor = ErrorColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        AnimatedVisibility(visible = isError) {
                            Row(
                                modifier = Modifier.padding(top = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = ErrorColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "  Email hoặc mật khẩu không đúng!",
                                    color = ErrorColor,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        GradientButton(
                            text = "Đăng nhập",
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
                            isLoading = isLoading,
                            enabled = email.isNotBlank() && password.isNotBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 400))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = GreenLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "  Chăm sóc cây trồng thông minh",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

// --- MÀN HÌNH DASHBOARD ---
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()

    // Animation for content
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

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
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Header Section
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -30 }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WelcomeHeader()
                    StatusChip(isConnected = state.isConnected)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Stats Header
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 100)) + slideInVertically(tween(600, delayMillis = 100)) { 30 }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(GradientGreenStart, GradientGreenEnd)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Trạng thái vườn",
                                    fontSize = 14.sp,
                                    color = TextLight.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = if (state.soilMoisture > 40) "Tốt 🌿" else "Cần tưới nước 💧",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(TextLight.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Park,
                                    contentDescription = null,
                                    tint = TextLight,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sensor Cards Grid
            Text(
                text = "Thông số cảm biến",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(tween(600, delayMillis = 200)) { 30 }
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedSensorCard(
                            title = "Nhiệt độ",
                            value = String.format("%.1f", state.temperature),
                            unit = "°C",
                            icon = Icons.Default.Thermostat,
                            color = TemperatureColor,
                            progress = state.temperature,
                            maxValue = 50f,
                            modifier = Modifier.weight(1f)
                        )
                        AnimatedSensorCard(
                            title = "Độ ẩm KK",
                            value = String.format("%.0f", state.humidity),
                            unit = "%",
                            icon = Icons.Default.Cloud,
                            color = HumidityColor,
                            progress = state.humidity,
                            maxValue = 100f,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedSensorCard(
                            title = "Độ ẩm đất",
                            value = String.format("%.0f", state.soilMoisture),
                            unit = "%",
                            icon = Icons.Default.Grass,
                            color = SoilMoistureColor,
                            progress = state.soilMoisture,
                            maxValue = 100f,
                            modifier = Modifier.weight(1f)
                        )
                        AnimatedSensorCard(
                            title = "Mực nước",
                            value = String.format("%.0f", state.tankWaterLevel),
                            unit = "%",
                            icon = Icons.Default.WaterDrop,
                            color = WaterLevelColor,
                            progress = state.tankWaterLevel,
                            maxValue = 100f,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Control Section
            Text(
                text = "Điều khiển",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically(tween(600, delayMillis = 300)) { 30 }
            ) {
                PumpControlCard(
                    isPumpOn = state.isPumpOn,
                    onToggle = { viewModel.togglePump(it) }
                )
            }

            // Battery Status (if available)
            if (state.batteryLevel > 0) {
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, delayMillis = 400))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            color = BatteryColor.copy(alpha = 0.15f),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when {
                                            state.batteryLevel > 80 -> Icons.Default.BatteryFull
                                            state.batteryLevel > 50 -> Icons.Default.Battery5Bar
                                            state.batteryLevel > 20 -> Icons.Default.Battery2Bar
                                            else -> Icons.Default.BatteryAlert
                                        },
                                        contentDescription = "Battery",
                                        tint = BatteryColor,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = "Pin",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${state.batteryLevel.toInt()}%",
                                        fontSize = 14.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}