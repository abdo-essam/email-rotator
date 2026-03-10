package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmailsUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    operator fun invoke(): Flow<List<Email>> = repository.getAllEmails()
    fun byTool(toolId: Long): Flow<List<Email>> = repository.getEmailsByTool(toolId)
    fun usable(toolId: Long): Flow<List<Email>> = repository.getUsableByTool(toolId)
    fun byStatus(status: EmailStatus): Flow<List<Email>> = repository.getEmailsByStatus(status)
    fun search(query: String): Flow<List<Email>> = repository.searchEmails(query)
}
