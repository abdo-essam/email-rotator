package com.ae.emailrotator.domain.usecase.tool

import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class AddToolUseCase @Inject constructor(
    private val repository: ToolRepository
) {
    suspend operator fun invoke(tool: Tool): Long =
        repository.insertTool(tool)
}
