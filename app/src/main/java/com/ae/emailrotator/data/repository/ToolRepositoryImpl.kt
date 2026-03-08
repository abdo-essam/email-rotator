package com.ae.emailrotator.data.repository

import com.ae.emailrotator.data.local.dao.ToolDao
import com.ae.emailrotator.data.local.dao.ToolEmailDao
import com.ae.emailrotator.data.local.entity.ToolEmailCrossRef
import com.ae.emailrotator.data.mapper.toDomain
import com.ae.emailrotator.data.mapper.toEmailInTool
import com.ae.emailrotator.data.mapper.toEntity
import com.ae.emailrotator.domain.model.Tool
import com.ae.emailrotator.domain.model.ToolWithEmails
import com.ae.emailrotator.domain.repository.ToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ToolRepositoryImpl @Inject constructor(
    private val toolDao: ToolDao,
    private val toolEmailDao: ToolEmailDao
) : ToolRepository {

    override fun getAllTools(): Flow<List<Tool>> =
        toolDao.getAllTools().map { list -> list.map { it.toDomain() } }

    override fun getToolById(id: Long): Flow<Tool?> =
        toolDao.getToolById(id).map { it?.toDomain() }

    override fun getToolsByDeviceId(deviceId: Long): Flow<List<Tool>> =
        toolDao.getToolsByDeviceId(deviceId).map { list -> list.map { it.toDomain() } }

    override fun getAllToolsWithEmails(): Flow<List<ToolWithEmails>> =
        combine(toolDao.getAllTools(), toolEmailDao.getAllToolEmailJoins()) { tools, joins ->
            tools.map { te ->
                val tool = te.toDomain()
                val emails = joins.filter { it.toolId == tool.id }.map { it.toEmailInTool() }
                val active = tool.currentActiveEmailId?.let { id ->
                    emails.find { it.email.id == id }?.email
                }
                ToolWithEmails(tool, emails, active)
            }
        }

    override fun getToolWithEmails(toolId: Long): Flow<ToolWithEmails?> =
        combine(toolDao.getToolById(toolId), toolEmailDao.getEmailsForToolFlow(toolId)) { te, joins ->
            te?.let {
                val tool = it.toDomain()
                val emails = joins.map { j -> j.toEmailInTool() }
                val active = tool.currentActiveEmailId?.let { id ->
                    emails.find { e -> e.email.id == id }?.email
                }
                ToolWithEmails(tool, emails, active)
            }
        }

    override fun getToolsWithEmailsByDevice(deviceId: Long): Flow<List<ToolWithEmails>> =
        combine(toolDao.getToolsByDeviceId(deviceId), toolEmailDao.getAllToolEmailJoins()) { tools, joins ->
            tools.map { te ->
                val tool = te.toDomain()
                val emails = joins.filter { it.toolId == tool.id }.map { it.toEmailInTool() }
                val active = tool.currentActiveEmailId?.let { id ->
                    emails.find { it.email.id == id }?.email
                }
                ToolWithEmails(tool, emails, active)
            }
        }

    override suspend fun insertTool(tool: Tool): Long = toolDao.insert(tool.toEntity())
    override suspend fun updateTool(tool: Tool) = toolDao.update(tool.toEntity())
    override suspend fun deleteTool(id: Long) = toolDao.delete(id)
    override suspend fun setActiveEmail(toolId: Long, emailId: Long?) = toolDao.setActiveEmail(toolId, emailId)

    override suspend fun assignEmailsToTool(toolId: Long, emailIds: List<Long>) {
        toolEmailDao.deleteAllForTool(toolId)
        toolEmailDao.insertAll(emailIds.mapIndexed { i, id -> ToolEmailCrossRef(toolId, id, i) })
    }

    override suspend fun getToolByIdOnce(id: Long): Tool? = toolDao.getToolByIdOnce(id)?.toDomain()
    override suspend fun getToolsWithEmailOnce(emailId: Long): List<Tool> =
        toolDao.getToolsContainingEmail(emailId).map { it.toDomain() }

    override suspend fun getToolWithEmailsOnce(toolId: Long): ToolWithEmails? {
        val te = toolDao.getToolByIdOnce(toolId) ?: return null
        val tool = te.toDomain()
        val emails = toolEmailDao.getEmailsForTool(toolId).map { it.toEmailInTool() }
        val active = tool.currentActiveEmailId?.let { id -> emails.find { it.email.id == id }?.email }
        return ToolWithEmails(tool, emails, active)
    }
}