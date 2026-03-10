package com.ae.emailrotator.presentation.emails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.ToolType
import com.ae.emailrotator.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EmailsState(
    val emails: List<Email> = emptyList(),
    val searchQuery: String = "",
    val toolFilter: ToolType? = null,
    val isLoading: Boolean = true,
    val showAddSheet: Boolean = false,
    val editingEmail: Email? = null,
    val limitEmail: Email? = null,
    val deleteEmail: Email? = null,
    val snackbar: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EmailsViewModel @Inject constructor(
    private val getEmails: GetEmailsUseCase,
    private val addEmail: AddEmailUseCase,
    private val updateEmail: UpdateEmailUseCase,
    private val deleteEmailUseCase: DeleteEmailUseCase,
    private val limitEmailUseCase: LimitEmailUseCase,
    private val refreshUseCase: RefreshAvailabilityUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EmailsState())
    val state: StateFlow<EmailsState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private val toolFilter = MutableStateFlow<ToolType?>(null)

    init {
        viewModelScope.launch { refreshUseCase() }

        viewModelScope.launch {
            combine(searchQuery, toolFilter) { q, t -> Pair(q, t) }
                .flatMapLatest { (query, tool) ->
                    when {
                        query.isNotBlank() -> getEmails.search(query)
                        tool != null -> getEmails.byTool(tool)
                        else -> getEmails()
                    }
                }
                .collect { emails ->
                    val q = searchQuery.value
                    val t = toolFilter.value
                    // If both search and filter, apply both
                    val filtered = if (q.isNotBlank() && t != null) {
                        emails.filter {
                            it.address.contains(q, ignoreCase = true) && it.tool == t
                        }
                    } else emails

                    _state.update { old ->
                        old.copy(
                            emails = filtered,
                            searchQuery = q,
                            toolFilter = t,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun setSearch(query: String) { searchQuery.value = query }
    fun setToolFilter(tool: ToolType?) { toolFilter.value = tool }

    fun showAdd() { _state.update { it.copy(showAddSheet = true, editingEmail = null) } }
    fun showEdit(email: Email) { _state.update { it.copy(showAddSheet = true, editingEmail = email) } }
    fun dismissSheet() { _state.update { it.copy(showAddSheet = false, editingEmail = null) } }

    fun showLimit(email: Email) { _state.update { it.copy(limitEmail = email) } }
    fun dismissLimit() { _state.update { it.copy(limitEmail = null) } }

    fun showDelete(email: Email) { _state.update { it.copy(deleteEmail = email) } }
    fun dismissDelete() { _state.update { it.copy(deleteEmail = null) } }

    fun clearSnackbar() { _state.update { it.copy(snackbar = null) } }

    fun saveEmail(address: String, tool: ToolType) {
        viewModelScope.launch {
            val editing = _state.value.editingEmail
            try {
                if (editing != null) {
                    updateEmail(editing.copy(address = address, tool = tool))
                    _state.update { it.copy(showAddSheet = false, editingEmail = null, snackbar = "Email updated.") }
                } else {
                    addEmail(Email(address = address, tool = tool))
                    _state.update { it.copy(showAddSheet = false, snackbar = "Email added.") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(snackbar = "Error: ${e.message}") }
            }
        }
    }

    fun limitEmail(emailId: Long, availableAt: Long) {
        viewModelScope.launch {
            limitEmailUseCase(emailId, availableAt)
            _state.update { it.copy(limitEmail = null, snackbar = "Email limited.") }
        }
    }

    fun deleteEmail(emailId: Long) {
        viewModelScope.launch {
            deleteEmailUseCase(emailId)
            _state.update { it.copy(deleteEmail = null, snackbar = "Email deleted.") }
        }
    }
}
