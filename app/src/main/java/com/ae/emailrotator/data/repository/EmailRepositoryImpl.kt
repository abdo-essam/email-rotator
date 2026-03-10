package com.ae.emailrotator.data.repository

import com.ae.emailrotator.data.local.dao.EmailDao
import com.ae.emailrotator.data.mapper.toDomain
import com.ae.emailrotator.data.mapper.toEntity
import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.ToolType
import com.ae.emailrotator.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val dao: EmailDao
) : EmailRepository {

    override fun getAllEmails(): Flow<List<Email>> =
        dao.getAllEmails().map { list -> list.map { it.toDomain() } }

    override fun getEmailsByTool(tool: ToolType): Flow<List<Email>> =
        dao.getEmailsByTool(tool.name).map { list -> list.map { it.toDomain() } }

    override fun getAvailableByTool(tool: ToolType): Flow<List<Email>> =
        dao.getAvailableByTool(tool.name).map { list -> list.map { it.toDomain() } }

    override fun searchEmails(query: String): Flow<List<Email>> =
        dao.searchEmails(query).map { list -> list.map { it.toDomain() } }

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

    override suspend fun refreshAvailability(): List<Email> {
        val now = System.currentTimeMillis()
        val expired = dao.getExpiredLimited(now).map { it.toDomain() }
        dao.markAvailableWhereExpired(now)
        return expired
    }

    override suspend fun getNextAvailable(tool: ToolType): Email? =
        dao.getNextAvailable(tool.name)?.toDomain()
}