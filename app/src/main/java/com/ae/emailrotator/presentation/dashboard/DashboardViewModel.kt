package com.ae.emailrotator.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.DashboardStats
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.usecase.email.LimitEmailUseCase
import com.ae.emailrotator.domain.usecase.email.RefreshAvailabilityUseCase
import com.ae.emailrotator.domain.usecase.email.GetEmailsUseCase
import com.ae.emailrotator.domain.usecase.stats.GetDashboardStatsUseCase
import com.ae.emailrotator.domain.usecase.tool.GetToolsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ToolEmailState(
    val tool: Tool,
    val current: Email? = null,
    val queue: List<Email> = emptyList()
)

data class DashboardState(
    val toolStates: List<ToolEmailState> = emptyList(),
    val stats: DashboardStats = DashboardStats(),
    val isLoading: Boolean = true,
    val limitEmail: Email? = null,
    val snackbar: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getEmails: GetEmailsUseCase,
    private val getTools: GetToolsUseCase,
    private val limitEmailUseCase: LimitEmailUseCase,
    private val refreshUseCase: RefreshAvailabilityUseCase,
    private val getDashboardStats: GetDashboardStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        viewModelScope.launch { refreshUseCase() }
        observeToolsAndEmails()
        observeStats()
    }

    private fun observeToolsAndEmails() {
        viewModelScope.launch {
            getTools().flatMapLatest { tools ->
                if (tools.isEmpty()) return@flatMapLatest flowOf(emptyList())
                val toolFlows = tools.map { tool ->
                    getEmails.usable(tool.id).map { emails ->
                        ToolEmailState(
                            tool = tool,
                            current = emails.firstOrNull(),
                            queue = emails
                        )
                    }
                }
                combine(toolFlows) { it.toList() }
            }.collect { toolStates ->
                _state.update { it.copy(toolStates = toolStates, isLoading = false) }
            }
        }
    }

    private fun observeStats() {
        viewModelScope.launch {
            getDashboardStats().collect { stats ->
                _state.update { it.copy(stats = stats) }
            }
        }
    }

    fun showLimit(email: Email) {
        _state.update { it.copy(limitEmail = email) }
    }

    fun dismissLimit() {
        _state.update { it.copy(limitEmail = null) }
    }

    fun limitEmail(emailId: Long, toolId: Long, availableAt: Long) {
        viewModelScope.launch {
            limitEmailUseCase(emailId, toolId, availableAt)
            _state.update {
                it.copy(
                    limitEmail = null,
                    snackbar = "Email limited for this tool. Next in queue activated."
                )
            }
        }
    }

    fun clearSnackbar() {
        _state.update { it.copy(snackbar = null) }
    }
}