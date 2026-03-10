package com.ae.emailrotator.di

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ae.emailrotator.data.local.AppDatabase
import com.ae.emailrotator.data.local.dao.EmailDao
import com.ae.emailrotator.data.local.dao.ToolDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS tools (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    name TEXT NOT NULL,
                    created_at INTEGER NOT NULL
                )
            """)
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_tools_name ON tools(name)")
            database.execSQL("""
                INSERT OR IGNORE INTO tools (name, created_at)
                VALUES ('Claude', ${System.currentTimeMillis()}),
                       ('Gemini', ${System.currentTimeMillis()})
            """)
            database.execSQL("ALTER TABLE emails ADD COLUMN tool_id INTEGER REFERENCES tools(id) ON DELETE SET NULL")
            // Migration logic for existing tool types if needed
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration() // Simple for now
            .build()

    @Provides
    fun provideEmailDao(db: AppDatabase): EmailDao = db.emailDao()

    @Provides
    fun provideToolDao(db: AppDatabase): ToolDao = db.toolDao()
}