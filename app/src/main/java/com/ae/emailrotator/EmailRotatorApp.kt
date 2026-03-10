package com.ae.emailrotator

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.ae.emailrotator.util.NotificationHelper
import com.ae.emailrotator.worker.EmailAvailabilityWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class EmailRotatorApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannel(this)

        val request = PeriodicWorkRequestBuilder<EmailAvailabilityWorker>(15, TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "email_check", ExistingPeriodicWorkPolicy.KEEP, request
        )
    }
}