package com.ae.emailrotator.data.local.dao

import androidx.room.*
import com.ae.emailrotator.data.local.entity.ToolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Query("SELECT * FROM tools ORDER BY name ASC")
    fun getAllTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE id = :id")
    fun getToolById(id: Long): Flow<ToolEntity?>

    @Query("SELECT * FROM tools WHERE id = :id")
    suspend fun getToolByIdOnce(id: Long): ToolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tool: ToolEntity): Long

    @Update
    suspend fun update(tool: ToolEntity)

    @Query("DELETE FROM tools WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("UPDATE tools SET current_active_email_id = :emailId WHERE id = :toolId")
    suspend fun setActiveEmail(toolId: Long, emailId: Long?)

    @Query("""
        SELECT t.* FROM tools t
        INNER JOIN tool_email_cross_ref te ON t.id = te.tool_id
        WHERE te.email_id = :emailId
    """)
    suspend fun getToolsContainingEmail(emailId: Long): List<ToolEntity>
}
