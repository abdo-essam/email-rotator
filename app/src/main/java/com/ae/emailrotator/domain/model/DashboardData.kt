package com.ae.emailrotator.domain.model

data class DashboardData(
    val toolSummaries: List<ToolSummary>,
    val allEmailStatuses: List<EmailStatusRow>
)

data class ToolSummary(
    val toolId: Long,
    val toolName: String,
    val currentActiveEmail: String?,
    val totalEmails: Int,
    val availableEmails: Int
)

data class EmailStatusRow(
    val emailId: Long,
    val emailAddress: String,
    val toolNames: List<String>,
    val status: EmailStatus,
    val availableAt: Long?
)
