package com.example.fencesecurity.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fencesecurity.data.model.SensorData
import com.example.fencesecurity.ui.theme.DarkGrayText
import com.example.fencesecurity.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val sensorData by viewModel.sensorData.collectAsState()
    val aiPrediction by viewModel.aiPrediction.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (sensorData == null && isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                sensorData?.let { data ->
                    StatusCard(data, aiPrediction?.riskPrediction?.toDouble() ?: data.riskScore)
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        "System Metrics",
                        style = MaterialTheme.typography.titleMedium,
                        color = DarkGrayText,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            MetricCard(
                                label = "Fence Voltage",
                                value = "${data.voltage}V",
                                icon = Icons.Default.Bolt,
                                color = Color(0xFF3B82F6)
                            )
                        }
                        item {
                            MetricCard(
                                label = "Fence Current",
                                value = "${data.current}A",
                                icon = Icons.Default.Speed,
                                color = Color(0xFF10B981)
                            )
                        }
                        item {
                            MetricCard(
                                label = "PIR Motion",
                                value = if (data.pirDetected) "ON" else "OFF",
                                icon = if (data.pirDetected) Icons.Default.DirectionsRun else Icons.Default.PersonOff,
                                color = if (data.pirDetected) Color(0xFFEF4444) else Color(0xFF10B981)
                            )
                        }
                        item {
                            MetricCard(
                                label = "Vibration Level",
                                value = "${data.vibrationLevel}",
                                icon = Icons.Default.Vibration,
                                color = Color(0xFF8B5CF6)
                            )
                        }
                        item {
                            MetricCard(
                                label = "Battery Level",
                                value = "${data.batteryLevel}%",
                                icon = Icons.Default.BatteryStd,
                                color = Color(0xFFF59E0B)
                            )
                        }
                        item {
                            MetricCard(
                                label = "Intrusion",
                                value = data.intrusionStatus,
                                icon = Icons.Default.Security,
                                color = if (data.intrusionStatus.contains("None", true)) Color(0xFF10B981) else Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }
        }

        if (errorMessage != null) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                action = { TextButton(onClick = { viewModel.clearError() }) { Text("Dismiss") } }
            ) { Text(errorMessage!!) }
        }
    }
}

@Composable
fun StatusCard(data: SensorData, riskScore: Double) {
    val elevation by animateDpAsState(targetValue = 4.dp, label = "elevation")
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "System Status",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkGrayText,
                    fontWeight = FontWeight.Bold
                )
                AnimatedStatusChip(riskScore)
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                "Risk Level: ${riskScore.toInt()}%",
                fontWeight = FontWeight.Medium,
                color = DarkGrayText.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = { (riskScore / 100f).toFloat() },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = when {
                    riskScore > 70 -> Color(0xFFEF4444)
                    riskScore > 30 -> Color(0xFFF59E0B)
                    else -> Color(0xFF10B981)
                },
                trackColor = Color(0xFFF3F4F6),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (data.pirDetected) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Icon(
                    imageVector = if (data.pirDetected) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (data.pirDetected) Color(0xFFEF4444) else Color(0xFF10B981),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (data.pirDetected) "Intrusion Detected!" else "Perimeter Secure",
                    color = if (data.pirDetected) Color(0xFFB91C1C) else Color(0xFF15803D),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, icon: ImageVector, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, color = DarkGrayText.copy(alpha = 0.6f), fontSize = 14.sp)
            AnimatedContent(
                targetState = value,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "valueAnimation"
            ) { targetValue ->
                Text(
                    targetValue,
                    color = DarkGrayText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AnimatedStatusChip(score: Double) {
    val (text, color, icon) = when {
        score > 70 -> Triple("DANGER", Color(0xFFEF4444), Icons.Default.Warning)
        score > 30 -> Triple("WARNING", Color(0xFFF59E0B), Icons.Default.Info)
        else -> Triple("SECURE", Color(0xFF10B981), Icons.Default.CheckCircle)
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
