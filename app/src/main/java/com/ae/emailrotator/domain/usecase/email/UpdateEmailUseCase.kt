package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class UpdateEmailUseCase @Inject constructor(
    private val emailRepository: EmailRepository,
    private val toolRepository: ToolRepository
) {
    suspend operator fun invoke(email: Email, toolIds: List<Long>) {
        emailRepository.updateEmail(email)

        val currentTools = toolRepository.getToolsWithEmailOnce(email.id)
        val currentToolIds = currentTools.map { it.id }

        for (tool in currentTools) {
            if (tool.id !in toolIds) {
                val twe = toolRepository.getToolWithEmailsOnce(tool.id)
                val remaining = twe?.emails?.map { it.email.id }
                    ?.filter { it != email.id } ?: emptyList()
                toolRepository.assignEmailsToTool(tool.id, remaining)
                if (tool.currentActiveEmailId == email.id) {
                    toolRepository.setActiveEmail(tool.id, remaining.firstOrNull())
                }
            }
        }

        for (toolId in toolIds) {
            if (toolId !in currentToolIds) {
                val twe = toolRepository.getToolWithEmailsOnce(toolId)
                val existing = twe?.emails?.map { it.email.id } ?: emptyList()
                if (email.id !in existing) {
                    toolRepository.assignEmailsToTool(toolId, existing + email.id)
                }
                if (twe?.tool?.currentActiveEmailId == null) {
                    toolRepository.setActiveEmail(toolId, email.id)
                }
            }
        }
    }
}