package com.ae.emailrotator.data.local.dao

import androidx.room.*
import com.ae.emailrotator.data.local.entity.EmailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {

    @Query("SELECT * FROM emails ORDER BY email ASC")
    fun getAllEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE id = :id")
    fun getEmailById(id: Long): Flow<EmailEntity?>

    @Query("SELECT * FROM emails WHERE id = :id")
    suspend fun getEmailByIdOnce(id: Long): EmailEntity?

    @Query("SELECT * FROM emails WHERE email LIKE '%' || :query || '%' ORDER BY email ASC")
    fun searchEmails(query: String): Flow<List<EmailEntity>>

    @Query("""
        SELECT e.* FROM emails e
        INNER JOIN tool_email_cross_ref te ON e.id = te.email_id
        WHERE te.tool_id = :toolId ORDER BY te.order_index ASC
    """)
    fun getEmailsByToolId(toolId: Long): Flow<List<EmailEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(email: EmailEntity): Long

    @Update
    suspend fun update(email: EmailEntity)

    @Query("DELETE FROM emails WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE emails SET status = :status, available_at = :availableAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, availableAt: Long?)

    @Query("UPDATE emails SET status = 'AVAILABLE', available_at = NULL WHERE status = 'LIMITED' AND available_at IS NOT NULL AND available_at <= :now")
    suspend fun markAvailableWhereTimeReached(now: Long)

    @Query("SELECT id FROM emails WHERE status = 'LIMITED' AND available_at IS NOT NULL AND available_at <= :now")
    suspend fun getEmailIdsToRefresh(now: Long): List<Long>

    @Query("""
        SELECT e.* FROM emails e INNER JOIN tool_email_cross_ref te ON e.id = te.email_id
        WHERE te.tool_id = :toolId AND e.status = 'AVAILABLE' ORDER BY te.order_index ASC
    """)
    suspend fun getAvailableEmailsForTool(toolId: Long): List<EmailEntity>
}