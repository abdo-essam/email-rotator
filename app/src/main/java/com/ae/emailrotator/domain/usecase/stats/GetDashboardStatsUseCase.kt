package com.ae.emailrotator.domain.usecase.stats

import com.ae.emailrotator.domain.model.DashboardStats
import com.ae.emailrotator.domain.model.EmailStatus
import com.ae.emailrotator.domain.model.ToolStat
import com.ae.emailrotator.domain.repository.EmailRepository
import com.ae.emailrotator.domain.repository.ToolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDashboardStatsUseCase @Inject constructor(
    private val emailRepository: EmailRepository,
    private val toolRepository: ToolRepository
) {
    operator fun invoke(): Flow<DashboardStats> =
        combine(
            emailRepository.getAllEmailStatuses(),
            toolRepository.getAllTools()
        ) { emailStatuses, tools ->
            val toolStats = tools.map { tool ->
                val toolEmails = emailStatuses.filter { it.toolId == tool.id }
                ToolStat(
                    toolId = tool.id,
                    toolName = tool.name,
                    totalEmails = toolEmails.size,
                    activeEmails = toolEmails.count { it.status == EmailStatus.AVAILABLE }
                )
            }
            DashboardStats(
                totalEmails = emailStatuses.map { it.address }.distinct().size,
                activeEmails = emailStatuses
                    .filter { it.status == EmailStatus.AVAILABLE }
                    .map { it.address }
                    .distinct()
                    .size,
                limitedEmails = emailStatuses
                    .filter { it.status == EmailStatus.LIMITED }
                    .map { it.address }
                    .distinct()
                    .size,
                needsVerificationEmails = emailStatuses
                    .filter { it.status == EmailStatus.NEEDS_VERIFICATION }
                    .map { it.address }
                    .distinct()
                    .size,
                toolStats = toolStats
            )
        }
}
