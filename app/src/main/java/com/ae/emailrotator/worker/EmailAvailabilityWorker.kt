package com.ae.emailrotator.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ae.emailrotator.domain.repository.SettingsRepository
import com.ae.emailrotator.domain.usecase.email.RefreshAvailabilityUseCase
import com.ae.emailrotator.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class EmailAvailabilityWorker @AssistedInject constructor(
    @Assisted private val ctx: Context,
    @Assisted params: WorkerParameters,
    private val refreshUseCase: RefreshAvailabilityUseCase,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return runCatching {
            val notificationsEnabled =
                settingsRepository.isNotificationsEnabled().firstOrNull() ?: true
            val becameAvailable = refreshUseCase()
            if (notificationsEnabled) {
                becameAvailable.forEach { email ->
                    NotificationHelper.notify(
                        ctx,
                        email.address,
                        email.toolName,
                        email.id.toInt()
                    )
                }
            }
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }
}
