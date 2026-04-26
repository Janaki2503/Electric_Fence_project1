package com.example.fencesecurity.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fencesecurity.data.model.Alert
import com.example.fencesecurity.data.repository.FenceRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(private val repository: FenceRepository) : ViewModel() {

    private val _alerts = MutableStateFlow<List<Alert>>(emptyList())
    val alerts: StateFlow<List<Alert>> = _alerts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                fetchAlerts(showLoading = _alerts.value.isEmpty())
                delay(10000)
            }
        }
    }

    fun manualRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchAlerts(showLoading = false)
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchAlerts(showLoading: Boolean) {
        if (showLoading) _isLoading.value = true
        _errorMessage.value = null
        repository.getAlerts()
            .onSuccess { data ->
                _alerts.value = data
            }
            .onFailure { error ->
                _errorMessage.value = "Failed to load alerts: ${error.message}"
            }
        if (showLoading) _isLoading.value = false
    }

    fun loadAlerts() {
        viewModelScope.launch {
            fetchAlerts(showLoading = true)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
