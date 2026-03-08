package com.ae.emailrotator.domain.model

enum class DeviceType(val displayName: String, val icon: String) {
    MAC("Mac", "🍎"),
    WINDOWS("Windows", "🪟");

    companion object {
        fun fromString(value: String): DeviceType =
            entries.find { it.name == value } ?: MAC
    }
}