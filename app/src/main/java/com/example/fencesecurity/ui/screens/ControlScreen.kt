package com.example.fencesecurity.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fencesecurity.ui.theme.DarkGrayText
import com.example.fencesecurity.ui.viewmodel.ControlViewModel

@Composable
fun ControlScreen(viewModel: ControlViewModel) {
    val relayStatus by viewModel.relayStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val isChecked = relayStatus?.relayCommand ?: false

    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                color = if (isChecked) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.size(120.dp),
                border = BorderStroke(1.dp, if (isChecked) Color(0xFFFECACA) else Color(0xFFDCFCE7))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isChecked) Icons.Default.Security else Icons.Default.PowerSettingsNew,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = if (isChecked) Color(0xFFEF4444) else Color(0xFF10B981)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Fence Relay Control",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = DarkGrayText
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Activating the relay will trigger the high-voltage fence system and siren.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(64.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isChecked) "System Active" else "System Disarmed",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isChecked) Color(0xFFEF4444) else Color(0xFF10B981)
                        )
                        Text(
                            text = if (isChecked) "Relay is providing power" else "Relay is currently OFF",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = isChecked,
                        onCheckedChange = { viewModel.toggleRelay(isChecked) },
                        modifier = Modifier.scale(1.5f),
                        enabled = !isLoading,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFEF4444)
                        )
                    )
                }
            }
            
            if (isLoading) {
                Spacer(modifier = Modifier.height(32.dp))
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        if (errorMessage != null) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(errorMessage!!)
            }
        }
    }
}
