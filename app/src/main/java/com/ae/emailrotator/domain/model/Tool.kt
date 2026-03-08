package com.ae.emailrotator.domain.model

data class Tool(
    val id: Long = 0L,
    val name: String,
    val deviceId: Long,
    val currentActiveEmailId: Long? = null
)
