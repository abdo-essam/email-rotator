package com.ae.emailrotator.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ae.emailrotator.presentation.dashboard.DashboardScreen
import com.ae.emailrotator.presentation.email.AddEditEmailScreen
import com.ae.emailrotator.presentation.email.EmailManagementScreen
import com.ae.emailrotator.presentation.history.HistoryScreen
import com.ae.emailrotator.presentation.tool.AddEditToolScreen
import com.ae.emailrotator.presentation.tool.ToolManagementScreen
import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.ae.emailrotator.presentation.device.DeviceDetailScreen
import com.ae.emailrotator.presentation.device.DevicesScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(isDarkMode: Boolean, onToggleDarkMode: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Dashboard, BottomNavItem.Devices,
        BottomNavItem.Emails, BottomNavItem.History
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in items.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    item.title,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen(navController, isDarkMode, onToggleDarkMode)
            }
            composable(BottomNavItem.Devices.route) {
                DevicesScreen(navController)
            }
            composable(BottomNavItem.Emails.route) {
                EmailManagementScreen(navController)
            }
            composable(BottomNavItem.History.route) {
                HistoryScreen()
            }
            composable(
                NavRoutes.DEVICE_DETAIL,
                arguments = listOf(navArgument("deviceId") { type = NavType.LongType })
            ) { entry ->
                DeviceDetailScreen(navController, entry.arguments!!.getLong("deviceId"))
            }
            composable(
                NavRoutes.ADD_TOOL,
                arguments = listOf(navArgument("deviceId") { type = NavType.LongType })
            ) { entry ->
                AddEditToolScreen(navController, toolId = null, deviceId = entry.arguments!!.getLong("deviceId"))
            }
            composable(
                NavRoutes.EDIT_TOOL,
                arguments = listOf(navArgument("toolId") { type = NavType.LongType })
            ) { entry ->
                AddEditToolScreen(navController, toolId = entry.arguments!!.getLong("toolId"), deviceId = null)
            }

            composable(
                NavRoutes.TOOL_DETAIL,
                arguments = listOf(navArgument("toolId") { type = NavType.LongType })
            ) { entry ->
                ToolDetailScreen(navController, entry.arguments!!.getLong("toolId"))
            }

            composable(NavRoutes.ADD_EMAIL) {
                AddEditEmailScreen(navController, emailId = null, preselectedToolId = null)
            }
            composable(
                NavRoutes.ADD_EMAIL_FOR_TOOL,
                arguments = listOf(navArgument("toolId") { type = NavType.LongType })
            ) { entry ->
                AddEditEmailScreen(
                    navController,
                    emailId = null,
                    preselectedToolId = entry.arguments!!.getLong("toolId")
                )
            }
            composable(
                NavRoutes.EDIT_EMAIL,
                arguments = listOf(navArgument("emailId") { type = NavType.LongType })
            ) { entry ->
                AddEditEmailScreen(
                    navController,
                    emailId = entry.arguments!!.getLong("emailId"),
                    preselectedToolId = null
                )
            }
        }
    }
}