package com.ae.emailrotator.presentation.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.usecase.email.AddEmailUseCase
import com.ae.emailrotator.domain.usecase.email.GetEmailsUseCase
import com.ae.emailrotator.domain.usecase.email.UpdateEmailUseCase
import com.ae.emailrotator.domain.usecase.tool.GetToolsWithEmailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditEmailUiState(
    val emailAddress: String = "",
    val allTools: List<Tool> = emptyList(),
    val selectedToolIds: Set<Long> = emptySet(),
    val isEditing: Boolean = false,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val emailError: String? = null,
    val savedSuccessfully: Boolean = false
)

@HiltViewModel
class AddEditEmailViewModel @Inject constructor(
    private val getEmailsUseCase: GetEmailsUseCase,
    private val addEmailUseCase: AddEmailUseCase,
    private val updateEmailUseCase: UpdateEmailUseCase,
    private val getToolsWithEmailsUseCase: GetToolsWithEmailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditEmailUiState())
    val uiState: StateFlow<AddEditEmailUiState> = _uiState.asStateFlow()

    private var editingEmail: Email? = null

    fun initialize(emailId: Long?) {
        viewModelScope.launch {
            getToolsWithEmailsUseCase().first().let { toolsWithEmails ->
                val tools = toolsWithEmails.map { it.tool }
                _uiState.update { it.copy(allTools = tools) }
            }

            if (emailId != null) {
                getEmailsUseCase.byId(emailId).first()?.let { email ->
                    editingEmail = email
                    _uiState.update {
                        it.copy(
                            emailAddress = email.address,
                            selectedToolIds = email.assignedToolIds.toSet(),
                            isEditing = true,
                            isLoading = false
                        )
                    }
                } ?: run {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updateEmailAddress(address: String) {
        _uiState.update {
            it.copy(
                emailAddress = address,
                emailError = null
            )
        }
    }

    fun toggleTool(toolId: Long) {
        _uiState.update { state ->
            val newSet = state.selectedToolIds.toMutableSet()
            if (toolId in newSet) newSet.remove(toolId) else newSet.add(toolId)
            state.copy(selectedToolIds = newSet)
        }
    }

    fun save() {
        val state = _uiState.value
        val address = state.emailAddress.trim()

        if (address.isBlank()) {
            _uiState.update { it.copy(emailError = "Email address is required") }
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(address).matches()) {
            _uiState.update { it.copy(emailError = "Invalid email format") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                if (_uiState.value.isEditing && editingEmail != null) {
                    val updated = editingEmail!!.copy(address = address)
                    updateEmailUseCase(updated, state.selectedToolIds.toList())
                } else {
                    val email = Email(address = address)
                    addEmailUseCase(email, state.selectedToolIds.toList())
                }

                _uiState.update { it.copy(isSaving = false, savedSuccessfully = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        emailError = "Failed to save: ${e.message}"
                    )
                }
            }
        }
    }
}
