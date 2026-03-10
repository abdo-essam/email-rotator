package com.ae.emailrotator.domain.usecase

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.repository.EmailRepository
import javax.inject.Inject

class AddEmailUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    suspend operator fun invoke(email: Email): Long =
        repository.insertEmail(email)
}
