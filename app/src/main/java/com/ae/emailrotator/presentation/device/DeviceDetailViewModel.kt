package com.ae.emailrotator.presentation.device

import com.ae.emailrotator.domain.model.ToolWithEmails
import com.ae.emailrotator.domain.usecase.device.GetDevicesUseCase
import com.ae.emailrotator.domain.usecase.tool.DeleteToolUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ae.emailrotator.domain.model.DeviceWithTools
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeviceDetailUiState(
    val deviceWithTools: DeviceWithTools? = null,
    val isLoading: Boolean = true,
    val deleteDialogTool: ToolWithEmails? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase,
    private val deleteToolUseCase: DeleteToolUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    fun loadDevice(deviceId: Long) {
        viewModelScope.launch {
            getDevicesUseCase.withToolsById(deviceId).collect { dwt ->
                _uiState.update { it.copy(deviceWithTools = dwt, isLoading = false) }
            }
        }
    }

    fun showDeleteToolDialog(twe: ToolWithEmails) {
        _uiState.update { it.copy(deleteDialogTool = twe) }
    }

    fun dismissDeleteToolDialog() {
        _uiState.update { it.copy(deleteDialogTool = null) }
    }

    fun deleteTool(toolId: Long) {
        viewModelScope.launch {
            deleteToolUseCase(toolId)
            _uiState.update { it.copy(deleteDialogTool = null, snackbarMessage = "Tool deleted.") }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}