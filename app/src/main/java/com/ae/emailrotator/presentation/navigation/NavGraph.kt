package com.ae.emailrotator.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.*
import com.ae.emailrotator.R
import com.ae.emailrotator.presentation.components.BottomBarTab
import com.ae.emailrotator.presentation.components.ModernBottomBar
import com.ae.emailrotator.presentation.dashboard.DashboardScreen
import com.ae.emailrotator.presentation.emails.EmailsScreen
import com.ae.emailrotator.presentation.settings.SettingsScreen
import com.ae.emailrotator.presentation.tools.ToolsScreen
import com.ae.emailrotator.presentation.verification.VerificationScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val EMAILS = "emails"
    const val TOOLS = "tools"
    const val SETTINGS = "settings"
    const val VERIFICATION = "verification"
}

@Composable
fun MainNavGraph(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit
) {
    val navController = rememberNavController()
    val navEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navEntry?.destination?.route

    val tabs = listOf(
        BottomBarTab(
            route = Routes.DASHBOARD,
            label = stringResource(R.string.nav_dashboard),
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home
        ),
        BottomBarTab(
            route = Routes.EMAILS,
            label = stringResource(R.string.nav_emails),
            icon = Icons.Outlined.Email,
            selectedIcon = Icons.Filled.Email
        ),
        BottomBarTab(
            route = Routes.TOOLS,
            label = stringResource(R.string.tools_title),
            icon = Icons.Outlined.Build,
            selectedIcon = Icons.Filled.Build
        ),
        BottomBarTab(
            route = Routes.SETTINGS,
            label = stringResource(R.string.nav_settings),
            icon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings
        )
    )

    Scaffold(
        bottomBar = {
            ModernBottomBar(
                tabs = tabs,
                currentRoute = currentRoute,
                onTabSelected = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
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
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onNavigateToVerification = {
                        navController.navigate(Routes.VERIFICATION)
                    }
                )
            }
            composable(Routes.EMAILS) {
                EmailsScreen()
            }
            composable(Routes.TOOLS) {
                ToolsScreen()
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode
                )
            }
            composable(Routes.VERIFICATION) {
                VerificationScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}