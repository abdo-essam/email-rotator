package com.ae.emailrotator.domain.usecase

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.repository.EmailRepository
import javax.inject.Inject

class RefreshAvailabilityUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    suspend operator fun invoke(): List<Email> =
        repository.refreshAvailability()
}
