package com.ae.emailrotator.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ae.emailrotator.domain.usecase.RefreshAvailabilityUseCase
import com.ae.emailrotator.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmailAvailabilityWorker @AssistedInject constructor(
    @Assisted private val ctx: Context,
    @Assisted params: WorkerParameters,
    private val refreshUseCase: RefreshAvailabilityUseCase
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            val becameAvailable = refreshUseCase()
            becameAvailable.forEach { email ->
                NotificationHelper.notify(
                    ctx,
                    email.address,
                    email.tool.displayName,
                    email.id.toInt()
                )
            }
            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }
}
