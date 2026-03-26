package com.ae.emailrotator.data.repository

import com.ae.emailrotator.data.local.dao.GlobalEmailDao
import com.ae.emailrotator.data.local.dao.ToolDao
import com.ae.emailrotator.data.local.dao.ToolEmailStatusDao
import com.ae.emailrotator.data.local.entity.GlobalEmailEntity
import com.ae.emailrotator.data.local.entity.ToolEmailStatusEntity
import com.ae.emailrotator.data.mapper.toDomain
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val globalEmailDao: GlobalEmailDao,
    private val toolEmailStatusDao: ToolEmailStatusDao,
    private val toolDao: ToolDao
) : EmailRepository {

    override fun getAllEmailStatuses(): Flow<List<Email>> =
        toolEmailStatusDao.getAllEmailStatuses()
            .map { list -> list.map { it.toDomain() } }

    override fun getEmailsForTool(toolId: Long): Flow<List<Email>> =
        toolEmailStatusDao.getEmailsForTool(toolId)
            .map { list -> list.map { it.toDomain() } }

    override fun getUsableEmailsForTool(toolId: Long): Flow<List<Email>> =
        toolEmailStatusDao.getUsableEmailsForTool(toolId)
            .map { list -> list.map { it.toDomain() } }

    override fun getEmailsByStatus(status: EmailStatus): Flow<List<Email>> =
        toolEmailStatusDao.getEmailsByStatus(status.name)
            .map { list -> list.map { it.toDomain() } }

    override fun searchEmails(query: String): Flow<List<Email>> =
        toolEmailStatusDao.searchEmails(query)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun addEmailToAllTools(
        address: String,
        initialStatus: EmailStatus
    ): Long {
        val emailId = globalEmailDao.insert(
            GlobalEmailEntity(address = address)
        )
        val tools = toolDao.getAllToolsSnapshot()
        val statuses = tools.map { tool ->
            ToolEmailStatusEntity(
                emailId = emailId,
                toolId = tool.id,
                status = initialStatus.name,
                lastUsedAt = System.currentTimeMillis() // Set initial lastUsedAt
            )
        }
        toolEmailStatusDao.insertAll(statuses)
        return emailId
    }

    override suspend fun updateEmailAddress(emailId: Long, newAddress: String) {
        val existing = globalEmailDao.getById(emailId) ?: return
        globalEmailDao.update(existing.copy(address = newAddress))
    }

    override suspend fun deleteEmail(emailId: Long) {
        globalEmailDao.delete(emailId)
    }

    override suspend fun limitEmail(statusId: Long, availableAt: Long) =
        toolEmailStatusDao.limitEmail(statusId, availableAt, System.currentTimeMillis())

    override suspend fun updateLimitTime(statusId: Long, availableAt: Long) =
        toolEmailStatusDao.updateLimitTime(statusId, availableAt)

    override suspend fun verifyEmail(emailId: Long, toolId: Long) =
        toolEmailStatusDao.verifyEmail(emailId, toolId)

    override suspend fun refreshAvailability(): List<Email> {
        val now = System.currentTimeMillis()
        val expired = toolEmailStatusDao.getExpiredLimited(now).map { it.toDomain() }
        toolEmailStatusDao.markAvailableWhereExpired(now)
        return expired
    }

    override suspend fun getNextUsable(toolId: Long): Email? =
        toolEmailStatusDao.getNextUsable(toolId)?.toDomain()

    override suspend fun syncNewToolWithExistingEmails(toolId: Long) {
        val allEmails = globalEmailDao.getAllEmailsSnapshot()
        val now = System.currentTimeMillis()
        val statuses = allEmails.map { email ->
            ToolEmailStatusEntity(
                emailId = email.id,
                toolId = toolId,
                status = EmailStatus.AVAILABLE.name,
                lastUsedAt = now
            )
        }
        toolEmailStatusDao.insertAll(statuses)
    }
}