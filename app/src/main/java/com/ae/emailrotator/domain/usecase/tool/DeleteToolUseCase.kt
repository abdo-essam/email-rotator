package com.ae.emailrotator.domain.usecase.tool

import com.ae.emailrotator.domain.repository.ToolRepository
import javax.inject.Inject

class DeleteToolUseCase @Inject constructor(
    private val repository: ToolRepository
) {
    suspend operator fun invoke(id: Long) =
        repository.deleteTool(id)
}
