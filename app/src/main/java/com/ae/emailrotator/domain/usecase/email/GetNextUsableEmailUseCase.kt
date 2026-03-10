package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.repository.EmailRepository
import javax.inject.Inject

class GetNextUsableEmailUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    suspend operator fun invoke(toolId: Long): Email? =
        repository.getNextUsable(toolId)
}
