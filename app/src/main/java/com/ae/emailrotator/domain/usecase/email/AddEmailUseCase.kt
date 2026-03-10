package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.repository.EmailRepository
import javax.inject.Inject

class AddEmailUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    suspend operator fun invoke(
        address: String,
        needsVerification: Boolean = false
    ): Long {
        val status = if (needsVerification) EmailStatus.NEEDS_VERIFICATION
        else EmailStatus.AVAILABLE
        return repository.addEmailToAllTools(address, status)
    }
}
