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
    operator fun invoke(): Flow<DashboardStats> = combine(
        emailRepository.getAllEmails(),
        toolRepository.getAllTools()
    ) { emails, tools ->
        val total = emails.size
        val active = emails.count { it.status == EmailStatus.AVAILABLE }
        val limited = emails.count { it.status == EmailStatus.LIMITED }
        val needsVerification = emails.count { it.status == EmailStatus.NEEDS_VERIFICATION }

        val toolStats = tools.map { tool ->
            val toolEmails = emails.filter { it.toolId == tool.id }
            ToolStat(
                toolId = tool.id,
                toolName = tool.name,
                totalEmails = toolEmails.size,
                activeEmails = toolEmails.count { it.status == EmailStatus.AVAILABLE }
            )
        }

        DashboardStats(
            totalEmails = total,
            activeEmails = active,
            limitedEmails = limited,
            needsVerificationEmails = needsVerification,
            toolStats = toolStats
        )
    }
}
