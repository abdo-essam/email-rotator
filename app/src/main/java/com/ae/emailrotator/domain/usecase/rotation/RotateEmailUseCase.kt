package com.ae.emailrotator.domain.usecase.rotation

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.HistoryAction
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import com.ae.emailrotator.domain.repository.UsageHistoryRepository
import javax.inject.Inject

class RotateEmailUseCase @Inject constructor(
    private val toolRepository: ToolRepository,
    private val emailRepository: EmailRepository,
    private val usageHistoryRepository: UsageHistoryRepository
) {
    suspend operator fun invoke(toolId: Long): Email? {
        emailRepository.refreshAvailability()

        val twe = toolRepository.getToolWithEmailsOnce(toolId) ?: return null
        val ordered = twe.emails.sortedBy { it.orderIndex }

        if (ordered.isEmpty()) {
            toolRepository.setActiveEmail(toolId, null)
            return null
        }

        val currentActiveId = twe.tool.currentActiveEmailId
        val now = System.currentTimeMillis()
        val currentIndex = ordered.indexOfFirst { it.email.id == currentActiveId }
        val startIndex = if (currentIndex >= 0) currentIndex + 1 else 0

        for (i in ordered.indices) {
            val idx = (startIndex + i) % ordered.size
            val candidate = ordered[idx].email

            val isAvailable = candidate.status == EmailStatus.AVAILABLE ||
                    (candidate.status == EmailStatus.LIMITED &&
                            candidate.availableAt != null &&
                            candidate.availableAt <= now)

            if (isAvailable && candidate.id != currentActiveId) {
                if (candidate.status == EmailStatus.LIMITED) {
                    emailRepository.updateEmailStatus(candidate.id, EmailStatus.AVAILABLE, null)
                }
                toolRepository.setActiveEmail(toolId, candidate.id)
                usageHistoryRepository.record(candidate.id, toolId, HistoryAction.ACTIVATED)
                return candidate
            }
        }

        if (currentActiveId != null) {
            val current = ordered.find { it.email.id == currentActiveId }?.email
            if (current != null && (current.status == EmailStatus.AVAILABLE ||
                        (current.availableAt != null && current.availableAt <= now))
            ) return current
        }

        toolRepository.setActiveEmail(toolId, null)
        return null
    }
}