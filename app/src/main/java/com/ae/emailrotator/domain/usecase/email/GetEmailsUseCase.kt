package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject

class GetEmailsUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    operator fun invoke(): Flow<List<Email>> = repository.getAllEmailStatuses()
    fun byTool(toolId: Long): Flow<List<Email>> = repository.getEmailsForTool(toolId)
    fun usable(toolId: Long): Flow<List<Email>> = repository.getUsableEmailsForTool(toolId)
    fun availableForTool(toolId: Long): Flow<List<Email>> = repository.getEmailsForTool(toolId).map { list ->
        list.filter { it.status == EmailStatus.AVAILABLE }
    }
    fun limitedForTool(toolId: Long): Flow<List<Email>> = repository.getEmailsForTool(toolId).map { list ->
        list.filter { it.status == EmailStatus.LIMITED }
    }
    fun byStatus(status: EmailStatus): Flow<List<Email>> = repository.getEmailsByStatus(status)
    fun search(query: String): Flow<List<Email>> = repository.searchEmails(query)
}

