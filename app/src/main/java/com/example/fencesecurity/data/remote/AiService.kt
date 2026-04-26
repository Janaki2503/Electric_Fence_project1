package com.example.fencesecurity.data.remote

import com.example.fencesecurity.data.model.AiPredictionResponse
import com.example.fencesecurity.data.model.SensorData
import retrofit2.http.Body
import retrofit2.http.POST

interface AiService {
    @POST("predict")
    suspend fun getPrediction(@Body sensorData: SensorData): AiPredictionResponse
}
