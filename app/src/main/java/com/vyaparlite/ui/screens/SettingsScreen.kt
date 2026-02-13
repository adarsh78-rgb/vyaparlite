package com.vyaparlite.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vyaparlite.data.AppContainer
import com.vyaparlite.ui.theme.AppSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(appContainer: AppContainer) {
    val scope = rememberCoroutineScope()
    val settings by appContainer.settingsRepository.observeSettings().collectAsStateWithLifecycle(initialValue = emptyMap())
    var currency by remember(settings["currency"]) { mutableStateOf(settings["currency"] ?: "₹") }
    var adsPreview by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        item { Text("Settings", style = MaterialTheme.typography.headlineSmall) }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Currency")
                    OutlinedTextField(value = currency, onValueChange = { currency = it }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = { scope.launch(Dispatchers.IO) { appContainer.settingsRepository.setCurrency(currency.ifBlank { "₹" }) } }) {
                        Text("Save Currency")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Backup")
                    Button(onClick = { scope.launch(Dispatchers.IO) { appContainer.settingsRepository.backupToLocalFile() } }, modifier = Modifier.fillMaxWidth()) { Text("Backup Local") }
                    Button(onClick = { scope.launch(Dispatchers.IO) { appContainer.settingsRepository.restoreFromLocalFile() } }, modifier = Modifier.fillMaxWidth()) { Text("Restore Local") }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Categories")
                    Text("Manage product categories from Inventory module")
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ads Toggle Preview")
                    Switch(checked = adsPreview, onCheckedChange = { adsPreview = it })
                    Text(if (adsPreview) "Ad preview enabled" else "Ad preview hidden")
                }
            }
        }
    }
}
