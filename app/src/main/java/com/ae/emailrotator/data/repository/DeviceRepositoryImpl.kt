package com.ae.emailrotator.data.repository

import com.ae.emailrotator.data.local.dao.DeviceDao
import com.ae.emailrotator.data.local.dao.ToolDao
import com.ae.emailrotator.data.local.dao.ToolEmailDao
import com.ae.emailrotator.data.mapper.toDomain
import com.ae.emailrotator.data.mapper.toEmailInTool
import com.ae.emailrotator.data.mapper.toEntity
import com.ae.emailrotator.domain.model.Device
import com.ae.emailrotator.domain.model.DeviceWithTools
import com.ae.emailrotator.domain.model.ToolWithEmails
import com.ae.emailrotator.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceDao: DeviceDao,
    private val toolDao: ToolDao,
    private val toolEmailDao: ToolEmailDao
) : DeviceRepository {

    override fun getAllDevices(): Flow<List<Device>> =
        deviceDao.getAllDevices().map { list -> list.map { it.toDomain() } }

    override fun getDeviceById(id: Long): Flow<Device?> =
        deviceDao.getDeviceById(id).map { it?.toDomain() }

    override fun getAllDevicesWithTools(): Flow<List<DeviceWithTools>> =
        combine(
            deviceDao.getAllDevices(),
            toolDao.getAllTools(),
            toolEmailDao.getAllToolEmailJoins()
        ) { devices, tools, joins ->
            devices.map { deviceEntity ->
                val device = deviceEntity.toDomain()
                val deviceTools = tools
                    .filter { it.deviceId == device.id }
                    .map { toolEntity ->
                        val tool = toolEntity.toDomain()
                        val emailsInTool = joins
                            .filter { it.toolId == tool.id }
                            .map { it.toEmailInTool() }
                        val activeEmail = tool.currentActiveEmailId?.let { activeId ->
                            emailsInTool.find { it.email.id == activeId }?.email
                        }
                        ToolWithEmails(tool, emailsInTool, activeEmail)
                    }
                DeviceWithTools(device, deviceTools)
            }
        }

    override fun getDeviceWithTools(deviceId: Long): Flow<DeviceWithTools?> =
        combine(
            deviceDao.getDeviceById(deviceId),
            toolDao.getToolsByDeviceId(deviceId),
            toolEmailDao.getAllToolEmailJoins()
        ) { deviceEntity, tools, joins ->
            deviceEntity?.let {
                val device = it.toDomain()
                val deviceTools = tools.map { toolEntity ->
                    val tool = toolEntity.toDomain()
                    val emailsInTool = joins
                        .filter { j -> j.toolId == tool.id }
                        .map { j -> j.toEmailInTool() }
                    val activeEmail = tool.currentActiveEmailId?.let { activeId ->
                        emailsInTool.find { e -> e.email.id == activeId }?.email
                    }
                    ToolWithEmails(tool, emailsInTool, activeEmail)
                }
                DeviceWithTools(device, deviceTools)
            }
        }

    override suspend fun insertDevice(device: Device): Long =
        deviceDao.insert(device.toEntity())

    override suspend fun updateDevice(device: Device) =
        deviceDao.update(device.toEntity())

    override suspend fun deleteDevice(id: Long) =
        deviceDao.delete(id)

    override suspend fun getDeviceByIdOnce(id: Long): Device? =
        deviceDao.getDeviceByIdOnce(id)?.toDomain()
}