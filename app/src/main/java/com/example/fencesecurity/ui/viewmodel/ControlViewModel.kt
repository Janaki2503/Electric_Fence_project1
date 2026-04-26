package com.example.fencesecurity.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fencesecurity.data.model.RelayControl
import com.example.fencesecurity.data.repository.FenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ControlViewModel(private val repository: FenceRepository) : ViewModel() {

    private val _relayStatus = MutableStateFlow<RelayControl?>(null)
    val relayStatus: StateFlow<RelayControl?> = _relayStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val deviceId = "ESP32_FENCE_01" // Example device ID

    init {
        loadRelayStatus()
    }

    fun loadRelayStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getRelayStatus(deviceId)
                .onSuccess { status ->
                    _relayStatus.value = status
                }
                .onFailure { error ->
                    _errorMessage.value = "Failed to load relay status: ${error.message}"
                }
            _isLoading.value = false
        }
    }

    fun toggleRelay(currentValue: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateRelay(deviceId, !currentValue)
                .onSuccess {
                    loadRelayStatus()
                }
                .onFailure { error ->
                    _errorMessage.value = "Failed to update relay: ${error.message}"
                }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
