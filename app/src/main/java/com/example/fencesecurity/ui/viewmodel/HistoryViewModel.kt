package com.example.fencesecurity.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fencesecurity.data.model.SensorData
import com.example.fencesecurity.data.repository.FenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: FenceRepository) : ViewModel() {

    private val _historyData = MutableStateFlow<List<SensorData>>(emptyList())
    val historyData: StateFlow<List<SensorData>> = _historyData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getHistoryData().onSuccess {
                _historyData.value = it.reversed() // Oldest first for charts
            }
            _isLoading.value = false
        }
    }
}
