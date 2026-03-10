package com.ae.emailrotator.domain.repository

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.ToolType
import kotlinx.coroutines.flow.Flow

interface EmailRepository {
    fun getAllEmails(): Flow<List<Email>>
    fun getEmailsByTool(tool: ToolType): Flow<List<Email>>
    fun getAvailableByTool(tool: ToolType): Flow<List<Email>>
    fun searchEmails(query: String): Flow<List<Email>>
    suspend fun getEmailById(id: Long): Email?
    suspend fun insertEmail(email: Email): Long
    suspend fun updateEmail(email: Email)
    suspend fun deleteEmail(id: Long)
    suspend fun limitEmail(id: Long, availableAt: Long)
    suspend fun refreshAvailability(): List<Email>
    suspend fun getNextAvailable(tool: ToolType): Email?
}
