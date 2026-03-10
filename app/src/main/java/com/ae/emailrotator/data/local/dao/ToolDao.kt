package com.ae.emailrotator.data.local.dao

import androidx.room.*
import com.ae.emailrotator.data.local.entity.ToolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolDao {
    @Query("SELECT * FROM tools ORDER BY created_at DESC")
    fun getAllTools(): Flow<List<ToolEntity>>

    @Query("SELECT * FROM tools WHERE id = :id")
    suspend fun getById(id: Long): ToolEntity?

    @Query("SELECT * FROM tools WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): ToolEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tool: ToolEntity): Long

    @Update
    suspend fun update(tool: ToolEntity)

    @Query("DELETE FROM tools WHERE id = :id")
    suspend fun delete(id: Long)
}
