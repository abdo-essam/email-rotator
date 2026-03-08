package com.ae.emailrotator.domain.usecase.device

import com.ae.emailrotator.domain.repository.DeviceRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class DeleteDeviceUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val toolRepository: ToolRepository
) {
    suspend operator fun invoke(deviceId: Long) {
        deviceRepository.deleteDevice(deviceId)
    }
}