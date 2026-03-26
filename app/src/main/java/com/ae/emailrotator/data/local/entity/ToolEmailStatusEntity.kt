package com.ae.emailrotator.data.local.entity

import androidx.room.*

@Entity(
    tableName = "tool_email_status",
    indices = [
        Index(value = ["email_id", "tool_id"], unique = true),
        Index(value = ["tool_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = GlobalEmailEntity::class,
            parentColumns = ["id"],
            childColumns = ["email_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ToolEntity::class,
            parentColumns = ["id"],
            childColumns = ["tool_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ToolEmailStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "email_id")
    val emailId: Long,
    @ColumnInfo(name = "tool_id")
    val toolId: Long,
    val status: String = "AVAILABLE",
    @ColumnInfo(name = "available_at")
    val availableAt: Long? = null,
    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long? = null
)

