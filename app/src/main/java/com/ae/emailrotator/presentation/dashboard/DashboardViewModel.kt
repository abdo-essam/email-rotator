package com.ae.emailrotator.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.ToolType
import com.ae.emailrotator.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val claudeCurrent: Email? = null,
    val claudeQueue: List<Email> = emptyList(),
    val geminiCurrent: Email? = null,
    val geminiQueue: List<Email> = emptyList(),
    val isLoading: Boolean = true,
    val limitEmail: Email? = null,
    val snackbar: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getEmails: GetEmailsUseCase,
    private val limitEmailUseCase: LimitEmailUseCase,
    private val refreshUseCase: RefreshAvailabilityUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        // Refresh on start
        viewModelScope.launch { refreshUseCase() }

        viewModelScope.launch {
            combine(
                getEmails.available(ToolType.CLAUDE),
                getEmails.available(ToolType.GEMINI)
            ) { claude, gemini ->
                DashboardState(
                    claudeCurrent = claude.firstOrNull(),
                    claudeQueue = claude,
                    geminiCurrent = gemini.firstOrNull(),
                    geminiQueue = gemini,
                    isLoading = false
                )
            }.collect { newState ->
                _state.update { old ->
                    newState.copy(
                        limitEmail = old.limitEmail,
                        snackbar = old.snackbar
                    )
                }
            }
        }
    }

    fun showLimit(email: Email) {
        _state.update { it.copy(limitEmail = email) }
    }

    fun dismissLimit() {
        _state.update { it.copy(limitEmail = null) }
    }

    fun limitEmail(emailId: Long, availableAt: Long) {
        viewModelScope.launch {
            limitEmailUseCase(emailId, availableAt)
            _state.update { it.copy(limitEmail = null, snackbar = "Email limited. Next in queue activated.") }
        }
    }

    fun clearSnackbar() {
        _state.update { it.copy(snackbar = null) }
    }
}