package com.ae.emailrotator.domain.model

data class DayAvailability(
    val date: Long,
    val dateFormatted: String,
    val emails: List<Email>,
    val count: Int
)
