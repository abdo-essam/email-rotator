package com.ae.emailrotator.domain.usecase

import com.ae.emailrotator.domain.model.Email
import com.ae.emailrotator.domain.model.ToolType
import com.ae.emailrotator.domain.repository.EmailRepository
import javax.inject.Inject

class GetNextAvailableEmailUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    suspend operator fun invoke(tool: ToolType): Email? =
        repository.getNextAvailable(tool)
}
