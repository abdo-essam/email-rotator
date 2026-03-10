package com.ae.emailrotator.domain.usecase.tool

import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class UpdateToolUseCase @Inject constructor(
    private val repository: ToolRepository
) {
    suspend operator fun invoke(tool: Tool) =
        repository.updateTool(tool)
}
