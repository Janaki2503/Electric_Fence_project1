package com.example.fencesecurity.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fencesecurity.ui.theme.DarkGrayText
import com.example.fencesecurity.ui.theme.GridLineColor
import com.example.fencesecurity.ui.viewmodel.HistoryViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModel
import com.patrykandpatrick.vico.core.cartesian.data.LineCartesianLayerModel
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import com.patrykandpatrick.vico.core.common.shape.Shape

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val history by viewModel.historyData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                ChartCard(
                    title = "Risk Score Over Time",
                    color = Color(0xFFEF4444), // Red
                    data = history.map { it.riskScore.toFloat() },
                    timeRange = "Last 24 Hours"
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                ChartCard(
                    title = "Voltage Trend (V)",
                    color = Color(0xFF3B82F6), // Blue
                    data = history.map { it.voltage.toFloat() },
                    timeRange = "Last 24 Hours"
                )

                Spacer(modifier = Modifier.height(24.dp))

                ChartCard(
                    title = "Current Trend (A)",
                    color = Color(0xFF10B981), // Green
                    data = history.map { it.current.toFloat() },
                    timeRange = "Last 24 Hours"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ChartCard(
    title: String,
    color: Color,
    data: List<Float>,
    timeRange: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, color = DarkGrayText, fontSize = 18.sp)
                Text(timeRange, color = DarkGrayText.copy(alpha = 0.5f), fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (data.isNotEmpty()) {
                val model = remember(data) {
                    CartesianChartModel(LineCartesianLayerModel.build { series(data) })
                }
                
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(
                            lines = listOf(
                                LineCartesianLayer.LineSpec(
                                    shader = DynamicShader.color(color),
                                    thicknessDp = 3f,
                                    point = rememberShapeComponent(shape = Shape.Pill, color = color),
                                    pointSizeDp = 4f
                                )
                            )
                        ),
                        startAxis = rememberStartAxis(
                            label = rememberTextComponent(color = DarkGrayText, textSize = 10.sp),
                            guideline = rememberLineComponent(color = GridLineColor)
                        ),
                        bottomAxis = rememberBottomAxis(
                            label = rememberTextComponent(color = DarkGrayText, textSize = 10.sp),
                            guideline = null
                        )
                    ),
                    model = model,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            } else {
                Box(
                    modifier = Modifier.height(200.dp).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No data available", color = Color.Gray)
                }
            }
        }
    }
}
