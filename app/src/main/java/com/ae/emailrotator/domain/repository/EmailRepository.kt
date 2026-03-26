package com.ae.emailrotator.domain.repository

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import kotlinx.coroutines.flow.Flow

interface EmailRepository {
    fun getAllEmailStatuses(): Flow<List<Email>>
    fun getEmailsForTool(toolId: Long): Flow<List<Email>>
    fun getUsableEmailsForTool(toolId: Long): Flow<List<Email>>
    fun getEmailsByStatus(status: EmailStatus): Flow<List<Email>>
    fun searchEmails(query: String): Flow<List<Email>>
    suspend fun addEmailToAllTools(address: String, initialStatus: EmailStatus): Long
    suspend fun updateEmailAddress(emailId: Long, newAddress: String)
    suspend fun deleteEmail(emailId: Long)
    suspend fun limitEmail(statusId: Long, availableAt: Long)
    suspend fun updateLimitTime(statusId: Long, availableAt: Long)
    suspend fun verifyEmail(emailId: Long, toolId: Long)

    suspend fun refreshAvailability(): List<Email>
    suspend fun getNextUsable(toolId: Long): Email?
    suspend fun syncNewToolWithExistingEmails(toolId: Long)
}
