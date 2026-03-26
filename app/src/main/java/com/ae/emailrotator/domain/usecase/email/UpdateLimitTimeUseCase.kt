package com.ae.emailrotator.domain.usecase.email

import com.ae.emailrotator.domain.repository.EmailRepository
import javax.inject.Inject

class UpdateLimitTimeUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    suspend operator fun invoke(statusId: Long, newAvailableAt: Long) =
        repository.updateLimitTime(statusId, newAvailableAt)
}
