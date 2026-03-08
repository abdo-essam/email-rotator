package com.ae.emailrotator.domain.usecase.tool

import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class AddToolUseCase @Inject constructor(
    private val toolRepository: ToolRepository
) {
    suspend operator fun invoke(name: String, deviceId: Long, emailIds: List<Long>): Long {
        val toolId = toolRepository.insertTool(Tool(name = name, deviceId = deviceId))
        if (emailIds.isNotEmpty()) {
            toolRepository.assignEmailsToTool(toolId, emailIds)
            toolRepository.setActiveEmail(toolId, emailIds.first())
        }
        return toolId
    }
}