package com.ae.emailrotator.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.ae.emailrotator.presentation.components.BottomBarTab
import com.ae.emailrotator.presentation.components.ModernBottomBar
import com.ae.emailrotator.presentation.dashboard.DashboardScreen
import com.ae.emailrotator.presentation.emails.EmailsScreen

object Routes {
    const val DASHBOARD = "dashboard"
    const val EMAILS = "emails"
}

@Composable
fun MainNavGraph(isDarkMode: Boolean, onToggleDarkMode: () -> Unit) {
    val navController = rememberNavController()
    val navEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navEntry?.destination?.route

    val tabs = listOf(
        BottomBarTab(Routes.DASHBOARD, "Home", Icons.Outlined.Home, Icons.Filled.Home),
        BottomBarTab(Routes.EMAILS, "Emails", Icons.Outlined.Email, Icons.Filled.Email)
    )

    Scaffold(
        bottomBar = {
            ModernBottomBar(
                tabs = tabs,
                currentRoute = currentRoute,
                onTabSelected = { route ->
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
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
                DashboardScreen(isDarkMode = isDarkMode, onToggleDarkMode = onToggleDarkMode)
            }
            composable(Routes.EMAILS) {
                EmailsScreen()
            }
        }
    }
}