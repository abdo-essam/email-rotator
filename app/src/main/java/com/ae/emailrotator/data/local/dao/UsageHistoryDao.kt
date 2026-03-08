package com.ae.emailrotator.data.local.dao

import androidx.room.*
import com.ae.emailrotator.data.local.entity.UsageHistoryEntity
import kotlinx.coroutines.flow.Flow

data class UsageHistoryJoin(
    val id: Long,
    val emailAddress: String,
    val toolName: String,
    val action: String,
    val timestamp: Long
)

@Dao
interface UsageHistoryDao {
    @Query("""
        SELECT h.id, e.email AS emailAddress, t.name AS toolName, h.action, h.timestamp
        FROM usage_history h
        INNER JOIN emails e ON h.email_id = e.id
        INNER JOIN tools t ON h.tool_id = t.id
        ORDER BY h.timestamp DESC
    """)
    fun getAllHistory(): Flow<List<UsageHistoryJoin>>

    @Query("""
        SELECT h.id, e.email AS emailAddress, t.name AS toolName, h.action, h.timestamp
        FROM usage_history h
        INNER JOIN emails e ON h.email_id = e.id
        INNER JOIN tools t ON h.tool_id = t.id
        WHERE h.email_id = :emailId
        ORDER BY h.timestamp DESC
    """)
    fun getHistoryForEmail(emailId: Long): Flow<List<UsageHistoryJoin>>

    @Query("""
        SELECT h.id, e.email AS emailAddress, t.name AS toolName, h.action, h.timestamp
        FROM usage_history h
        INNER JOIN emails e ON h.email_id = e.id
        INNER JOIN tools t ON h.tool_id = t.id
        WHERE h.tool_id = :toolId
        ORDER BY h.timestamp DESC
    """)
    fun getHistoryForTool(toolId: Long): Flow<List<UsageHistoryJoin>>

    @Insert
    suspend fun insert(entity: UsageHistoryEntity)
}
