package com.ae.emailrotator.data.local.dao

import androidx.room.*
import com.ae.emailrotator.data.local.entity.ToolEmailCrossRef
import kotlinx.coroutines.flow.Flow

data class ToolEmailJoin(
    val toolId: Long, val emailId: Long, val orderIndex: Int,
    val email: String, val status: String, val availableAt: Long?
)

@Dao
interface ToolEmailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crossRefs: List<ToolEmailCrossRef>)

    @Query("DELETE FROM tool_email_cross_ref WHERE tool_id = :toolId")
    suspend fun deleteAllForTool(toolId: Long)

    @Query("""
        SELECT te.tool_id AS toolId, te.email_id AS emailId, te.order_index AS orderIndex,
               e.email, e.status, e.available_at AS availableAt
        FROM tool_email_cross_ref te INNER JOIN emails e ON te.email_id = e.id
        WHERE te.tool_id = :toolId ORDER BY te.order_index ASC
    """)
    suspend fun getEmailsForTool(toolId: Long): List<ToolEmailJoin>

    @Query("""
        SELECT te.tool_id AS toolId, te.email_id AS emailId, te.order_index AS orderIndex,
               e.email, e.status, e.available_at AS availableAt
        FROM tool_email_cross_ref te INNER JOIN emails e ON te.email_id = e.id
        WHERE te.tool_id = :toolId ORDER BY te.order_index ASC
    """)
    fun getEmailsForToolFlow(toolId: Long): Flow<List<ToolEmailJoin>>

    @Query("""
        SELECT te.tool_id AS toolId, te.email_id AS emailId, te.order_index AS orderIndex,
               e.email, e.status, e.available_at AS availableAt
        FROM tool_email_cross_ref te INNER JOIN emails e ON te.email_id = e.id
        ORDER BY te.tool_id, te.order_index ASC
    """)
    fun getAllToolEmailJoins(): Flow<List<ToolEmailJoin>>

    @Query("SELECT DISTINCT tool_id FROM tool_email_cross_ref WHERE email_id = :emailId")
    suspend fun getToolIdsForEmail(emailId: Long): List<Long>
}