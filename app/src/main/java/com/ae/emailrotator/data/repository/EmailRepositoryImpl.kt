package com.ae.emailrotator.data.repository

import com.ae.emailrotator.data.local.dao.EmailDao
import com.ae.emailrotator.data.local.dao.ToolEmailDao
import com.ae.emailrotator.data.mapper.toDomain
import com.ae.emailrotator.data.mapper.toEntity
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val emailDao: EmailDao,
    private val toolEmailDao: ToolEmailDao
) : EmailRepository {

    override fun getAllEmails(): Flow<List<Email>> =
        emailDao.getAllEmails().flatMapLatest { entities ->
            flow {
                val result = entities.map { entity ->
                    val toolIds = toolEmailDao.getToolIdsForEmail(entity.id)
                    entity.toDomain().copy(assignedToolIds = toolIds)
                }
                emit(result)
            }
        }

    override fun getEmailById(id: Long): Flow<Email?> =
        emailDao.getEmailById(id).flatMapLatest { entity ->
            flow {
                val result = entity?.let {
                    val toolIds = toolEmailDao.getToolIdsForEmail(it.id)
                    it.toDomain().copy(assignedToolIds = toolIds)
                }
                emit(result)
            }
        }

    override fun searchEmails(query: String): Flow<List<Email>> =
        emailDao.searchEmails(query).flatMapLatest { entities ->
            flow {
                val result = entities.map { entity ->
                    val toolIds = toolEmailDao.getToolIdsForEmail(entity.id)
                    entity.toDomain().copy(assignedToolIds = toolIds)
                }
                emit(result)
            }
        }

    override fun getEmailsByToolId(toolId: Long): Flow<List<Email>> =
        emailDao.getEmailsByToolId(toolId).map { list ->
            list.map { it.toDomain() }
        }

    override suspend fun insertEmail(email: Email): Long =
        emailDao.insert(email.toEntity())

    override suspend fun updateEmail(email: Email) =
        emailDao.update(email.toEntity())

    override suspend fun deleteEmail(id: Long) =
        emailDao.delete(id)

    override suspend fun updateEmailStatus(id: Long, status: EmailStatus, availableAt: Long?) =
        emailDao.updateStatus(id, status.name, availableAt)

    override suspend fun refreshAvailability(): List<Long> {
        val now = System.currentTimeMillis()
        val ids = emailDao.getEmailIdsToRefresh(now)
        emailDao.markAvailableWhereTimeReached(now)
        return ids
    }

    override suspend fun getEmailByIdOnce(id: Long): Email? =
        emailDao.getEmailByIdOnce(id)?.let { entity ->
            val toolIds = toolEmailDao.getToolIdsForEmail(entity.id)
            entity.toDomain().copy(assignedToolIds = toolIds)
        }

    override suspend fun getAvailableEmailsForTool(toolId: Long): List<Email> =
        emailDao.getAvailableEmailsForTool(toolId).map { it.toDomain() }
}
