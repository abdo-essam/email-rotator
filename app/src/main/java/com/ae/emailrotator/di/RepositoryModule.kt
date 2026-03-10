package com.ae.emailrotator.di

import com.ae.emailrotator.data.repository.EmailRepositoryImpl
import com.ae.emailrotator.data.repository.SettingsRepositoryImpl
import com.ae.emailrotator.data.repository.ToolRepositoryImpl
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.SettingsRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindEmailRepo(impl: EmailRepositoryImpl): EmailRepository

    @Binds @Singleton
    abstract fun bindSettingsRepo(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds @Singleton
    abstract fun bindToolRepo(impl: ToolRepositoryImpl): ToolRepository
}