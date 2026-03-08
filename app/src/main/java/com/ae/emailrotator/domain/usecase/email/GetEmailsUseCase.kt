package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.repository.EmailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmailsUseCase @Inject constructor(
    private val emailRepository: EmailRepository
) {
    operator fun invoke(): Flow<List<Email>> = emailRepository.getAllEmails()
    fun search(query: String): Flow<List<Email>> = emailRepository.searchEmails(query)
    fun byTool(toolId: Long): Flow<List<Email>> = emailRepository.getEmailsByToolId(toolId)
    fun byId(id: Long): Flow<Email?> = emailRepository.getEmailById(id)
}