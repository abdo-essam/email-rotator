package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.repository.EmailRepository
import javax.inject.Inject

class UpdateEmailUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    suspend operator fun invoke(emailId: Long, newAddress: String) =
        repository.updateEmailAddress(emailId, newAddress)
}
