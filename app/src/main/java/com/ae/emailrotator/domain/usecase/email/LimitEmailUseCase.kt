package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.HistoryAction
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import com.ae.emailrotator.domain.repository.UsageHistoryRepository
import com.ae.emailrotator.domain.usecase.rotation.RotateEmailUseCase
import javax.inject.Inject

class LimitEmailUseCase @Inject constructor(
    private val emailRepository: EmailRepository,
    private val toolRepository: ToolRepository,
    private val usageHistoryRepository: UsageHistoryRepository,
    private val rotateEmailUseCase: RotateEmailUseCase
) {
    suspend operator fun invoke(emailId: Long, availableAt: Long): List<Long> {
        emailRepository.updateEmailStatus(emailId, EmailStatus.LIMITED, availableAt)
        val affectedTools = toolRepository.getToolsWithEmailOnce(emailId)
        val rotatedToolIds = mutableListOf<Long>()
        for (tool in affectedTools) {
            usageHistoryRepository.record(emailId, tool.id, HistoryAction.LIMITED)
            if (tool.currentActiveEmailId == emailId) {
                rotateEmailUseCase(tool.id)
                rotatedToolIds.add(tool.id)
            }
        }
        return rotatedToolIds
    }
}