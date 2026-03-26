package com.ae.emailrotator.domain.usecase.stats

import com.ae.emailrotator.domain.model.DayAvailability
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.util.DateTimeUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetDayAvailabilityUseCase @Inject constructor(
    private val repository: EmailRepository
) {
    operator fun invoke(dateSelection: Long): Flow<DayAvailability> =
        repository.getAllEmailStatuses().map { allStatuses ->
            // Filter emails that will become available on the selected date
            val matchingEmails = allStatuses.filter { email ->
                email.availableAt != null && DateTimeUtil.isSameDay(email.availableAt, dateSelection)
            }
            
            DayAvailability(
                date = dateSelection,
                dateFormatted = DateTimeUtil.formatDate(dateSelection),
                emails = matchingEmails,
                count = matchingEmails.size
            )
        }
}
