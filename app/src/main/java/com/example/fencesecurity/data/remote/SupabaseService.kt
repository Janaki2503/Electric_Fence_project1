package com.example.fencesecurity.data.remote

import com.example.fencesecurity.data.model.Alert
import com.example.fencesecurity.data.model.RelayControl
import com.example.fencesecurity.data.model.SensorData
import retrofit2.Response
import retrofit2.http.*

interface SupabaseService {
    @GET("sensor_data")
    suspend fun getLatestSensorData(
        @Query("device_id") deviceId: String = "eq.FENCE_001",
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 1
    ): List<SensorData>

    @GET("sensor_data")
    suspend fun getHistoryData(
        @Query("device_id") deviceId: String = "eq.FENCE_001",
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 50
    ): List<SensorData>

    @Headers("Content-Type: application/json")
    @GET("alerts")
    suspend fun getAlerts(
        @Query("select") select: String = "*",
        @Query("order") order: String = "created_at.desc",
        @Query("limit") limit: Int = 10
    ): Response<List<Alert>>

    @GET("device_control")
    suspend fun getDeviceControl(
        @Query("select") select: String = "*"
    ): List<RelayControl>

    @PATCH("device_control")
    suspend fun updateRelayControl(
        @Query("device_id") deviceId: String,
        @Body updates: Map<String, Boolean>
    )

    @POST("alerts")
    suspend fun createAlert(@Body alert: Map<String, Any>): Response<Unit>
}
