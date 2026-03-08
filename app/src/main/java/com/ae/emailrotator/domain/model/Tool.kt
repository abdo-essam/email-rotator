package com.ae.emailrotator.domain.model

data class Tool(
    val id: Long = 0L,
    val name: String,
    val currentActiveEmailId: Long? = null
)
