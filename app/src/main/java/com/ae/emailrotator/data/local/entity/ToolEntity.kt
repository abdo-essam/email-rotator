package com.ae.emailrotator.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ae.emailrotator.domain.model.Tool

@Entity(
    tableName = "tools",
    indices = [Index(value = ["name"], unique = true)]
)
data class ToolEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Tool = Tool(
        id = id,
        name = name,
        createdAt = createdAt
    )
}

fun Tool.toEntity(): ToolEntity = ToolEntity(
    id = id,
    name = name,
    createdAt = createdAt
)
