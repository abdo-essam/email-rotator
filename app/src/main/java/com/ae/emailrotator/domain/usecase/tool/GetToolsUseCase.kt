package com.ae.emailrotator.domain.usecase.tool

import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.repository.ToolRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetToolsUseCase @Inject constructor(
    private val repository: ToolRepository
) {
    operator fun invoke(): Flow<List<Tool>> = repository.getAllTools()
    suspend fun byName(name: String): Tool? = repository.getToolByName(name)
}
