package com.ae.emailrotator.domain.model

data class ToolWithEmails(
    val tool: Tool,
    val emails: List<EmailInTool>,
    val currentActiveEmail: Email? = null
) {
    val availableCount: Int
        get() = emails.count { it.email.status == EmailStatus.AVAILABLE }
    val limitedCount: Int
        get() = emails.count { it.email.status == EmailStatus.LIMITED }
}

data class EmailInTool(
    val email: Email,
    val orderIndex: Int
)
