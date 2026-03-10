package com.ae.emailrotator.data.mapper

import com.ae.emailrotator.data.local.entity.EmailEntity
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.ToolType

fun EmailEntity.toDomain(): Email = Email(
    id = id,
    address = email,
    tool = ToolType.fromString(tool),
    status = try { EmailStatus.valueOf(status) } catch (_: Exception) { EmailStatus.AVAILABLE },
    availableAt = availableAt,
    createdAt = createdAt
)

fun Email.toEntity(): EmailEntity = EmailEntity(
    id = id,
    email = address,
    tool = tool.name,
    status = status.name,
    availableAt = availableAt,
    createdAt = createdAt
)