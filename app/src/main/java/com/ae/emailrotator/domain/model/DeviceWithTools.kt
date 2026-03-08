package com.ae.emailrotator.domain.model

data class DeviceWithTools(
    val device: Device,
    val tools: List<ToolWithEmails>
) {
    val totalEmails: Int get() = tools.sumOf { it.emails.size }
    val totalAvailable: Int
        get() = tools.sumOf { twe ->
            twe.emails.count { it.email.status == EmailStatus.AVAILABLE }
        }
    val totalLimited: Int
        get() = tools.sumOf { twe ->
            twe.emails.count { it.email.status == EmailStatus.LIMITED }
        }
}