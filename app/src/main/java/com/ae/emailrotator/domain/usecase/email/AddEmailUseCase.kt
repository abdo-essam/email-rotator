package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class AddEmailUseCase @Inject constructor(
    private val emailRepository: EmailRepository,
    private val toolRepository: ToolRepository
) {
    suspend operator fun invoke(email: Email, toolIds: List<Long>): Long {
        val emailId = emailRepository.insertEmail(email)
        for (toolId in toolIds) {
            val twe = toolRepository.getToolWithEmailsOnce(toolId)
            val existing = twe?.emails?.map { it.email.id } ?: emptyList()
            toolRepository.assignEmailsToTool(toolId, existing + emailId)
            if (twe?.tool?.currentActiveEmailId == null) {
                toolRepository.setActiveEmail(toolId, emailId)
            }
        }
        return emailId
    }
}