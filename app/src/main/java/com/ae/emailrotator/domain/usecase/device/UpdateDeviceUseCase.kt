package com.ae.emailrotator.domain.usecase.device

import com.ae.emailrotator.domain.model.Device
import com.ae.emailrotator.domain.repository.DeviceRepository
import javax.inject.Inject

class UpdateDeviceUseCase @Inject constructor(
    private val deviceRepository: DeviceRepository
) {
    suspend operator fun invoke(device: Device) =
        deviceRepository.updateDevice(device)
}