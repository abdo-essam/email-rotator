package com.ae.emailrotator.domain.model

enum class HistoryAction(val displayName: String) {
    ACTIVATED("Activated"),
    LIMITED("Limited"),
    BECAME_AVAILABLE("Became Available"),
    ROTATED_OUT("Rotated Out")
}