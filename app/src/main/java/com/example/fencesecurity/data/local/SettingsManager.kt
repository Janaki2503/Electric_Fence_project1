package com.example.fencesecurity.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsManager(private val context: Context) {
    private val dataStore = context.dataStore

    val isDarkMode: Flow<Boolean> = dataStore.data.map { it[IS_DARK_MODE] ?: false }
    val notificationsEnabled: Flow<Boolean> = dataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: true }
    val refreshInterval: Flow<Int> = dataStore.data.map { it[REFRESH_INTERVAL] ?: 10 }
    val deviceId: Flow<String> = dataStore.data.map { it[DEVICE_ID] ?: "FENCE_001" }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { it[IS_DARK_MODE] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setRefreshInterval(seconds: Int) {
        dataStore.edit { it[REFRESH_INTERVAL] = seconds }
    }

    suspend fun setDeviceId(id: String) {
        dataStore.edit { it[DEVICE_ID] = id }
    }

    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val REFRESH_INTERVAL = intPreferencesKey("refresh_interval")
        private val DEVICE_ID = stringPreferencesKey("device_id")
    }
}
