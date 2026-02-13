package com.vyaparlite.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vyaparlite.ads.BannerAd
import com.vyaparlite.ads.NativeAdPlaceholder
import com.vyaparlite.data.AnalyticsSummary
import com.vyaparlite.data.AppContainer
import com.vyaparlite.ui.components.MetricCard
import com.vyaparlite.ui.components.QuickActionTile
import com.vyaparlite.ui.navigation.Route
import com.vyaparlite.ui.theme.AppSpacing
import com.vyaparlite.utils.formatMoney

@Composable
fun DashboardScreen(appContainer: AppContainer, onNavigate: (Route) -> Unit) {
    val summary by appContainer.analyticsRepository.observeSummary().collectAsStateWithLifecycle(
        initialValue = AnalyticsSummary(0.0, 0.0, 0.0, 0.0, 0.0, emptyList(), 0, 0)
    )
    var currency by remember { mutableStateOf("₹") }
    var reveal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        currency = appContainer.settingsRepository.getCurrency()
        reveal = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text("Namaste, Owner", style = MaterialTheme.typography.headlineMedium)
        Text("Aaj ka business snapshot", style = MaterialTheme.typography.bodyMedium)

        AnimatedVisibility(reveal) {
            Column(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                MetricCard("Today's Sales", formatMoney(currency, summary.todayRevenue))
                Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        MetricCard("Profit", formatMoney(currency, summary.monthProfit))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        MetricCard("Expenses", formatMoney(currency, summary.monthExpense))
                    }
                }
                MetricCard("Stock Alerts", "${summary.lowStockCount} low-stock products")
            }
        }

        NativeAdPlaceholder()

        Text("Quick Actions", style = MaterialTheme.typography.titleLarge)
        val actions = listOf(
            Triple("Add Product", Icons.Default.AddShoppingCart, Route.Inventory),
            Triple("Record Sale", Icons.Default.PointOfSale, Route.Sales),
            Triple("Add Expense", Icons.Default.Payments, Route.Sales),
            Triple("Employee", Icons.Default.People, Route.Employees)
        )
        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(actions) { (label, icon, route) ->
                QuickActionTile(label = label, icon = icon, onClick = { onNavigate(route) })
            }
        }

        BannerAd(modifier = Modifier.fillMaxWidth())
    }
}
