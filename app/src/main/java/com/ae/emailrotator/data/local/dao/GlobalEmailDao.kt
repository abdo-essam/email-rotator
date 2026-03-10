package com.ae.emailrotator.data.local.dao

import androidx.room.*
import com.ae.emailrotator.data.local.entity.GlobalEmailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GlobalEmailDao {

    @Query("SELECT * FROM global_emails ORDER BY created_at ASC")
    fun getAllEmails(): Flow<List<GlobalEmailEntity>>

    @Query("SELECT * FROM global_emails ORDER BY created_at ASC")
    suspend fun getAllEmailsSnapshot(): List<GlobalEmailEntity>

    @Query("SELECT * FROM global_emails WHERE id = :id")
    suspend fun getById(id: Long): GlobalEmailEntity?

    @Query("SELECT * FROM global_emails WHERE address = :address LIMIT 1")
    suspend fun getByAddress(address: String): GlobalEmailEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(email: GlobalEmailEntity): Long

    @Update
    suspend fun update(email: GlobalEmailEntity)

    @Query("DELETE FROM global_emails WHERE id = :id")
    suspend fun delete(id: Long)
}
