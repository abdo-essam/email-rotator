package com.ae.emailrotator.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "emails", indices = [Index(value = ["email"], unique = true)])
data class EmailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val email: String,
    val status: String = "AVAILABLE",
    @ColumnInfo(name = "available_at")
    val availableAt: Long? = null
)