package com.ae.emailrotator.domain.repository

import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.model.ToolWithEmails
import kotlinx.coroutines.flow.Flow

interface ToolRepository {
    fun getAllTools(): Flow<List<Tool>>
    fun getToolById(id: Long): Flow<Tool?>
    fun getToolsByDeviceId(deviceId: Long): Flow<List<Tool>>
    fun getAllToolsWithEmails(): Flow<List<ToolWithEmails>>
    fun getToolWithEmails(toolId: Long): Flow<ToolWithEmails?>
    fun getToolsWithEmailsByDevice(deviceId: Long): Flow<List<ToolWithEmails>>
    suspend fun insertTool(tool: Tool): Long
    suspend fun updateTool(tool: Tool)
    suspend fun deleteTool(id: Long)
    suspend fun setActiveEmail(toolId: Long, emailId: Long?)
    suspend fun assignEmailsToTool(toolId: Long, emailIds: List<Long>)
    suspend fun getToolByIdOnce(id: Long): Tool?
    suspend fun getToolsWithEmailOnce(emailId: Long): List<Tool>
    suspend fun getToolWithEmailsOnce(toolId: Long): ToolWithEmails?
}
