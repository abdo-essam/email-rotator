package com.ae.emailrotator.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.ae.emailrotator.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DEFAULT_LIMIT_DAYS = intPreferencesKey("default_limit_days")
    }

    companion object {
        const val DEFAULT_LIMIT_DAYS = 7
    }

    override fun isDarkMode(): Flow<Boolean> =
        context.dataStore.data.map { it[Keys.DARK_MODE] ?: false }

    override suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    override fun isNotificationsEnabled(): Flow<Boolean> =
        context.dataStore.data.map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    override fun getDefaultLimitDays(): Flow<Int> =
        context.dataStore.data.map { it[Keys.DEFAULT_LIMIT_DAYS] ?: DEFAULT_LIMIT_DAYS }

    override suspend fun setDefaultLimitDays(days: Int) {
        context.dataStore.edit { it[Keys.DEFAULT_LIMIT_DAYS] = days }
    }
}