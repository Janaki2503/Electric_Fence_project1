package com.example.fencesecurity.data.model

import com.google.gson.annotations.SerializedName

data class AiPredictionResponse(
    @SerializedName("ai_intrusion_status") val aiIntrusionStatus: String,
    @SerializedName("risk_prediction") val riskPrediction: Int
)
