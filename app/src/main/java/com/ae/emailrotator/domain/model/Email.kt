package com.ae.emailrotator.domain.model

data class Email(
    val id: Long = 0L,         // statusId (primary key in tool_email_status)
    val globalEmailId: Long = 0L, // email_id (reference to global_emails)
    val address: String,
    val toolId: Long,
    val toolName: String = "",
    val status: EmailStatus = EmailStatus.AVAILABLE,
    val availableAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isUsable: Boolean get() = status == EmailStatus.AVAILABLE
    val isLimited: Boolean get() = status == EmailStatus.LIMITED
    val needsVerification: Boolean get() = status == EmailStatus.NEEDS_VERIFICATION
}
