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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val navController = rememberNavController()
    val bottomNavItems = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Emails,
        BottomNavItem.Tools,
        BottomNavItem.History
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
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
                DashboardScreen(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode
                )
            }
            composable(BottomNavItem.Emails.route) {
                EmailManagementScreen(navController = navController)
            }
            composable(BottomNavItem.Tools.route) {
                ToolManagementScreen(navController = navController)
            }
            composable(BottomNavItem.History.route) {
                HistoryScreen()
            }
            composable(NavRoutes.ADD_EMAIL) {
                AddEditEmailScreen(
                    navController = navController,
                    emailId = null
                )
            }
            composable(
                route = NavRoutes.EDIT_EMAIL,
                arguments = listOf(navArgument("emailId") { type = NavType.LongType })
            ) { backStackEntry ->
                val emailId = backStackEntry.arguments?.getLong("emailId")
                AddEditEmailScreen(
                    navController = navController,
                    emailId = emailId
                )
            }
            composable(NavRoutes.ADD_TOOL) {
                AddEditToolScreen(
                    navController = navController,
                    toolId = null
                )
            }
            composable(
                route = NavRoutes.EDIT_TOOL,
                arguments = listOf(navArgument("toolId") { type = NavType.LongType })
            ) { backStackEntry ->
                val toolId = backStackEntry.arguments?.getLong("toolId")
                AddEditToolScreen(
                    navController = navController,
                    toolId = toolId
                )
            }
        }
    }
}
