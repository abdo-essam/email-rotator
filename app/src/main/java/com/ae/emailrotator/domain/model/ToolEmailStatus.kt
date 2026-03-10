package com.ae.emailrotator.domain.model

/**
 * Tracks the status of a [GlobalEmail] for a specific [Tool].
 * Each email has an independent status per tool.
 */
data class ToolEmailStatus(
    val id: Long = 0L,
    val emailId: Long,
    val toolId: Long,
    val status: EmailStatus = EmailStatus.AVAILABLE,
    val availableAt: Long? = null
)
