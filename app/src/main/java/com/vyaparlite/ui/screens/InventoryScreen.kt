package com.vyaparlite.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.vyaparlite.data.AppContainer
import com.vyaparlite.ui.components.StockIndicatorBar
import com.vyaparlite.ui.theme.AppSpacing
import com.vyaparlite.utils.formatMoney

@Composable
fun InventoryScreen(appContainer: AppContainer) {
    var search by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("₹") }
    val products by appContainer.inventoryRepository.observeProducts(search, null)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    LaunchedEffect(Unit) {
        currency = appContainer.settingsRepository.getCurrency()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Inventory2, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.md),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
        ) {
            Text("Inventory", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search products") },
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                items(products) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                                Icon(Icons.Default.Inventory2, contentDescription = item.name)
                                Column {
                                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                                    Text("Price: ${formatMoney(currency, item.sellingPrice)}")
                                }
                            }
                            Text("Stock: ${item.quantity}")
                            StockIndicatorBar(current = item.quantity, threshold = item.lowStockThreshold)
                        }
                    }
                }
            }
        }
    }
}
