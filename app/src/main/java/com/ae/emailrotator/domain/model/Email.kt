package com.ae.emailrotator.domain.model

data class Email(
    val id: Long = 0L,
    val address: String,
    val tool: ToolType,
    val status: EmailStatus = EmailStatus.AVAILABLE,
    val availableAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
