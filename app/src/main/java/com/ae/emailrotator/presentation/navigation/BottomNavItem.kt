package com.ae.emailrotator.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : BottomNavItem("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Emails : BottomNavItem("emails", "Emails", Icons.Default.Email)
    data object Tools : BottomNavItem("tools", "Tools", Icons.Default.Build)
    data object History : BottomNavItem("history", "History", Icons.Default.History)
}

object NavRoutes {
    const val ADD_EMAIL = "add_email"
    const val EDIT_EMAIL = "edit_email/{emailId}"
    const val ADD_TOOL = "add_tool"
    const val EDIT_TOOL = "edit_tool/{toolId}"

    fun editEmail(emailId: Long) = "edit_email/$emailId"
    fun editTool(toolId: Long) = "edit_tool/$toolId"
}
