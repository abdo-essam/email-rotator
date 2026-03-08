package com.ae.emailrotator.domain.usecase.device

import com.ae.emailrotator.domain.model.Device
import com.ae.emailrotator.domain.model.DeviceWithTools
import com.ae.emailrotator.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDevicesUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    operator fun invoke(): Flow<List<Device>> = deviceRepository.getAllDevices()

    fun withTools(): Flow<List<DeviceWithTools>> = deviceRepository.getAllDevicesWithTools()

    fun withToolsById(deviceId: Long): Flow<DeviceWithTools?> =
        deviceRepository.getDeviceWithTools(deviceId)
}