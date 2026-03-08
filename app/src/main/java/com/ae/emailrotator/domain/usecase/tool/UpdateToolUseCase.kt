package com.ae.emailrotator.domain.usecase.tool

import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class UpdateToolUseCase @Inject constructor(
    private val toolRepository: ToolRepository
) {
    suspend operator fun invoke(tool: Tool, emailIds: List<Long>) {
        toolRepository.updateTool(tool)
        toolRepository.assignEmailsToTool(tool.id, emailIds)
        if (tool.currentActiveEmailId != null && tool.currentActiveEmailId !in emailIds) {
            toolRepository.setActiveEmail(tool.id, emailIds.firstOrNull())
        }
    }
}
