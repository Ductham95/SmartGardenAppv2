package com.example.smartgardenapp.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartgardenapp.ui.theme.*

@Composable
fun AnimatedSensorCard(
    title: String,
    value: String,
    unit: String = "",
    icon: ImageVector,
    color: Color,
    progress: Float = 0f,
    maxValue: Float = 100f,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress / maxValue,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Card(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                ambientColor = color.copy(alpha = 0.3f),
                spotColor = color.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon with circular progress
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                // Background circle
                Canvas(modifier = Modifier.size(72.dp)) {
                    drawArc(
                        color = color.copy(alpha = 0.15f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                }
                // Progress arc
                Canvas(modifier = Modifier.size(72.dp)) {
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * animatedProgress,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                        size = Size(size.width, size.height)
                    )
                }
                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Value
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                if (unit.isNotEmpty()) {
                    Text(
                        text = unit,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Title
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    gradient: Brush = Brush.horizontalGradient(
        colors = listOf(GradientGreenStart, GradientGreenEnd)
    )
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled && !isLoading) gradient
                    else Brush.horizontalGradient(listOf(TextMuted, TextMuted))
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = TextLight,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = text,
                    color = TextLight,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = leadingIcon?.let {
                { Icon(imageVector = it, contentDescription = null, tint = GreenPrimary) }
            },
            trailingIcon = trailingIcon,
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
        if (isError && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = ErrorColor,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun StatusChip(
    isConnected: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isConnected) SuccessColor.copy(alpha = 0.15f) else ErrorColor.copy(alpha = 0.15f)
    val textColor = if (isConnected) SuccessColor else ErrorColor
    val text = if (isConnected) "Đã kết nối" else "Mất kết nối"
    val icon = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PumpControlCard(
    isPumpOn: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isPumpOn) GreenPrimary else TextMuted,
        animationSpec = tween(300),
        label = "pumpColor"
    )

    Card(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = if (isPumpOn) GreenPrimary.copy(alpha = 0.4f) else Color.Transparent,
                spotColor = if (isPumpOn) GreenPrimary.copy(alpha = 0.4f) else Color.Transparent
            ),
        shape = RoundedCornerShape(24.dp),
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
                        .size(56.dp)
                        .background(
                            color = animatedColor.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Pump",
                        tint = animatedColor,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Máy bơm nước",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = if (isPumpOn) "Đang hoạt động" else "Đã tắt",
                        fontSize = 14.sp,
                        color = animatedColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Switch(
                checked = isPumpOn,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = SurfaceLight,
                    checkedTrackColor = GreenPrimary,
                    uncheckedThumbColor = SurfaceLight,
                    uncheckedTrackColor = TextMuted
                )
            )
        }
    }
}

@Composable
fun WelcomeHeader(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Xin chào! 👋",
            fontSize = 16.sp,
            color = TextSecondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Vườn thông minh",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = GreenPrimary
        )
    }
}

@Composable
fun FloatingPlantDecoration(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        modifier = modifier.offset(y = offsetY.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Grass,
            contentDescription = null,
            tint = GreenLight.copy(alpha = 0.6f),
            modifier = Modifier.size(120.dp)
        )
    }
}

