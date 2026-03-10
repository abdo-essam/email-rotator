package com.ae.emailrotator.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ae.emailrotator.R
import com.ae.emailrotator.presentation.components.BottomBarTab
import com.ae.emailrotator.presentation.components.ModernBottomBar
import com.ae.emailrotator.presentation.dashboard.DashboardScreen
import com.ae.emailrotator.presentation.emails.EmailsScreen
import com.ae.emailrotator.presentation.settings.SettingsScreen
import com.ae.emailrotator.presentation.tools.ToolsScreen
import com.ae.emailrotator.presentation.verification.VerificationScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Emails : Screen("emails")
    object Settings : Screen("settings")
    object Verification : Screen("verification")
    object Tools : Screen("tools")
}

@Composable
fun MainNavGraph(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabs = listOf(
        BottomBarTab(
            route = Screen.Dashboard.route,
            label = stringResource(R.string.nav_dashboard),
            icon = Icons.Outlined.Dashboard,
            selectedIcon = Icons.Filled.Dashboard
        ),
        BottomBarTab(
            route = Screen.Emails.route,
            label = stringResource(R.string.nav_emails),
            icon = Icons.Outlined.Email,
            selectedIcon = Icons.Filled.Email
        ),
        BottomBarTab(
            route = Screen.Settings.route,
            label = stringResource(R.string.nav_settings),
            icon = Icons.Outlined.Settings,
            selectedIcon = Icons.Filled.Settings
        )
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Emails.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                ModernBottomBar(
                    tabs = tabs,
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode,
                    onNavigateToVerification = { navController.navigate(Screen.Verification.route) }
                )
            }
            composable(Screen.Emails.route) {
                EmailsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    isDarkMode = isDarkMode,
                    onToggleDarkMode = onToggleDarkMode,
                    onNavigateToTools = { navController.navigate(Screen.Tools.route) }
                )
            }
            composable(Screen.Verification.route) {
                VerificationScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Tools.route) {
                ToolsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}