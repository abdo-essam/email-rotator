package com.ae.emailrotator.domain.repository

import com.ae.emailrotator.domain.model.Device
import com.ae.emailrotator.domain.model.DeviceWithTools
import kotlinx.coroutines.flow.Flow

interface DeviceRepository {
    fun getAllDevices(): Flow<List<Device>>
    fun getDeviceById(id: Long): Flow<Device?>
    fun getAllDevicesWithTools(): Flow<List<DeviceWithTools>>
    fun getDeviceWithTools(deviceId: Long): Flow<DeviceWithTools?>
    suspend fun insertDevice(device: Device): Long
    suspend fun updateDevice(device: Device)
    suspend fun deleteDevice(id: Long)
    suspend fun getDeviceByIdOnce(id: Long): Device?
}