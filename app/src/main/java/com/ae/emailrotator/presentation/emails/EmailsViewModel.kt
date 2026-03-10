package com.ae.emailrotator.presentation.emails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.repository.SettingsRepository
import com.ae.emailrotator.domain.usecase.email.*
import com.ae.emailrotator.domain.usecase.tool.GetToolsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmailsState(
    val emails: List<Email> = emptyList(),
    val tools: List<Tool> = emptyList(),
    val searchQuery: String = "",
    val toolFilter: Long? = null,
    val isLoading: Boolean = true,
    val showAddSheet: Boolean = false,
    val editingEmail: Email? = null,
    val limitEmail: Email? = null,
    val deleteEmail: Email? = null,
    val snackbar: String? = null,
    val defaultLimitDays: Int = 7
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EmailsViewModel @Inject constructor(
    private val getEmails: GetEmailsUseCase,
    private val addEmail: AddEmailUseCase,
    private val updateEmail: UpdateEmailUseCase,
    private val deleteEmailUseCase: DeleteEmailUseCase,
    private val limitEmailUseCase: LimitEmailUseCase,
    private val verifyEmailUseCase: VerifyEmailUseCase,
    private val refreshUseCase: RefreshAvailabilityUseCase,
    private val getTools: GetToolsUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmailsState())
    val state: StateFlow<EmailsState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val toolFilter = MutableStateFlow<Long?>(null)

    init {
        viewModelScope.launch { refreshUseCase() }
        observeTools()
        observeEmails()
        observeSettings()
    }

    private fun observeTools() {
        viewModelScope.launch {
            getTools().collect { tools ->
                _state.update { it.copy(tools = tools) }
            }
        }
    }

    private fun observeEmails() {
        viewModelScope.launch {
            combine(searchQuery, toolFilter) { q, t -> q to t }
                .flatMapLatest { (query, toolId) ->
                    when {
                        query.isNotBlank() && toolId != null ->
                            getEmails.search(query).map { list ->
                                list.filter { it.toolId == toolId }
                            }
                        query.isNotBlank() -> getEmails.search(query)
                        toolId != null -> getEmails.byTool(toolId)
                        else -> getEmails()
                    }
                }
                .collect { emails ->
                    _state.update { it.copy(emails = emails, isLoading = false) }
                }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.getDefaultLimitDays().collect { days ->
                _state.update { it.copy(defaultLimitDays = days) }
            }
        }
    }

    fun setSearch(query: String) {
        searchQuery.value = query
        _state.update { it.copy(searchQuery = query) }
    }

    fun setToolFilter(toolId: Long?) {
        toolFilter.value = toolId
        _state.update { it.copy(toolFilter = toolId) }
    }

    fun showAdd() = _state.update { it.copy(showAddSheet = true, editingEmail = null) }
    fun showEdit(email: Email) = _state.update { it.copy(showAddSheet = true, editingEmail = email) }
    fun dismissSheet() = _state.update { it.copy(showAddSheet = false, editingEmail = null) }
    fun showLimit(email: Email) = _state.update { it.copy(limitEmail = email) }
    fun dismissLimit() = _state.update { it.copy(limitEmail = null) }
    fun showDelete(email: Email) = _state.update { it.copy(deleteEmail = email) }
    fun dismissDelete() = _state.update { it.copy(deleteEmail = null) }
    fun clearSnackbar() = _state.update { it.copy(snackbar = null) }

    fun saveEmail(address: String, needsVerification: Boolean) {
        viewModelScope.launch {
            val editing = _state.value.editingEmail
            runCatching {
                if (editing != null) {
                    updateEmail(editing.id, address)
                    _state.update {
                        it.copy(showAddSheet = false, editingEmail = null, snackbar = "Email updated.")
                    }
                } else {
                    addEmail(address, needsVerification)
                    _state.update { it.copy(showAddSheet = false, snackbar = "Email added to all tools.") }
                }
            }.onFailure { e ->
                _state.update { it.copy(snackbar = "Error: ${e.message}") }
            }
        }
    }

    fun limitEmail(emailId: Long, toolId: Long, availableAt: Long) {
        viewModelScope.launch {
            limitEmailUseCase(emailId, toolId, availableAt)
            _state.update { it.copy(limitEmail = null, snackbar = "Email limited for this tool.") }
        }
    }

    fun verifyEmail(emailId: Long, toolId: Long) {
        viewModelScope.launch {
            verifyEmailUseCase(emailId, toolId)
            _state.update { it.copy(snackbar = "Email verified successfully.") }
        }
    }

    fun deleteEmail(emailId: Long) {
        viewModelScope.launch {
            deleteEmailUseCase(emailId)
            _state.update { it.copy(deleteEmail = null, snackbar = "Email deleted from all tools.") }
        }
    }
}
