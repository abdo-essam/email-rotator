package com.ae.emailrotator.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val defaultLimitDays: Int = 7
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                settingsRepository.isDarkMode(),
                settingsRepository.isNotificationsEnabled(),
                settingsRepository.getDefaultLimitDays()
            ) { dark, notifications, limitDays ->
                SettingsState(
                    isDarkMode = dark,
                    notificationsEnabled = notifications,
                    defaultLimitDays = limitDays
                )
            }.collect { settings ->
                _state.value = settings
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            settingsRepository.setDarkMode(!_state.value.isDarkMode)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setDefaultLimitDays(days: Int) {
        viewModelScope.launch {
            settingsRepository.setDefaultLimitDays(days)
        }
    }
}
