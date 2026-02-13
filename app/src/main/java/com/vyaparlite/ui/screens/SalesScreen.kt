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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vyaparlite.data.AppContainer
import com.vyaparlite.data.SaleDraftItem
import com.vyaparlite.ui.theme.AppSpacing
import com.vyaparlite.utils.formatMoney
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SalesScreen(appContainer: AppContainer) {
    val scope = rememberCoroutineScope()
    val products by appContainer.inventoryRepository.observeProducts("", null)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val sales by appContainer.salesRepository.observeSales().collectAsStateWithLifecycle(initialValue = emptyList())

    var selectedProductId by remember { mutableStateOf<Long?>(null) }
    var qty by remember { mutableIntStateOf(1) }
    var salePrice by remember { mutableStateOf("0") }
    var currency by remember { mutableStateOf("₹") }

    val selected = products.firstOrNull { it.id == selectedProductId } ?: products.firstOrNull()
    val profit = ((salePrice.toDoubleOrNull() ?: selected?.sellingPrice ?: 0.0) - (selected?.purchasePrice ?: 0.0)) * qty

    LaunchedEffect(products) {
        if (selectedProductId == null && products.isNotEmpty()) {
            selectedProductId = products.first().id
            salePrice = products.first().sellingPrice.toString()
        }
    }
    LaunchedEffect(Unit) { currency = appContainer.settingsRepository.getCurrency() }

    Column(
        modifier = Modifier.fillMaxSize().padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text("Sales Entry", style = MaterialTheme.typography.headlineSmall)
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(AppSpacing.md), verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)) {
                Text("Minimal Form", style = MaterialTheme.typography.titleMedium)
                LazyColumn(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    items(products.take(8)) { p ->
                        TextButton(onClick = {
                            selectedProductId = p.id
                            salePrice = p.sellingPrice.toString()
                        }) { Text(p.name) }
                    }
                }
                OutlinedTextField(value = salePrice, onValueChange = { salePrice = it }, label = { Text("Sale Price") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { qty = (qty - 1).coerceAtLeast(1) }) { androidx.compose.material3.Icon(Icons.Default.Remove, null) }
                    Text("Qty: $qty", modifier = Modifier.padding(top = 12.dp))
                    Button(onClick = { qty += 1 }) { androidx.compose.material3.Icon(Icons.Default.Add, null) }
                }
                Text("Auto Profit: ${formatMoney(currency, profit)}")
                Button(onClick = {
                    selected?.let {
                        scope.launch(Dispatchers.IO) {
                            appContainer.salesRepository.addSale(
                                listOf(SaleDraftItem(it.id, qty, salePrice.toDoubleOrNull() ?: it.sellingPrice))
                            )
                        }
                    }
                }, modifier = Modifier.fillMaxWidth()) { Text("Record Sale") }
            }
        }

        Text("Recent Sales", style = MaterialTheme.typography.titleMedium)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sales.take(10)) { sale ->
                Card {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(formatMoney(currency, sale.totalAmount))
                        Text("Profit ${formatMoney(currency, sale.totalProfit)}")
                    }
                }
            }
        }
    }
}
