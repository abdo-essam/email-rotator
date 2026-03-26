package com.ae.emailrotator.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.DashboardStats
import com.ae.emailrotator.domain.model.DayAvailability
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.usecase.email.GetEmailsUseCase
import com.ae.emailrotator.domain.usecase.email.LimitEmailUseCase
import com.ae.emailrotator.domain.usecase.email.RefreshAvailabilityUseCase
import com.ae.emailrotator.domain.usecase.email.UpdateLimitTimeUseCase
import com.ae.emailrotator.domain.usecase.stats.GetDashboardStatsUseCase
import com.ae.emailrotator.domain.usecase.stats.GetDayAvailabilityUseCase
import com.ae.emailrotator.domain.usecase.tool.GetToolsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ToolEmailState(
    val tool: Tool,
    val availableEmails: List<Email> = emptyList(),
    val limitedEmails: List<Email> = emptyList()
) {
    val nextComingEmail: Email?
        get() = limitedEmails.firstOrNull()
}

data class DashboardState(
    val toolStates: List<ToolEmailState> = emptyList(),
    val stats: DashboardStats = DashboardStats(),
    val isLoading: Boolean = true,
    val limitEmail: Email? = null,
    val updateLimitEmail: Email? = null,
    val selectedDateAvailability: DayAvailability? = null,
    val showDatePicker: Boolean = false,
    val snackbar: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getEmails: GetEmailsUseCase,
    private val getTools: GetToolsUseCase,
    private val limitEmailUseCase: LimitEmailUseCase,
    private val updateLimitTimeUseCase: UpdateLimitTimeUseCase,
    private val refreshUseCase: RefreshAvailabilityUseCase,
    private val getDashboardStats: GetDashboardStatsUseCase,
    private val getDayAvailability: GetDayAvailabilityUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val selectedDate = MutableStateFlow<Long?>(null)

    init {
        viewModelScope.launch { refreshUseCase() }
        observeToolsAndEmails()
        observeStats()
        observeSelectedDateAvailability()
    }

    private fun observeToolsAndEmails() {
        viewModelScope.launch {
            getTools().flatMapLatest { tools ->
                if (tools.isEmpty()) return@flatMapLatest flowOf(emptyList())

                val toolFlows = tools.map { tool ->
                    combine(
                        getEmails.availableForTool(tool.id),
                        getEmails.limitedForTool(tool.id)
                    ) { available, limited ->
                        ToolEmailState(
                            tool = tool,
                            availableEmails = available,
                            limitedEmails = limited
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

    private fun observeSelectedDateAvailability() {
        viewModelScope.launch {
            selectedDate.filterNotNull().flatMapLatest { date ->
                getDayAvailability(date)
            }.collect { availability ->
                _state.update { it.copy(selectedDateAvailability = availability) }
            }
        }
    }

    fun showLimit(email: Email) {
        _state.update { it.copy(limitEmail = email) }
    }

    fun dismissLimit() {
        _state.update { it.copy(limitEmail = null) }
    }

    fun showUpdateLimit(email: Email) {
        _state.update { it.copy(updateLimitEmail = email) }
    }

    fun dismissUpdateLimit() {
        _state.update { it.copy(updateLimitEmail = null) }
    }

    fun limitEmail(statusId: Long, availableAt: Long) {
        viewModelScope.launch {
            limitEmailUseCase(statusId, availableAt)
            _state.update {
                it.copy(
                    limitEmail = null,
                    snackbar = "Email limited successfully."
                )
            }
        }
    }

    fun updateLimitTime(statusId: Long, newAvailableAt: Long) {
        viewModelScope.launch {
            updateLimitTimeUseCase(statusId, newAvailableAt)
            _state.update {
                it.copy(
                    updateLimitEmail = null,
                    snackbar = "Limit time updated."
                )
            }
        }
    }

    fun selectDate(date: Long) {
        selectedDate.value = date
        _state.update { it.copy(showDatePicker = false) }
    }

    fun showDatePicker() {
        _state.update { it.copy(showDatePicker = true) }
    }

    fun dismissDatePicker() {
        _state.update { it.copy(showDatePicker = false) }
    }

    fun clearDateSelection() {
        selectedDate.value = null
        _state.update { it.copy(selectedDateAvailability = null) }
    }

    fun clearSnackbar() {
        _state.update { it.copy(snackbar = null) }
    }
}