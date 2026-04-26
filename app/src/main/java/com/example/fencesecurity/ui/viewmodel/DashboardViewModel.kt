package com.example.fencesecurity.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fencesecurity.data.model.AiPredictionResponse
import com.example.fencesecurity.data.model.SensorData
import com.example.fencesecurity.data.repository.FenceRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: FenceRepository) : ViewModel() {

    private val _sensorData = MutableStateFlow<SensorData?>(null)
    val sensorData: StateFlow<SensorData?> = _sensorData

    private val _aiPrediction = MutableStateFlow<AiPredictionResponse?>(null)
    val aiPrediction: StateFlow<AiPredictionResponse?> = _aiPrediction

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLiveMode = MutableStateFlow(false)
    val isLiveMode: StateFlow<Boolean> = _isLiveMode

    private var refreshJob: Job? = null

    init {
        startAutoRefresh()
    }

    fun toggleLiveMode() {
        _isLiveMode.value = !_isLiveMode.value
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            while (true) {
                refreshData()
                val interval = if (_isLiveMode.value) 3000L else 10000L
                delay(interval)
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getLatestSensorData()
                .onSuccess { data ->
                    _sensorData.value = data
                    data?.let { 
                        getAiPrediction(it)
                        checkAndTriggerAlert(it)
                    }
                }
                .onFailure { error ->
                    _errorMessage.value = "Failed: ${error.message}"
                }
            _isLoading.value = false
        }
    }

    private suspend fun getAiPrediction(data: SensorData) {
        repository.getAiPrediction(data)
            .onSuccess { _aiPrediction.value = it }
    }

    private fun checkAndTriggerAlert(data: SensorData) {
        if (data.riskScore > 80.0 || data.intrusionStatus == "CRITICAL") {
            viewModelScope.launch {
                val alertMap = mapOf(
                    "device_id" to data.deviceId,
                    "title" to "High Risk Detected",
                    "message" to "Critical intrusion detected at fence! Risk Score: ${data.riskScore}",
                    "level" to "HIGH",
                    "location" to "Fence Sector 1" // In a real app, this would come from GPS
                )
                repository.createAlert(alertMap)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
