package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.repository.EmailRepository
import javax.inject.Inject

class LimitEmailUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    suspend operator fun invoke(emailId: Long, availableAt: Long) =
        repository.limitEmail(emailId, availableAt)
}
