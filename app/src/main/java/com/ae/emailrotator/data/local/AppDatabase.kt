package com.ae.emailrotator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ae.emailrotator.data.local.dao.EmailDao
import com.ae.emailrotator.data.local.entity.EmailEntity

@Database(
    entities = [EmailEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emailDao(): EmailDao

    companion object {
        const val DATABASE_NAME = "email_rotator_db"
    }
}