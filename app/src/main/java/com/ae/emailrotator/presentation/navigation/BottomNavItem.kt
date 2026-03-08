package com.ae.emailrotator.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.outlined.*


sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Dashboard : BottomNavItem("dashboard", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    data object Devices : BottomNavItem("devices", "Devices", Icons.Filled.Devices, Icons.Outlined.Devices)
    data object Emails : BottomNavItem("emails", "Emails", Icons.Filled.Email, Icons.Outlined.Email)
    data object History : BottomNavItem("history", "History", Icons.Filled.History, Icons.Outlined.History)
}

object NavRoutes {
    const val DEVICE_DETAIL = "device_detail/{deviceId}"
    const val ADD_TOOL = "add_tool/{deviceId}"
    const val EDIT_TOOL = "edit_tool/{toolId}"
    const val TOOL_DETAIL = "tool_detail/{toolId}"
    const val ADD_EMAIL = "add_email"
    const val ADD_EMAIL_FOR_TOOL = "add_email_for_tool/{toolId}"
    const val EDIT_EMAIL = "edit_email/{emailId}"

    fun deviceDetail(id: Long) = "device_detail/$id"
    fun addTool(deviceId: Long) = "add_tool/$deviceId"
    fun editTool(id: Long) = "edit_tool/$id"
    fun toolDetail(id: Long) = "tool_detail/$id"
    fun addEmailForTool(toolId: Long) = "add_email_for_tool/$toolId"
    fun editEmail(id: Long) = "edit_email/$id"
}