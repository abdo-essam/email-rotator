package com.ae.emailrotator.data.local.dao

import androidx.room.*
import com.ae.emailrotator.data.local.entity.EmailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {

    @Query("""
        SELECT * FROM emails
        ORDER BY
            CASE status WHEN 'AVAILABLE' THEN 0 ELSE 1 END,
            available_at ASC,
            created_at ASC
    """)
    fun getAllEmails(): Flow<List<EmailEntity>>

    @Query("""
        SELECT * FROM emails WHERE tool = :tool
        ORDER BY
            CASE status WHEN 'AVAILABLE' THEN 0 ELSE 1 END,
            available_at ASC,
            created_at ASC
    """)
    fun getEmailsByTool(tool: String): Flow<List<EmailEntity>>

    @Query("""
        SELECT * FROM emails
        WHERE tool = :tool AND status = 'AVAILABLE'
        ORDER BY created_at ASC
    """)
    fun getAvailableByTool(tool: String): Flow<List<EmailEntity>>

    @Query("""
        SELECT * FROM emails
        WHERE email LIKE '%' || :query || '%'
        ORDER BY
            CASE status WHEN 'AVAILABLE' THEN 0 ELSE 1 END,
            available_at ASC
    """)
    fun searchEmails(query: String): Flow<List<EmailEntity>>

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

    @Query("""
        SELECT * FROM emails
        WHERE status = 'LIMITED' AND available_at IS NOT NULL AND available_at <= :now
    """)
    suspend fun getExpiredLimited(now: Long): List<EmailEntity>

    @Query("""
        UPDATE emails SET status = 'AVAILABLE', available_at = NULL
        WHERE status = 'LIMITED' AND available_at IS NOT NULL AND available_at <= :now
    """)
    suspend fun markAvailableWhereExpired(now: Long)

    @Query("""
        SELECT * FROM emails
        WHERE tool = :tool AND status = 'AVAILABLE'
        ORDER BY created_at ASC
        LIMIT 1
    """)
    suspend fun getNextAvailable(tool: String): EmailEntity?
}