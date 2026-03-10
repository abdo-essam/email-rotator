package com.ae.emailrotator.data.local.dao

import androidx.room.*
import com.ae.emailrotator.data.local.entity.EmailEntity
import com.ae.emailrotator.domain.model.EmailStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {
    @Query("""
        SELECT e.*, t.name as toolName 
        FROM emails e 
        LEFT JOIN tools t ON e.tool_id = t.id 
        ORDER BY e.created_at DESC
    """)
    fun getAllEmails(): Flow<List<EmailWithToolName>>

    @Query("""
        SELECT e.*, t.name as toolName 
        FROM emails e 
        LEFT JOIN tools t ON e.tool_id = t.id 
        WHERE e.tool_id = :toolId 
        ORDER BY e.created_at DESC
    """)
    fun getEmailsByTool(toolId: Long): Flow<List<EmailWithToolName>>

    @Query("""
        SELECT e.*, t.name as toolName 
        FROM emails e 
        LEFT JOIN tools t ON e.tool_id = t.id 
        WHERE e.tool_id = :toolId AND e.status = 'AVAILABLE' 
        ORDER BY e.created_at ASC
    """)
    fun getUsableByTool(toolId: Long): Flow<List<EmailWithToolName>>

    @Query("""
        SELECT e.*, t.name as toolName 
        FROM emails e 
        LEFT JOIN tools t ON e.tool_id = t.id 
        WHERE e.status = :status 
        ORDER BY e.created_at DESC
    """)
    fun getEmailsByStatus(status: EmailStatus): Flow<List<EmailWithToolName>>

    @Query("""
        SELECT e.*, t.name as toolName 
        FROM emails e 
        LEFT JOIN tools t ON e.tool_id = t.id 
        WHERE e.address LIKE '%' || :query || '%' 
        ORDER BY e.created_at DESC
    """)
    fun searchEmails(query: String): Flow<List<EmailWithToolName>>

    @Query("SELECT * FROM emails WHERE id = :id")
    suspend fun getById(id: Long): EmailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(email: EmailEntity): Long

    @Update
    suspend fun update(email: EmailEntity)

    @Query("DELETE FROM emails WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE emails SET status = 'LIMITED', available_at = :availableAt WHERE id = :id")
    suspend fun limitEmail(id: Long, availableAt: Long)

    @Query("UPDATE emails SET status = 'AVAILABLE', available_at = NULL WHERE id = :id")
    suspend fun verifyEmail(id: Long)

    @Query("UPDATE emails SET status = 'AVAILABLE', available_at = NULL WHERE status = 'LIMITED' AND available_at <= :now")
    suspend fun markAvailableWhereExpired(now: Long)

    @Query("SELECT * FROM emails WHERE status = 'LIMITED' AND available_at <= :now")
    suspend fun getExpiredLimited(now: Long): List<EmailEntity>

    @Query("SELECT * FROM emails WHERE tool_id = :toolId AND status = 'AVAILABLE' ORDER BY created_at ASC LIMIT 1")
    suspend fun getNextUsable(toolId: Long): EmailEntity?
}

data class EmailWithToolName(
    @Embedded val email: EmailEntity,
    val toolName: String?
)