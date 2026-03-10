package com.ae.emailrotator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ae.emailrotator.data.local.dao.GlobalEmailDao
import com.ae.emailrotator.data.local.dao.ToolDao
import com.ae.emailrotator.data.local.dao.ToolEmailStatusDao
import com.ae.emailrotator.data.local.entity.GlobalEmailEntity
import com.ae.emailrotator.data.local.entity.ToolEmailStatusEntity
import com.ae.emailrotator.data.local.entity.ToolEntity

@Database(
    entities = [
        GlobalEmailEntity::class,
        ToolEntity::class,
        ToolEmailStatusEntity::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun globalEmailDao(): GlobalEmailDao
    abstract fun toolDao(): ToolDao
    abstract fun toolEmailStatusDao(): ToolEmailStatusDao

    companion object {
        const val DATABASE_NAME = "email_rotator_db"
    }
}