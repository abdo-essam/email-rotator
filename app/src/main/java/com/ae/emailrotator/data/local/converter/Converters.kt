package com.ae.emailrotator.data.local.converter

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Long? = value
    @TypeConverter
    fun toTimestamp(date: Long?): Long? = date
}