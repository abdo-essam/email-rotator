package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import com.ae.emailrotator.domain.usecase.rotation.RotateEmailUseCase
import javax.inject.Inject

class DeleteEmailUseCase @Inject constructor(
    private val emailRepository: EmailRepository,
    private val toolRepository: ToolRepository,
    private val rotateEmailUseCase: RotateEmailUseCase
) {
    suspend operator fun invoke(emailId: Long) {
        val tools = toolRepository.getToolsWithEmailOnce(emailId)
        for (tool in tools) {
            val twe = toolRepository.getToolWithEmailsOnce(tool.id)
            val remaining = twe?.emails?.map { it.email.id }
                ?.filter { it != emailId } ?: emptyList()
            toolRepository.assignEmailsToTool(tool.id, remaining)
            if (tool.currentActiveEmailId == emailId) {
                if (remaining.isNotEmpty()) rotateEmailUseCase(tool.id)
                else toolRepository.setActiveEmail(tool.id, null)
            }
        }
        emailRepository.deleteEmail(emailId)
    }
}
