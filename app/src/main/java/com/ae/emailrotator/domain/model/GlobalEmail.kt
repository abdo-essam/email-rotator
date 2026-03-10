package com.ae.emailrotator.domain.model

/**
 * Represents a single email address that exists globally,
 * with per-tool status tracked separately via [ToolEmailStatus].
 */
data class GlobalEmail(
    val id: Long = 0L,
    val address: String,
    val createdAt: Long = System.currentTimeMillis()
)
