package com.ae.emailrotator.presentation.tool

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.EmailInTool
import com.ae.emailrotator.domain.model.ToolWithEmails
import com.ae.emailrotator.domain.usecase.email.LimitEmailUseCase
import com.ae.emailrotator.domain.usecase.tool.GetToolsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ToolDetailUiState(
    val toolWithEmails: ToolWithEmails? = null,
    val isLoading: Boolean = true,
    val limitDialogEmail: EmailInTool? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class ToolDetailViewModel @Inject constructor(
    private val getToolsUseCase: GetToolsUseCase,
    private val limitEmailUseCase: LimitEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToolDetailUiState())
    val uiState: StateFlow<ToolDetailUiState> = _uiState.asStateFlow()

    fun loadTool(toolId: Long) {
        viewModelScope.launch {
            getToolsUseCase.byId(toolId).collect { twe ->
                _uiState.update { it.copy(toolWithEmails = twe, isLoading = false) }
            }
        }
    }

    fun showLimitDialog(emailInTool: EmailInTool) {
        _uiState.update { it.copy(limitDialogEmail = emailInTool) }
    }

    fun dismissLimitDialog() {
        _uiState.update { it.copy(limitDialogEmail = null) }
    }

    fun limitEmail(emailId: Long, availableAt: Long) {
        viewModelScope.launch {
            val rotated = limitEmailUseCase(emailId, availableAt)
            _uiState.update {
                it.copy(
                    limitDialogEmail = null,
                    snackbarMessage = "Email limited. ${rotated.size} tool(s) rotated."
                )
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}