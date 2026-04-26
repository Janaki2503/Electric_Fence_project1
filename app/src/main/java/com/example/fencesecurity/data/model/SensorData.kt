package com.example.fencesecurity.data.model

import com.google.gson.annotations.SerializedName

data class SensorData(
    @SerializedName("device_id") val deviceId: String,
    val voltage: Double,
    val current: Double,
    @SerializedName("pir_status") val pirStatus: Int,
    @SerializedName("vibration_level") val vibrationLevel: Int,
    @SerializedName("intrusion_status") val intrusionStatus: String,
    @SerializedName("risk_score") val riskScore: Double,
    @SerializedName("battery_level") val batteryLevel: Double,
    @SerializedName("created_at") val createdAt: String
) {
    val pirDetected: Boolean
        get() = pirStatus == 1
}
