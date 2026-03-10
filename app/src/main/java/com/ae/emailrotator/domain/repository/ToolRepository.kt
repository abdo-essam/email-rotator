package com.ae.emailrotator.domain.repository

import com.ae.emailrotator.domain.model.Tool
import kotlinx.coroutines.flow.Flow

interface ToolRepository {
    fun getAllTools(): Flow<List<Tool>>
    suspend fun getToolById(id: Long): Tool?
    suspend fun insertTool(tool: Tool): Long
    suspend fun updateTool(tool: Tool)
    suspend fun deleteTool(id: Long)
    suspend fun getToolByName(name: String): Tool?
}
