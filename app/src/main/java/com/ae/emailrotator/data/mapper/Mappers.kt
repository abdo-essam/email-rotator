package com.ae.emailrotator.data.mapper

import com.ae.emailrotator.data.local.dao.EmailStatusWithDetails
import com.ae.emailrotator.data.local.entity.GlobalEmailEntity
import com.ae.emailrotator.data.local.entity.ToolEmailStatusEntity
import com.ae.emailrotator.data.local.entity.ToolEntity
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.GlobalEmail
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.model.ToolEmailStatus

fun EmailStatusWithDetails.toDomain(): Email = Email(
    id = email_id, // Map email_id to domain Email id to represent the global identity
    address = address,
    toolId = tool_id,
    toolName = tool_name,
    status = runCatching { EmailStatus.valueOf(status) }.getOrDefault(EmailStatus.AVAILABLE),
    availableAt = available_at,
    createdAt = email_created_at
)

fun GlobalEmailEntity.toDomain(): GlobalEmail = GlobalEmail(
    id = id,
    address = address,
    createdAt = createdAt
)

fun GlobalEmail.toEntity(): GlobalEmailEntity = GlobalEmailEntity(
    id = id,
    address = address,
    createdAt = createdAt
)

fun ToolEntity.toDomain(): Tool = Tool(
    id = id,
    name = name,
    createdAt = createdAt
)

fun Tool.toEntity(): ToolEntity = ToolEntity(
    id = id,
    name = name,
    createdAt = createdAt
)

fun ToolEmailStatusEntity.toDomain(): ToolEmailStatus = ToolEmailStatus(
    id = id,
    emailId = emailId,
    toolId = toolId,
    status = runCatching { EmailStatus.valueOf(status) }.getOrDefault(EmailStatus.AVAILABLE),
    availableAt = availableAt
)
