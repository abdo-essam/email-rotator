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

    override fun getAllToolsWithEmails(): Flow<List<ToolWithEmails>> {
        return combine(
            toolDao.getAllTools(),
            toolEmailDao.getAllToolEmailJoins()
        ) { tools, joins ->
            tools.map { toolEntity ->
                val tool = toolEntity.toDomain()
                val emailsInTool = joins
                    .filter { it.toolId == tool.id }
                    .map { it.toEmailInTool() }
                val activeEmail = tool.currentActiveEmailId?.let { activeId ->
                    emailsInTool.find { it.email.id == activeId }?.email
                }
                ToolWithEmails(
                    tool = tool,
                    emails = emailsInTool,
                    currentActiveEmail = activeEmail
                )
            }
        }
    }

    override fun getToolWithEmails(toolId: Long): Flow<ToolWithEmails?> {
        return combine(
            toolDao.getToolById(toolId),
            toolEmailDao.getEmailsForToolFlow(toolId)
        ) { toolEntity, joins ->
            toolEntity?.let {
                val tool = it.toDomain()
                val emailsInTool = joins.map { j -> j.toEmailInTool() }
                val activeEmail = tool.currentActiveEmailId?.let { activeId ->
                    emailsInTool.find { e -> e.email.id == activeId }?.email
                }
                ToolWithEmails(
                    tool = tool,
                    emails = emailsInTool,
                    currentActiveEmail = activeEmail
                )
            }
        }
    }

    override suspend fun insertTool(tool: Tool): Long =
        toolDao.insert(tool.toEntity())

    override suspend fun updateTool(tool: Tool) =
        toolDao.update(tool.toEntity())

    override suspend fun deleteTool(id: Long) =
        toolDao.delete(id)

    override suspend fun setActiveEmail(toolId: Long, emailId: Long?) =
        toolDao.setActiveEmail(toolId, emailId)

    override suspend fun assignEmailsToTool(toolId: Long, emailIds: List<Long>) {
        toolEmailDao.deleteAllForTool(toolId)
        val crossRefs = emailIds.mapIndexed { index, emailId ->
            ToolEmailCrossRef(
                toolId = toolId,
                emailId = emailId,
                orderIndex = index
            )
        }
        toolEmailDao.insertAll(crossRefs)
    }

    override suspend fun getToolByIdOnce(id: Long): Tool? =
        toolDao.getToolByIdOnce(id)?.toDomain()

    override suspend fun getToolsWithEmailOnce(emailId: Long): List<Tool> =
        toolDao.getToolsContainingEmail(emailId).map { it.toDomain() }

    override suspend fun getToolWithEmailsOnce(toolId: Long): ToolWithEmails? {
        val toolEntity = toolDao.getToolByIdOnce(toolId) ?: return null
        val tool = toolEntity.toDomain()
        val joins = toolEmailDao.getEmailsForTool(toolId)
        val emailsInTool = joins.map { it.toEmailInTool() }
        val activeEmail = tool.currentActiveEmailId?.let { activeId ->
            emailsInTool.find { it.email.id == activeId }?.email
        }
        return ToolWithEmails(
            tool = tool,
            emails = emailsInTool,
            currentActiveEmail = activeEmail
        )
    }
}
