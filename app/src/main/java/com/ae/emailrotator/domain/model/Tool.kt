package com.ae.emailrotator.domain.model

data class Tool(
    val id: Long = 0L,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
