package com.ae.emailrotator.domain.model

enum class ToolType(val displayName: String) {
    CLAUDE("Claude"),
    GEMINI("Gemini");

    companion object {
        fun fromString(value: String): ToolType =
            entries.find { it.name == value } ?: CLAUDE
    }
}
