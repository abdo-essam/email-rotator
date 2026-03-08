package com.ae.emailrotator.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ae.emailrotator.domain.model.HistoryAction
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import com.ae.emailrotator.domain.repository.UsageHistoryRepository
import com.ae.emailrotator.domain.usecase.rotation.RotateEmailUseCase
import com.ae.emailrotator.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class EmailAvailabilityWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val emailRepository: EmailRepository,
    private val toolRepository: ToolRepository,
    private val usageHistoryRepository: UsageHistoryRepository,
    private val rotateEmailUseCase: RotateEmailUseCase
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val refreshedIds = emailRepository.refreshAvailability()

            for (emailId in refreshedIds) {
                val email = emailRepository.getEmailByIdOnce(emailId)
                if (email != null) {
                    NotificationHelper.showEmailAvailableNotification(
                        appContext,
                        email.address,
                        emailId.toInt()
                    )

                    val tools = toolRepository.getToolsWithEmailOnce(emailId)
                    for (tool in tools) {
                        usageHistoryRepository.record(
                            emailId, tool.id, HistoryAction.BECAME_AVAILABLE
                        )
                        if (tool.currentActiveEmailId == null) {
                            rotateEmailUseCase(tool.id)
                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
