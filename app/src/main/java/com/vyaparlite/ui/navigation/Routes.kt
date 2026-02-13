package com.vyaparlite.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(val path: String, val label: String, val icon: ImageVector?) {
    data object Splash : Route("splash", "Splash", null)
    data object Dashboard : Route("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Inventory : Route("inventory", "Inventory", Icons.Default.Inventory2)
    data object Employees : Route("employees", "Employees", Icons.Default.People)
    data object Sales : Route("sales", "Sales", Icons.Default.PointOfSale)
    data object Analytics : Route("analytics", "Analytics", Icons.Default.Analytics)
    data object Settings : Route("settings", "Settings", Icons.Default.Settings)
}

val BottomNavRoutes = listOf(
    Route.Dashboard,
    Route.Inventory,
    Route.Sales,
    Route.Employees,
    Route.Analytics,
    Route.Settings
)
