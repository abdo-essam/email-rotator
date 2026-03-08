package com.ae.emailrotator.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.*
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.usecase.device.GetDevicesUseCase
import com.ae.emailrotator.domain.usecase.email.LimitEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val devicesWithTools: List<DeviceWithTools> = emptyList(),
    val allEmails: List<Email> = emptyList(),
    val isLoading: Boolean = true,
    val limitDialogEmailId: Long? = null,
    val limitDialogEmailAddress: String? = null,
    val snackbarMessage: String? = null,
    val totalDevices: Int = 0,
    val totalTools: Int = 0,
    val totalEmails: Int = 0,
    val totalAvailable: Int = 0,
    val totalLimited: Int = 0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase,
    private val emailRepository: EmailRepository,
    private val limitEmailUseCase: LimitEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getDevicesUseCase.withTools(),
                emailRepository.getAllEmails()
            ) { devices, emails ->
                val totalTools = devices.sumOf { it.tools.size }
                val available = emails.count { it.status == EmailStatus.AVAILABLE }
                val limited = emails.count { it.status == EmailStatus.LIMITED }

                DashboardUiState(
                    devicesWithTools = devices,
                    allEmails = emails,
                    isLoading = false,
                    totalDevices = devices.size,
                    totalTools = totalTools,
                    totalEmails = emails.size,
                    totalAvailable = available,
                    totalLimited = limited
                )
            }.collect { newState ->
                _uiState.update { old ->
                    newState.copy(
                        limitDialogEmailId = old.limitDialogEmailId,
                        limitDialogEmailAddress = old.limitDialogEmailAddress,
                        snackbarMessage = old.snackbarMessage
                    )
                }
            }
        }
    }

    fun showLimitDialog(emailId: Long, emailAddress: String) {
        _uiState.update { it.copy(limitDialogEmailId = emailId, limitDialogEmailAddress = emailAddress) }
    }

    fun dismissLimitDialog() {
        _uiState.update { it.copy(limitDialogEmailId = null, limitDialogEmailAddress = null) }
    }

    fun limitEmail(emailId: Long, availableAt: Long) {
        viewModelScope.launch {
            val rotated = limitEmailUseCase(emailId, availableAt)
            _uiState.update {
                it.copy(
                    limitDialogEmailId = null,
                    limitDialogEmailAddress = null,
                    snackbarMessage = "Email limited. ${rotated.size} tool(s) rotated."
                )
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}