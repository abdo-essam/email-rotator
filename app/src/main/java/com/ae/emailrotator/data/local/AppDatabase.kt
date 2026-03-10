package com.ae.emailrotator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ae.emailrotator.data.local.dao.EmailDao
import com.ae.emailrotator.data.local.dao.ToolDao
import com.ae.emailrotator.data.local.entity.EmailEntity
import com.ae.emailrotator.data.local.entity.ToolEntity

@Database(
    entities = [EmailEntity::class, ToolEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun emailDao(): EmailDao
    abstract fun toolDao(): ToolDao

    companion object {
        const val DATABASE_NAME = "email_rotator_db"
    }
}