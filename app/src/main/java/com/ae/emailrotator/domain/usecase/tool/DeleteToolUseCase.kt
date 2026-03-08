package com.ae.emailrotator.domain.usecase.tool

import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class DeleteToolUseCase @Inject constructor(
    private val toolRepository: ToolRepository
) {
    suspend operator fun invoke(toolId: Long) {
        toolRepository.assignEmailsToTool(toolId, emptyList())
        toolRepository.deleteTool(toolId)
    }
}