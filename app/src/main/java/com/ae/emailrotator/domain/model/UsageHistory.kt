package com.ae.emailrotator.domain.model

data class UsageHistory(
    val id: Long = 0L,
    val emailAddress: String,
    val toolName: String,
    val action: HistoryAction,
    val timestamp: Long
)
