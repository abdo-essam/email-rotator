package com.ae.emailrotator.data.mapper

import com.ae.emailrotator.data.local.dao.ToolEmailJoin
import com.ae.emailrotator.data.local.dao.UsageHistoryJoin
import com.ae.emailrotator.data.local.entity.DeviceEntity
import com.ae.emailrotator.data.local.entity.EmailEntity
import com.ae.emailrotator.data.local.entity.ToolEntity
import com.ae.emailrotator.domain.model.*

fun DeviceEntity.toDomain(): Device = Device(id, name, DeviceType.fromString(type), createdAt)
fun Device.toEntity(): DeviceEntity = DeviceEntity(id, name, type.name, createdAt)

fun EmailEntity.toDomain(): Email = Email(
    id, email,
    try { EmailStatus.valueOf(status) } catch (_: Exception) { EmailStatus.AVAILABLE },
    availableAt
)
fun Email.toEntity(): EmailEntity = EmailEntity(id, address, status.name, availableAt)

fun ToolEntity.toDomain(): Tool = Tool(id, name, deviceId, currentActiveEmailId)
fun Tool.toEntity(): ToolEntity = ToolEntity(id, name, deviceId, currentActiveEmailId)

fun ToolEmailJoin.toEmailInTool(): EmailInTool = EmailInTool(
    Email(emailId, email,
        try { EmailStatus.valueOf(status) } catch (_: Exception) { EmailStatus.AVAILABLE },
        availableAt),
    orderIndex
)

fun UsageHistoryJoin.toDomain(): UsageHistory = UsageHistory(
    id, emailAddress, toolName, deviceName,
    try { HistoryAction.valueOf(action) } catch (_: Exception) { HistoryAction.ACTIVATED },
    timestamp
)