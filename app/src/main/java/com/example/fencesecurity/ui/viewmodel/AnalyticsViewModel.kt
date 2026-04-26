package com.example.fencesecurity.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fencesecurity.data.model.Alert
import com.example.fencesecurity.data.repository.FenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class AnalyticsState(
    val totalToday: Int = 0,
    val totalWeek: Int = 0,
    val highRiskCount: Int = 0,
    val mediumRiskCount: Int = 0,
    val averageRiskScore: Double = 0.0
)

class AnalyticsViewModel(private val repository: FenceRepository) : ViewModel() {

    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadAnalytics()
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAlerts().onSuccess { alerts ->
                calculateStats(alerts)
            }
            _isLoading.value = false
        }
    }

    private fun calculateStats(alerts: List<Alert>) {
        val today = Calendar.getInstance()
        val highRisk = alerts.count { it.level.equals("HIGH", true) }
        val mediumRisk = alerts.count { it.level.equals("MEDIUM", true) }
        
        // Simple mock for today/week based on count for demo
        // In real app, parse createdAt string
        val totalToday = alerts.size / 3 
        val totalWeek = alerts.size

        _state.value = AnalyticsState(
            totalToday = totalToday,
            totalWeek = totalWeek,
            highRiskCount = highRisk,
            mediumRiskCount = mediumRisk,
            averageRiskScore = if (alerts.isNotEmpty()) 45.5 else 0.0 // Mock avg
        )
    }
}
