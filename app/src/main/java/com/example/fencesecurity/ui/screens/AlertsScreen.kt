package com.example.fencesecurity.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fencesecurity.data.model.Alert
import com.example.fencesecurity.ui.theme.DarkGrayText
import com.example.fencesecurity.ui.viewmodel.AlertsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(viewModel: AlertsViewModel) {
    val alerts by viewModel.alerts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.manualRefresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            if (isLoading && alerts.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null && alerts.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: "Failed to load alerts",
                        color = DarkGrayText,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadAlerts() }) {
                        Text("Retry")
                    }
                }
            } else if (alerts.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No alerts available",
                        color = DarkGrayText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(text = "Fence system running normally", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(alerts, key = { it.id }) { alert ->
                        AlertItem(alert)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertItem(alert: Alert) {
    val level = alert.level?.uppercase() ?: "INFO"
    val config = when (level) {
        "HIGH" -> AlertConfig(Color(0xFFFEF2F2), Color(0xFFEF4444), Color(0xFFEF4444), Icons.Default.Warning)
        "WARNING" -> AlertConfig(Color(0xFFFFF7ED), Color(0xFFFED7AA), Color(0xFFF59E0B), Icons.Default.PriorityHigh)
        else -> AlertConfig(Color(0xFFF0FDF4), Color(0xFFDCFCE7), Color(0xFF10B981), Icons.Default.CheckCircle)
    }

    val createdAt = alert.createdAt ?: ""
    val isNew = remember(createdAt) { isRecent(createdAt, 30) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = config.cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(if (level == "HIGH") 2.dp else 1.dp, config.borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = config.iconColor.copy(0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = config.icon,
                        contentDescription = null,
                        tint = config.iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alert.title ?: "Unknown Alert",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (level == "HIGH") Color(0xFFB91C1C) else DarkGrayText,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isNew) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Badge(containerColor = Color(0xFF3B82F6), contentColor = Color.White) {
                            Text(
                                text = "NEW",
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
                Text(
                    text = alert.message ?: "No details available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGrayText.copy(0.7f)
                )
                
                alert.location?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ID: ${alert.deviceId ?: "N/A"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (createdAt.isNotEmpty()) formatTimestamp(createdAt) else "Unknown time",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

private data class AlertConfig(
    val cardColor: Color,
    val borderColor: Color,
    val iconColor: Color,
    val icon: ImageVector
)

private fun isRecent(timestamp: String, secondsThreshold: Int): Boolean {
    if (timestamp.isEmpty()) return false
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(timestamp.take(19))
        val now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time
        val diff = now.time - (date?.time ?: 0L)
        diff < secondsThreshold * 1000
    } catch (e: Exception) {
        false
    }
}

private fun formatTimestamp(timestamp: String): String {
    if (timestamp.isEmpty()) return "Unknown"
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse(timestamp.take(19)) ?: return timestamp
        
        val diff = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time.time - date.time
        when {
            diff < 0 -> "Just now"
            diff < 60000 -> "Just now"
            diff < 3600000 -> "${diff / 60000} min ago"
            diff < 86400000 -> "${diff / 3600000} hours ago"
            else -> SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(date)
        }
    } catch (e: Exception) {
        timestamp
    }
}
