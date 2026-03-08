package com.ae.emailrotator.domain.repository

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import kotlinx.coroutines.flow.Flow

interface EmailRepository {
    fun getAllEmails(): Flow<List<Email>>
    fun getEmailById(id: Long): Flow<Email?>
    fun searchEmails(query: String): Flow<List<Email>>
    fun getEmailsByToolId(toolId: Long): Flow<List<Email>>
    suspend fun insertEmail(email: Email): Long
    suspend fun updateEmail(email: Email)
    suspend fun deleteEmail(id: Long)
    suspend fun updateEmailStatus(id: Long, status: EmailStatus, availableAt: Long?)
    suspend fun refreshAvailability(): List<Long>
    suspend fun getEmailByIdOnce(id: Long): Email?
    suspend fun getAvailableEmailsForTool(toolId: Long): List<Email>
}
