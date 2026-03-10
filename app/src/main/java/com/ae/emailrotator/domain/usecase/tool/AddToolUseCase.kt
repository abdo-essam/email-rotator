package com.ae.emailrotator.domain.usecase.tool

import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class AddToolUseCase @Inject constructor(
    private val toolRepository: ToolRepository,
    private val emailRepository: EmailRepository
) {
    suspend operator fun invoke(tool: Tool): Long {
        val toolId = toolRepository.insertTool(tool)
        emailRepository.syncNewToolWithExistingEmails(toolId)
        return toolId
    }
}
