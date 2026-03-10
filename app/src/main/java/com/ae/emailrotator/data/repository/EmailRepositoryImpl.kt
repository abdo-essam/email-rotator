package com.ae.emailrotator.data.repository

import com.ae.emailrotator.data.local.dao.EmailDao
import com.ae.emailrotator.data.local.entity.toEntity
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmailRepositoryImpl @Inject constructor(
    private val dao: EmailDao
) : EmailRepository {

    override fun getAllEmails(): Flow<List<Email>> =
        dao.getAllEmails().map { list -> list.map { it.email.toDomain(it.toolName ?: "") } }

    override fun getEmailsByTool(toolId: Long): Flow<List<Email>> =
        dao.getEmailsByTool(toolId).map { list -> list.map { it.email.toDomain(it.toolName ?: "") } }

    override fun getUsableByTool(toolId: Long): Flow<List<Email>> =
        dao.getUsableByTool(toolId).map { list -> list.map { it.email.toDomain(it.toolName ?: "") } }

    override fun getEmailsByStatus(status: EmailStatus): Flow<List<Email>> =
        dao.getEmailsByStatus(status).map { list -> list.map { it.email.toDomain(it.toolName ?: "") } }

    override fun searchEmails(query: String): Flow<List<Email>> =
        dao.searchEmails(query).map { list -> list.map { it.email.toDomain(it.toolName ?: "") } }

    override suspend fun getEmailById(id: Long): Email? =
        dao.getById(id)?.toDomain()

    override suspend fun insertEmail(email: Email): Long =
        dao.insert(email.toEntity())

    override suspend fun updateEmail(email: Email) =
        dao.update(email.toEntity())

    override suspend fun deleteEmail(id: Long) =
        dao.delete(id)

    override suspend fun limitEmail(id: Long, availableAt: Long) =
        dao.limitEmail(id, availableAt)

    override suspend fun verifyEmail(id: Long) =
        dao.verifyEmail(id)

    override suspend fun refreshAvailability(): List<Email> {
        val now = System.currentTimeMillis()
        val expired = dao.getExpiredLimited(now).map { it.toDomain() }
        dao.markAvailableWhereExpired(now)
        return expired
    }

    override suspend fun getNextUsable(toolId: Long): Email? =
        dao.getNextUsable(toolId)?.toDomain()
}