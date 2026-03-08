package com.ae.emailrotator.domain.model

data class Device(
    val id: Long = 0L,
    val name: String,
    val type: DeviceType,
    val createdAt: Long = System.currentTimeMillis()
)