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
    private val usageHistoryDao: UsageHistoryDao
) : UsageHistoryRepository {

    override fun getAllHistory(): Flow<List<UsageHistory>> =
        usageHistoryDao.getAllHistory().map { list -> list.map { it.toDomain() } }

    override fun getHistoryForEmail(emailId: Long): Flow<List<UsageHistory>> =
        usageHistoryDao.getHistoryForEmail(emailId).map { list -> list.map { it.toDomain() } }

    override fun getHistoryForTool(toolId: Long): Flow<List<UsageHistory>> =
        usageHistoryDao.getHistoryForTool(toolId).map { list -> list.map { it.toDomain() } }

    override suspend fun record(emailId: Long, toolId: Long, action: HistoryAction) {
        usageHistoryDao.insert(
            UsageHistoryEntity(
                emailId = emailId,
                toolId = toolId,
                action = action.name,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
