package com.ae.emailrotator.presentation.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.usecase.email.DeleteEmailUseCase
import com.ae.emailrotator.domain.usecase.email.GetEmailsUseCase
import com.ae.emailrotator.domain.usecase.email.LimitEmailUseCase
import com.ae.emailrotator.domain.usecase.tool.GetToolsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject



data class EmailManagementUiState(
    val emails: List<Email> = emptyList(),
    val allTools: List<Tool> = emptyList(),
    val searchQuery: String = "",
    val selectedToolFilter: Long? = null,
    val isLoading: Boolean = true,
    val limitDialogEmail: Email? = null,
    val deleteDialogEmail: Email? = null,
    val snackbarMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EmailManagementViewModel @Inject constructor(
    private val getEmailsUseCase: GetEmailsUseCase,
    private val deleteEmailUseCase: DeleteEmailUseCase,
    private val limitEmailUseCase: LimitEmailUseCase,
    private val getToolsUseCase: GetToolsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailManagementUiState())
    val uiState: StateFlow<EmailManagementUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val selectedToolFilter = MutableStateFlow<Long?>(null)

    init {
        viewModelScope.launch {
            getToolsUseCase().collect { toolsWithEmails ->
                _uiState.update { it.copy(allTools = toolsWithEmails.map { t -> t.tool }) }
            }
        }

        viewModelScope.launch {
            combine(searchQuery, selectedToolFilter) { query, filter ->
                Pair(query, filter)
            }.flatMapLatest { (query, toolFilter) ->
                when {
                    toolFilter != null -> getEmailsUseCase.byTool(toolFilter)
                    query.isNotBlank() -> getEmailsUseCase.search(query)
                    else -> getEmailsUseCase()
                }
            }.collect { emails ->
                val query = searchQuery.value
                val filtered = if (query.isNotBlank() && selectedToolFilter.value != null) {
                    emails.filter { it.address.contains(query, ignoreCase = true) }
                } else emails
                _uiState.update {
                    it.copy(
                        emails = filtered,
                        searchQuery = query,
                        selectedToolFilter = selectedToolFilter.value,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) { searchQuery.value = query }
    fun updateToolFilter(toolId: Long?) { selectedToolFilter.value = toolId }
    fun showLimitDialog(email: Email) { _uiState.update { it.copy(limitDialogEmail = email) } }
    fun dismissLimitDialog() { _uiState.update { it.copy(limitDialogEmail = null) } }
    fun showDeleteDialog(email: Email) { _uiState.update { it.copy(deleteDialogEmail = email) } }
    fun dismissDeleteDialog() { _uiState.update { it.copy(deleteDialogEmail = null) } }
    fun clearSnackbar() { _uiState.update { it.copy(snackbarMessage = null) } }

    fun limitEmail(emailId: Long, availableAt: Long) {
        viewModelScope.launch {
            limitEmailUseCase(emailId, availableAt)
            _uiState.update { it.copy(limitDialogEmail = null, snackbarMessage = "Email limited.") }
        }
    }

    fun deleteEmail(emailId: Long) {
        viewModelScope.launch {
            deleteEmailUseCase(emailId)
            _uiState.update { it.copy(deleteDialogEmail = null, snackbarMessage = "Email deleted.") }
        }
    }
}