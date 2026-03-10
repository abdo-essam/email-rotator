package com.ae.emailrotator.domain.usecase

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.ToolType
import com.ae.emailrotator.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmailsUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    operator fun invoke(): Flow<List<Email>> = repository.getAllEmails()
    fun byTool(tool: ToolType): Flow<List<Email>> = repository.getEmailsByTool(tool)
    fun available(tool: ToolType): Flow<List<Email>> = repository.getAvailableByTool(tool)
    fun search(query: String): Flow<List<Email>> = repository.searchEmails(query)
}
