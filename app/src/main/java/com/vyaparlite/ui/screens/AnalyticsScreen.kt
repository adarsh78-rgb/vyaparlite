package com.vyaparlite.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.vyaparlite.data.AnalyticsSummary
import com.vyaparlite.data.AppContainer
import com.vyaparlite.ui.components.MetricCard
import com.vyaparlite.ui.components.MiniBarChart
import com.vyaparlite.ui.theme.AppSpacing
import com.vyaparlite.utils.formatMoney

@Composable
fun AnalyticsScreen(appContainer: AppContainer) {
    val summary by appContainer.analyticsRepository.observeSummary().collectAsStateWithLifecycle(
        initialValue = AnalyticsSummary(0.0, 0.0, 0.0, 0.0, 0.0, emptyList(), 0, 0)
    )
    var currency by remember { mutableStateOf("₹") }

    LaunchedEffect(Unit) { currency = appContainer.settingsRepository.getCurrency() }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        item { Text("Analytics", style = MaterialTheme.typography.headlineSmall) }
        item { MetricCard("Revenue", "Daily ${formatMoney(currency, summary.todayRevenue)}") }
        item { MiniBarChart(listOf(summary.todayRevenue.toFloat(), summary.weekRevenue.toFloat(), summary.monthRevenue.toFloat()), listOf("Day", "Week", "Month")) }
        item { MetricCard("Expense Pie (summary)", formatMoney(currency, summary.monthExpense), "Full pie chart in v2 chart module") }
        item {
            Text("Top Products", style = MaterialTheme.typography.titleMedium)
        }
        items(summary.topProducts) { (name, qty) ->
            Text("$name: $qty", modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp))
        }
        item { BannerAd(modifier = Modifier.fillMaxWidth()) }
    }
}
