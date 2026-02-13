package com.vyaparlite.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.vyaparlite.data.AppContainer
import com.vyaparlite.ui.screens.AnalyticsScreen
import com.vyaparlite.ui.screens.DashboardScreen
import com.vyaparlite.ui.screens.EmployeesScreen
import com.vyaparlite.ui.screens.InventoryScreen
import com.vyaparlite.ui.screens.SalesScreen
import com.vyaparlite.ui.screens.SettingsScreen
import com.vyaparlite.ui.screens.SplashScreen

@Composable
fun AppNavHost(appContainer: AppContainer) {
    val navController = rememberNavController()
    val current = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomBar = current != Route.Splash.path

    LaunchedEffect(Unit) {
        appContainer.seedIfNeeded()
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavRoutes.forEach { item ->
                        NavigationBarItem(
                            selected = current == item.path,
                            onClick = {
                                navController.navigate(item.path) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                }
                            },
                            icon = { item.icon?.let { Icon(it, item.label) } },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Route.Splash.path,
            modifier = Modifier.padding(padding)
        ) {
            composable(Route.Splash.path) {
                SplashScreen(
                    onComplete = {
                        navController.navigate(Route.Dashboard.path) {
                            popUpTo(Route.Splash.path) { inclusive = true }
                        }
                    }
                )
            }
            composable(Route.Dashboard.path) {
                DashboardScreen(appContainer, onNavigate = { navController.navigate(it.path) })
            }
            composable(Route.Inventory.path) { InventoryScreen(appContainer) }
            composable(Route.Employees.path) { EmployeesScreen(appContainer) }
            composable(Route.Sales.path) { SalesScreen(appContainer) }
            composable(Route.Analytics.path) { AnalyticsScreen(appContainer) }
            composable(Route.Settings.path) { SettingsScreen(appContainer) }
        }
    }
}
