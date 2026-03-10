package com.ae.emailrotator.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun isDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(enabled: Boolean)
    fun isNotificationsEnabled(): Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)
    fun getDefaultLimitDays(): Flow<Int>
    suspend fun setDefaultLimitDays(days: Int)
}