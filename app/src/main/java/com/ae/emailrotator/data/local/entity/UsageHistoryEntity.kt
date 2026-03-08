package com.ae.emailrotator.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usage_history",
    foreignKeys = [
        ForeignKey(entity = EmailEntity::class, parentColumns = ["id"],
            childColumns = ["email_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ToolEntity::class, parentColumns = ["id"],
            childColumns = ["tool_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("email_id"), Index("tool_id")]
)
data class UsageHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "email_id") val emailId: Long,
    @ColumnInfo(name = "tool_id") val toolId: Long,
    val action: String,
    val timestamp: Long
)