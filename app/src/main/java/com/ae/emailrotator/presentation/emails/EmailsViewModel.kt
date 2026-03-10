package com.ae.emailrotator.presentation.emails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.usecase.email.*
import com.ae.emailrotator.domain.usecase.tool.GetToolsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmailsState(
    val emails: List<Email> = emptyList(),
    val tools: List<Tool> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val filterToolId: Long? = null,
    val snackbar: String? = null
)

@HiltViewModel
class EmailsViewModel @Inject constructor(
    private val getEmails: GetEmailsUseCase,
    private val getTools: GetToolsUseCase,
    private val addEmail: AddEmailUseCase,
    private val updateEmail: UpdateEmailUseCase,
    private val deleteEmail: DeleteEmailUseCase,
    private val limitEmail: LimitEmailUseCase,
    private val verifyEmail: VerifyEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EmailsState())
    val state: StateFlow<EmailsState> = _state.asStateFlow()

    init {
        observeEmails()
        observeTools()
    }

    private fun observeEmails() {
        viewModelScope.launch {
            combine(
                _state.map { it.searchQuery }.distinctUntilChanged(),
                _state.map { it.filterToolId }.distinctUntilChanged()
            ) { query, toolId ->
                if (query.isBlank() && toolId == null) {
                    getEmails()
                } else if (query.isNotBlank()) {
                    getEmails.search(query)
                } else {
                    getEmails.byTool(toolId!!)
                }
            }.flatMapLatest { it }
                .collect { list ->
                    _state.update { it.copy(emails = list, isLoading = false) }
                }
        }
    }

    private fun observeTools() {
        viewModelScope.launch {
            getTools().collect { list ->
                _state.update { it.copy(tools = list) }
            }
        }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    fun onFilterToolChange(toolId: Long?) {
        _state.update { it.copy(filterToolId = toolId) }
    }

    fun saveEmail(address: String, toolId: Long, id: Long = 0L) {
        viewModelScope.launch {
            val email = Email(id = id, address = address, toolId = toolId)
            if (id == 0L) addEmail(email) else updateEmail(email)
            _state.update { it.copy(snackbar = if (id == 0L) "Email added." else "Email updated.") }
        }
    }

    fun deleteEmail(id: Long) {
        viewModelScope.launch {
            deleteEmail.invoke(id)
            _state.update { it.copy(snackbar = "Email deleted.") }
        }
    }

    fun limitEmail(id: Long, availableAt: Long) {
        viewModelScope.launch {
            limitEmail.invoke(id, availableAt)
            _state.update { it.copy(snackbar = "Email limited.") }
        }
    }

    fun verifyEmail(id: Long) {
        viewModelScope.launch {
            verifyEmail.invoke(id)
            _state.update { it.copy(snackbar = "Email verified.") }
        }
    }

    fun clearSnackbar() {
        _state.update { it.copy(snackbar = null) }
    }
}
