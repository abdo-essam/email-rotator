package com.ae.emailrotator.presentation.device

import com.ae.emailrotator.domain.model.Device
import com.ae.emailrotator.domain.model.DeviceType
import com.ae.emailrotator.domain.model.DeviceWithTools
import com.ae.emailrotator.domain.usecase.device.AddDeviceUseCase
import com.ae.emailrotator.domain.usecase.device.DeleteDeviceUseCase
import com.ae.emailrotator.domain.usecase.device.GetDevicesUseCase
import com.ae.emailrotator.domain.usecase.device.UpdateDeviceUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DevicesUiState(
    val devicesWithTools: List<DeviceWithTools> = emptyList(),
    val isLoading: Boolean = true,
    val showAddSheet: Boolean = false,
    val editingDevice: Device? = null,
    val deleteDialogDevice: Device? = null,
    val snackbarMessage: String? = null
)

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase,
    private val addDeviceUseCase: AddDeviceUseCase,
    private val updateDeviceUseCase: UpdateDeviceUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevicesUiState())
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getDevicesUseCase.withTools().collect { devices ->
                _uiState.update { it.copy(devicesWithTools = devices, isLoading = false) }
            }
        }
    }

    fun showAddSheet() { _uiState.update { it.copy(showAddSheet = true, editingDevice = null) } }
    fun showEditSheet(device: Device) { _uiState.update { it.copy(showAddSheet = true, editingDevice = device) } }
    fun dismissSheet() { _uiState.update { it.copy(showAddSheet = false, editingDevice = null) } }
    fun showDeleteDialog(device: Device) { _uiState.update { it.copy(deleteDialogDevice = device) } }
    fun dismissDeleteDialog() { _uiState.update { it.copy(deleteDialogDevice = null) } }
    fun clearSnackbar() { _uiState.update { it.copy(snackbarMessage = null) } }

    fun saveDevice(name: String, type: DeviceType) {
        viewModelScope.launch {
            val editing = _uiState.value.editingDevice
            if (editing != null) {
                updateDeviceUseCase(editing.copy(name = name, type = type))
                _uiState.update { it.copy(showAddSheet = false, editingDevice = null, snackbarMessage = "Device updated.") }
            } else {
                addDeviceUseCase(Device(name = name, type = type))
                _uiState.update { it.copy(showAddSheet = false, snackbarMessage = "Device added.") }
            }
        }
    }

    fun deleteDevice(deviceId: Long) {
        viewModelScope.launch {
            deleteDeviceUseCase(deviceId)
            _uiState.update { it.copy(deleteDialogDevice = null, snackbarMessage = "Device deleted.") }
        }
    }
}