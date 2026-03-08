package com.ae.emailrotator.domain.repository

import com.ae.emailrotator.domain.model.HistoryAction
import com.ae.emailrotator.domain.model.UsageHistory
import kotlinx.coroutines.flow.Flow

interface UsageHistoryRepository {
    fun getAllHistory(): Flow<List<UsageHistory>>
    fun getHistoryForEmail(emailId: Long): Flow<List<UsageHistory>>
    fun getHistoryForTool(toolId: Long): Flow<List<UsageHistory>>
    suspend fun record(emailId: Long, toolId: Long, action: HistoryAction)
}