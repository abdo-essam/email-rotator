package com.ae.emailrotator.data.repository

import com.ae.emailrotator.data.local.dao.UsageHistoryDao
import com.ae.emailrotator.data.local.entity.UsageHistoryEntity
import com.ae.emailrotator.data.mapper.toDomain
import com.ae.emailrotator.domain.model.HistoryAction
import com.ae.emailrotator.domain.model.UsageHistory
import com.ae.emailrotator.domain.repository.UsageHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsageHistoryRepositoryImpl @Inject constructor(
    private val dao: UsageHistoryDao
) : UsageHistoryRepository {
    override fun getAllHistory(): Flow<List<UsageHistory>> =
        dao.getAllHistory().map { list -> list.map { it.toDomain() } }
    override fun getHistoryForEmail(emailId: Long): Flow<List<UsageHistory>> =
        dao.getHistoryForEmail(emailId).map { list -> list.map { it.toDomain() } }
    override fun getHistoryForTool(toolId: Long): Flow<List<UsageHistory>> =
        dao.getHistoryForTool(toolId).map { list -> list.map { it.toDomain() } }

    override suspend fun record(emailId: Long, toolId: Long, action: HistoryAction) {
        dao.insert(
            UsageHistoryEntity(
                emailId = emailId,
                toolId = toolId,
                action = action.name,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
