package com.ae.emailrotator.data.local.entity

import androidx.room.*
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus

@Entity(
    tableName = "emails",
    foreignKeys = [
        ForeignKey(
            entity = ToolEntity::class,
            parentColumns = ["id"],
            childColumns = ["tool_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("tool_id")]
)
data class EmailEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val address: String,
    @ColumnInfo(name = "tool_id") val toolId: Long?,
    val status: EmailStatus,
    @ColumnInfo(name = "available_at") val availableAt: Long?,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(toolName: String = ""): Email = Email(
        id = id,
        address = address,
        toolId = toolId ?: 0,
        toolName = toolName,
        status = status,
        availableAt = availableAt,
        createdAt = createdAt
    )
}

fun Email.toEntity(): EmailEntity = EmailEntity(
    id = id,
    address = address,
    toolId = if (toolId == 0L) null else toolId,
    status = status,
    availableAt = availableAt,
    createdAt = createdAt
)