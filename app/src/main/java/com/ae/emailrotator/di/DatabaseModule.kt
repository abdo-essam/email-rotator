package com.ae.emailrotator.di

import android.content.Context
import androidx.room.Room
import com.ae.emailrotator.data.local.AppDatabase
import com.ae.emailrotator.data.local.dao.GlobalEmailDao
import com.ae.emailrotator.data.local.dao.ToolDao
import com.ae.emailrotator.data.local.dao.ToolEmailStatusDao
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideGlobalEmailDao(db: AppDatabase): GlobalEmailDao = db.globalEmailDao()

    @Provides
    fun provideToolDao(db: AppDatabase): ToolDao = db.toolDao()

    @Provides
    fun provideToolEmailStatusDao(db: AppDatabase): ToolEmailStatusDao = db.toolEmailStatusDao()
}