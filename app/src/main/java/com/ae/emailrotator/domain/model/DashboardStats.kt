package com.ae.emailrotator.domain.model

data class DashboardStats(
    val totalEmails: Int = 0,
    val activeEmails: Int = 0,
    val limitedEmails: Int = 0,
    val needsVerificationEmails: Int = 0,
    val toolStats: List<ToolStat> = emptyList()
)

data class ToolStat(
    val toolId: Long,
    val toolName: String,
    val totalEmails: Int,
    val activeEmails: Int
)
