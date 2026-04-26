package com.example.fencesecurity.data.model

import com.google.gson.annotations.SerializedName

data class RelayControl(
    val id: Int? = null,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("relay_command") val relayCommand: Boolean,
    @SerializedName("updated_at") val updatedAt: String? = null
)
