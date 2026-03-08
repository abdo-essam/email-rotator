package com.ae.emailrotator.presentation.tool

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.ToolWithEmails
import com.ae.emailrotator.domain.usecase.tool.DeleteToolUseCase
import com.ae.emailrotator.domain.usecase.tool.GetToolsWithEmailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ToolManagementUiState(
    val toolsWithEmails: List<ToolWithEmails> = emptyList(),
    val isLoading: Boolean = true,
    val deleteDialogTool: ToolWithEmails? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class ToolManagementViewModel @Inject constructor(
    private val getToolsWithEmailsUseCase: GetToolsWithEmailsUseCase,
    private val deleteToolUseCase: DeleteToolUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToolManagementUiState())
    val uiState: StateFlow<ToolManagementUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getToolsWithEmailsUseCase().collect { tools ->
                _uiState.update {
                    it.copy(toolsWithEmails = tools, isLoading = false)
                }
            }
        }
    }

    fun showDeleteDialog(toolWithEmails: ToolWithEmails) {
        _uiState.update { it.copy(deleteDialogTool = toolWithEmails) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(deleteDialogTool = null) }
    }

    fun deleteTool(toolId: Long) {
        viewModelScope.launch {
            deleteToolUseCase(toolId)
            _uiState.update {
                it.copy(deleteDialogTool = null, snackbarMessage = "Tool deleted.")
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}
