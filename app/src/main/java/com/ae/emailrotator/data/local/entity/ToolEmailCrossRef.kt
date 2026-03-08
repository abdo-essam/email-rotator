package com.ae.emailrotator.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "tool_email_cross_ref",
    primaryKeys = ["tool_id", "email_id"],
    foreignKeys = [
        ForeignKey(
            entity = ToolEntity::class,
            parentColumns = ["id"],
            childColumns = ["tool_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EmailEntity::class,
            parentColumns = ["id"],
            childColumns = ["email_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("tool_id"),
        Index("email_id")
    ]
)
data class ToolEmailCrossRef(
    @ColumnInfo(name = "tool_id")
    val toolId: Long,
    @ColumnInfo(name = "email_id")
    val emailId: Long,
    @ColumnInfo(name = "order_index")
    val orderIndex: Int
)
