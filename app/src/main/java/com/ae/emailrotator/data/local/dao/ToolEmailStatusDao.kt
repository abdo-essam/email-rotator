package com.ae.emailrotator.data.local.dao

import androidx.room.*
import com.ae.emailrotator.data.local.entity.ToolEmailStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolEmailStatusDao {

    @Query("""
        SELECT tes.*, ge.address, ge.created_at as email_created_at, t.name as tool_name
        FROM tool_email_status tes
        INNER JOIN global_emails ge ON tes.email_id = ge.id
        INNER JOIN tools t ON tes.tool_id = t.id
        WHERE tes.tool_id = :toolId
        ORDER BY
            CASE tes.status 
                WHEN 'AVAILABLE' THEN 0 
                WHEN 'LIMITED' THEN 1 
                ELSE 2 
            END,
            tes.available_at ASC,
            ge.created_at ASC
    """)
    fun getEmailsForTool(toolId: Long): Flow<List<EmailStatusWithDetails>>

    @Query("""
        SELECT tes.*, ge.address, ge.created_at as email_created_at, t.name as tool_name
        FROM tool_email_status tes
        INNER JOIN global_emails ge ON tes.email_id = ge.id
        INNER JOIN tools t ON tes.tool_id = t.id
        WHERE tes.tool_id = :toolId AND tes.status = 'AVAILABLE'
        ORDER BY ge.created_at ASC
    """)
    fun getUsableEmailsForTool(toolId: Long): Flow<List<EmailStatusWithDetails>>

    @Query("""
        SELECT tes.*, ge.address, ge.created_at as email_created_at, t.name as tool_name
        FROM tool_email_status tes
        INNER JOIN global_emails ge ON tes.email_id = ge.id
        INNER JOIN tools t ON tes.tool_id = t.id
        WHERE tes.status = :status
        ORDER BY ge.created_at ASC
    """)
    fun getEmailsByStatus(status: String): Flow<List<EmailStatusWithDetails>>

    @Query("""
        SELECT tes.*, ge.address, ge.created_at as email_created_at, t.name as tool_name
        FROM tool_email_status tes
        INNER JOIN global_emails ge ON tes.email_id = ge.id
        INNER JOIN tools t ON tes.tool_id = t.id
        WHERE ge.address LIKE '%' || :query || '%'
        ORDER BY
            CASE tes.status 
                WHEN 'AVAILABLE' THEN 0 
                WHEN 'LIMITED' THEN 1 
                ELSE 2 
            END,
            tes.available_at ASC
    """)
    fun searchEmails(query: String): Flow<List<EmailStatusWithDetails>>

    @Query("""
        SELECT tes.*, ge.address, ge.created_at as email_created_at, t.name as tool_name
        FROM tool_email_status tes
        INNER JOIN global_emails ge ON tes.email_id = ge.id
        INNER JOIN tools t ON tes.tool_id = t.id
    """)
    fun getAllEmailStatuses(): Flow<List<EmailStatusWithDetails>>

    @Query("SELECT * FROM tool_email_status WHERE email_id = :emailId AND tool_id = :toolId")
    suspend fun getStatus(emailId: Long, toolId: Long): ToolEmailStatusEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(status: ToolEmailStatusEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(statuses: List<ToolEmailStatusEntity>)

    @Update
    suspend fun update(status: ToolEmailStatusEntity)

    @Query("""
        UPDATE tool_email_status 
        SET status = 'LIMITED', available_at = :availableAt 
        WHERE email_id = :emailId AND tool_id = :toolId
    """)
    suspend fun limitEmail(emailId: Long, toolId: Long, availableAt: Long)

    @Query("""
        UPDATE tool_email_status 
        SET status = 'AVAILABLE', available_at = NULL 
        WHERE email_id = :emailId AND tool_id = :toolId
    """)
    suspend fun verifyEmail(emailId: Long, toolId: Long)

    @Query("""
        SELECT tes.*, ge.address, ge.created_at as email_created_at, t.name as tool_name
        FROM tool_email_status tes
        INNER JOIN global_emails ge ON tes.email_id = ge.id
        INNER JOIN tools t ON tes.tool_id = t.id
        WHERE tes.status = 'LIMITED' 
          AND tes.available_at IS NOT NULL 
          AND tes.available_at <= :now
    """)
    suspend fun getExpiredLimited(now: Long): List<EmailStatusWithDetails>

    @Query("""
        UPDATE tool_email_status 
        SET status = 'AVAILABLE', available_at = NULL
        WHERE status = 'LIMITED' 
          AND available_at IS NOT NULL 
          AND available_at <= :now
    """)
    suspend fun markAvailableWhereExpired(now: Long)

    @Query("""
        SELECT tes.*, ge.address, ge.created_at as email_created_at, t.name as tool_name
        FROM tool_email_status tes
        INNER JOIN global_emails ge ON tes.email_id = ge.id
        INNER JOIN tools t ON tes.tool_id = t.id
        WHERE tes.tool_id = :toolId AND tes.status = 'AVAILABLE'
        ORDER BY ge.created_at ASC
        LIMIT 1
    """)
    suspend fun getNextUsable(toolId: Long): EmailStatusWithDetails?

    @Query("SELECT * FROM tool_email_status WHERE email_id = :emailId")
    suspend fun getAllStatusesForEmail(emailId: Long): List<ToolEmailStatusEntity>

    @Query("DELETE FROM tool_email_status WHERE email_id = :emailId AND tool_id = :toolId")
    suspend fun deleteStatus(emailId: Long, toolId: Long)
}

data class EmailStatusWithDetails(
    val id: Long,
    val email_id: Long,
    val tool_id: Long,
    val tool_name: String,
    val address: String,
    val status: String,
    val available_at: Long?,
    val email_created_at: Long
)
