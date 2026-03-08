package com.ae.emailrotator.di

import android.content.Context
import androidx.room.Room
import com.ae.emailrotator.data.local.AppDatabase
import com.ae.emailrotator.data.local.dao.EmailDao
import com.ae.emailrotator.data.local.dao.ToolDao
import com.ae.emailrotator.data.local.dao.ToolEmailDao
import com.ae.emailrotator.data.local.dao.UsageHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()

    @Provides
    fun provideEmailDao(db: AppDatabase): EmailDao = db.emailDao()

    @Provides
    fun provideToolDao(db: AppDatabase): ToolDao = db.toolDao()

    @Provides
    fun provideToolEmailDao(db: AppDatabase): ToolEmailDao = db.toolEmailDao()

    @Provides
    fun provideUsageHistoryDao(db: AppDatabase): UsageHistoryDao = db.usageHistoryDao()
}
