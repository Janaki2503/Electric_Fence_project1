package com.example.fencesecurity.data.repository

import android.util.Log
import com.example.fencesecurity.data.model.AiPredictionResponse
import com.example.fencesecurity.data.model.Alert
import com.example.fencesecurity.data.model.RelayControl
import com.example.fencesecurity.data.model.SensorData
import com.example.fencesecurity.data.remote.AiService
import com.example.fencesecurity.data.remote.SupabaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FenceRepository(
    private val supabaseService: SupabaseService,
    private val aiService: AiService
) {
    suspend fun getLatestSensorData(): Result<SensorData?> = withContext(Dispatchers.IO) {
        try {
            val response = supabaseService.getLatestSensorData()
            Result.success(response.firstOrNull())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistoryData(): Result<List<SensorData>> = withContext(Dispatchers.IO) {
        try {
            val response = supabaseService.getHistoryData()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAlerts(): Result<List<Alert>> = withContext(Dispatchers.IO) {
        try {
            val response = supabaseService.getAlerts()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Log.e("ALERT_ERROR", errorMsg)
                Result.failure(Exception("Could not load alerts. Please try again later."))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: Please check your connection."))
        }
    }

    suspend fun createAlert(alert: Map<String, Any>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = supabaseService.createAlert(alert)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create alert: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRelayStatus(deviceId: String): Result<RelayControl?> = withContext(Dispatchers.IO) {
        try {
            val response = supabaseService.getDeviceControl()
            Result.success(response.find { it.deviceId == deviceId })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRelay(deviceId: String, command: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            supabaseService.updateRelayControl(deviceId, mapOf("relay_command" to command))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAiPrediction(sensorData: SensorData): Result<AiPredictionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = aiService.getPrediction(sensorData)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
