package com.ae.emailrotator.data.mapper

import com.ae.emailrotator.data.local.dao.ToolEmailJoin
import com.ae.emailrotator.data.local.dao.UsageHistoryJoin
import com.ae.emailrotator.data.local.entity.EmailEntity
import com.ae.emailrotator.data.local.entity.ToolEntity
import com.ae.emailrotator.domain.model.*

fun EmailEntity.toDomain(): Email = Email(
    id = id,
    address = email,
    status = try { EmailStatus.valueOf(status) } catch (_: Exception) { EmailStatus.AVAILABLE },
    availableAt = availableAt
)

fun Email.toEntity(): EmailEntity = EmailEntity(
    id = id,
    email = address,
    status = status.name,
    availableAt = availableAt
)

fun ToolEntity.toDomain(): Tool = Tool(
    id = id,
    name = name,
    currentActiveEmailId = currentActiveEmailId
)

fun Tool.toEntity(): ToolEntity = ToolEntity(
    id = id,
    name = name,
    currentActiveEmailId = currentActiveEmailId
)

fun ToolEmailJoin.toEmailInTool(): EmailInTool = EmailInTool(
    email = Email(
        id = emailId,
        address = email,
        status = try { EmailStatus.valueOf(status) } catch (_: Exception) { EmailStatus.AVAILABLE },
        availableAt = availableAt
    ),
    orderIndex = orderIndex
)

fun UsageHistoryJoin.toDomain(): UsageHistory = UsageHistory(
    id = id,
    emailAddress = emailAddress,
    toolName = toolName,
    action = try { HistoryAction.valueOf(action) } catch (_: Exception) { HistoryAction.ACTIVATED },
    timestamp = timestamp
)
