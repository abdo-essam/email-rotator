package com.ae.emailrotator.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ae.emailrotator.data.local.dao.EmailDao
import com.ae.emailrotator.data.local.dao.ToolDao
import com.ae.emailrotator.data.local.dao.ToolEmailDao
import com.ae.emailrotator.data.local.dao.UsageHistoryDao
import com.ae.emailrotator.data.local.entity.EmailEntity
import com.ae.emailrotator.data.local.entity.ToolEmailCrossRef
import com.ae.emailrotator.data.local.entity.ToolEntity
import com.ae.emailrotator.data.local.entity.UsageHistoryEntity
import androidx.room.TypeConverters
import com.ae.emailrotator.data.local.converter.Converters
import com.ae.emailrotator.data.local.dao.DeviceDao
import com.ae.emailrotator.data.local.entity.DeviceEntity


@Database(
    entities = [
        DeviceEntity::class, EmailEntity::class, ToolEntity::class,
        ToolEmailCrossRef::class, UsageHistoryEntity::class
    ],
    version = 1, exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun emailDao(): EmailDao
    abstract fun toolDao(): ToolDao
    abstract fun toolEmailDao(): ToolEmailDao
    abstract fun usageHistoryDao(): UsageHistoryDao

    companion object { const val DATABASE_NAME = "email_rotator_db" }
}