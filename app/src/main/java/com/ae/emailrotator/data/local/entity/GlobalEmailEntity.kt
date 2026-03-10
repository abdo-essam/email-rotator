package com.ae.emailrotator.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "global_emails",
    indices = [Index(value = ["address"], unique = true)]
)
data class GlobalEmailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val address: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
