package com.ae.emailrotator.presentation.tool

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.usecase.email.GetEmailsUseCase
import com.ae.emailrotator.domain.usecase.tool.AddToolUseCase
import com.ae.emailrotator.domain.usecase.tool.GetToolsUseCase
import com.ae.emailrotator.domain.usecase.tool.UpdateToolUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditToolUiState(
    val toolName: String = "",
    val deviceId: Long = 0L,
    val allEmails: List<Email> = emptyList(),
    val selectedEmailIds: Set<Long> = emptySet(),
    val isEditing: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val nameError: String? = null,
    val savedSuccessfully: Boolean = false
)

@HiltViewModel
class AddEditToolViewModel @Inject constructor(
    private val getEmailsUseCase: GetEmailsUseCase,
    private val getToolsUseCase: GetToolsUseCase,
    private val addToolUseCase: AddToolUseCase,
    private val updateToolUseCase: UpdateToolUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditToolUiState())
    val uiState: StateFlow<AddEditToolUiState> = _uiState.asStateFlow()

    private var editingTool: Tool? = null

    fun initialize(toolId: Long?, deviceId: Long?) {
        viewModelScope.launch {
            val allEmails = getEmailsUseCase().first()
            _uiState.update { it.copy(allEmails = allEmails) }

            if (toolId != null) {
                val twe = getToolsUseCase.byId(toolId).first()
                if (twe != null) {
                    editingTool = twe.tool
                    _uiState.update {
                        it.copy(
                            toolName = twe.tool.name,
                            deviceId = twe.tool.deviceId,
                            selectedEmailIds = twe.emails.map { e -> e.email.id }.toSet(),
                            isEditing = true,
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } else if (deviceId != null) {
                _uiState.update { it.copy(deviceId = deviceId, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateToolName(name: String) {
        _uiState.update { it.copy(toolName = name, nameError = null) }
    }

    fun toggleEmail(emailId: Long) {
        _uiState.update { state ->
            val newSet = state.selectedEmailIds.toMutableSet()
            if (emailId in newSet) newSet.remove(emailId) else newSet.add(emailId)
            state.copy(selectedEmailIds = newSet)
        }
    }

    fun save() {
        val state = _uiState.value
        val name = state.toolName.trim()

        if (name.isBlank()) {
            _uiState.update { it.copy(nameError = "Tool name is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                if (state.isEditing && editingTool != null) {
                    val updated = editingTool!!.copy(name = name)
                    updateToolUseCase(updated, state.selectedEmailIds.toList())
                } else {
                    addToolUseCase(name, state.deviceId, state.selectedEmailIds.toList())
                }
                _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, nameError = "Failed to save: ${e.message}")
                }
            }
        }
    }
}
