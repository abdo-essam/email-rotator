package com.ae.emailrotator.di

import android.content.Context
import androidx.work.*
import com.ae.emailrotator.worker.EmailAvailabilityWorker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    fun enqueuePeriodicCheck(workManager: WorkManager) {
        val request = PeriodicWorkRequestBuilder<EmailAvailabilityWorker>(15, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()
        workManager.enqueueUniquePeriodicWork(
            "email_availability_check", ExistingPeriodicWorkPolicy.KEEP, request
        )
    }
}