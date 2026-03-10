package com.ae.emailrotator.di

import com.ae.emailrotator.data.repository.*
import com.ae.emailrotator.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEmailRepo(impl: EmailRepositoryImpl): EmailRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepo(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindToolRepo(impl: ToolRepositoryImpl): ToolRepository
}