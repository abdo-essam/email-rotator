package com.ae.emailrotator.domain.usecase.tool

import com.ae.emailrotator.domain.model.ToolWithEmails
import com.ae.emailrotator.domain.repository.ToolRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetToolsUseCase @Inject constructor(
    private val toolRepository: ToolRepository
) {
    operator fun invoke(): Flow<List<ToolWithEmails>> = toolRepository.getAllToolsWithEmails()
    fun byDevice(deviceId: Long): Flow<List<ToolWithEmails>> =
        toolRepository.getToolsWithEmailsByDevice(deviceId)
    fun byId(toolId: Long): Flow<ToolWithEmails?> = toolRepository.getToolWithEmails(toolId)
}