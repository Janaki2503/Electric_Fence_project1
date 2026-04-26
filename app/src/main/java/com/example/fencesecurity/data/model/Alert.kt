package com.example.fencesecurity.data.model

import com.google.gson.annotations.SerializedName

data class Alert(
    val id: Int = 0,
    @SerializedName("device_id")
    val deviceId: String? = null,
    val title: String? = "Unknown Alert",
    val message: String? = "No details available",
    val level: String? = "INFO",
    val location: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null
)
