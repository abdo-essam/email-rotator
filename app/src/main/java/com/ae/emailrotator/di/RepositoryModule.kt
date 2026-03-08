package com.ae.emailrotator.di

import com.ae.emailrotator.data.repository.DeviceRepositoryImpl
import com.ae.emailrotator.data.repository.EmailRepositoryImpl
import com.ae.emailrotator.data.repository.SettingsRepositoryImpl
import com.ae.emailrotator.data.repository.ToolRepositoryImpl
import com.ae.emailrotator.data.repository.UsageHistoryRepositoryImpl
import com.ae.emailrotator.domain.repository.DeviceRepository
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.SettingsRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import com.ae.emailrotator.domain.repository.UsageHistoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton abstract fun bindDeviceRepo(impl: DeviceRepositoryImpl): DeviceRepository
    @Binds
    @Singleton abstract fun bindEmailRepo(impl: EmailRepositoryImpl): EmailRepository
    @Binds @Singleton abstract fun bindToolRepo(impl: ToolRepositoryImpl): ToolRepository
    @Binds @Singleton abstract fun bindHistoryRepo(impl: UsageHistoryRepositoryImpl): UsageHistoryRepository
    @Binds @Singleton abstract fun bindSettingsRepo(impl: SettingsRepositoryImpl): SettingsRepository
}