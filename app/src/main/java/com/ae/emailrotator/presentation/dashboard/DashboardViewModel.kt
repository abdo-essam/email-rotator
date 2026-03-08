package com.ae.emailrotator.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.*
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.usecase.email.LimitEmailUseCase
import com.ae.emailrotator.domain.usecase.tool.GetToolsWithEmailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val toolSummaries: List<ToolSummary> = emptyList(),
    val emailStatusRows: List<EmailStatusRow> = emptyList(),
    val isLoading: Boolean = true,
    val limitDialogEmail: EmailStatusRow? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getToolsWithEmailsUseCase: GetToolsWithEmailsUseCase,
    private val emailRepository: EmailRepository,
    private val limitEmailUseCase: LimitEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            combine(
                getToolsWithEmailsUseCase(),
                emailRepository.getAllEmails()
            ) { toolsWithEmails, allEmails ->
                val toolSummaries = toolsWithEmails.map { twe ->
                    val availableCount = twe.emails.count { it.email.status == EmailStatus.AVAILABLE }
                    ToolSummary(
                        toolId = twe.tool.id,
                        toolName = twe.tool.name,
                        currentActiveEmail = twe.currentActiveEmail?.address,
                        totalEmails = twe.emails.size,
                        availableEmails = availableCount
                    )
                }

                val emailRows = allEmails.map { email ->
                    val toolNames = toolsWithEmails
                        .filter { twe -> twe.emails.any { it.email.id == email.id } }
                        .map { it.tool.name }
                    EmailStatusRow(
                        emailId = email.id,
                        emailAddress = email.address,
                        toolNames = toolNames,
                        status = email.status,
                        availableAt = email.availableAt
                    )
                }

                DashboardUiState(
                    toolSummaries = toolSummaries,
                    emailStatusRows = emailRows,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state.copy(
                    limitDialogEmail = _uiState.value.limitDialogEmail,
                    snackbarMessage = _uiState.value.snackbarMessage
                )
            }
        }
    }

    fun showLimitDialog(emailRow: EmailStatusRow) {
        _uiState.update { it.copy(limitDialogEmail = emailRow) }
    }

    fun dismissLimitDialog() {
        _uiState.update { it.copy(limitDialogEmail = null) }
    }

    fun limitEmail(emailId: Long, availableAt: Long) {
        viewModelScope.launch {
            val rotatedTools = limitEmailUseCase(emailId, availableAt)
            _uiState.update {
                it.copy(
                    limitDialogEmail = null,
                    snackbarMessage = "Email limited. ${rotatedTools.size} tool(s) rotated."
                )
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
