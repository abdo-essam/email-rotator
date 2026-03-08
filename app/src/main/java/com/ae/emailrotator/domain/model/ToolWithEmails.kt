package com.ae.emailrotator.domain.model

data class ToolWithEmails(
    val tool: Tool,
    val emails: List<EmailInTool>,
    val currentActiveEmail: Email? = null
)

data class EmailInTool(
    val email: Email,
    val orderIndex: Int
)
