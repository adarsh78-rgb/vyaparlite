package com.vyaparlite.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.vyaparlite.utils.formatMoney
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun EmployeesScreen(appContainer: AppContainer) {
    val scope = rememberCoroutineScope()
    val employees by appContainer.employeeRepository.observeEmployees().collectAsStateWithLifecycle(initialValue = emptyList())
    var currency by remember { mutableStateOf("₹") }

    LaunchedEffect(Unit) { currency = appContainer.settingsRepository.getCurrency() }

    Column(
        modifier = Modifier.fillMaxSize().padding(AppSpacing.md),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
    ) {
        Text("Employees", style = MaterialTheme.typography.headlineSmall)
        Text("Salary + attendance", style = MaterialTheme.typography.bodyMedium)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            items(employees) { emp ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(emp.name, style = MaterialTheme.typography.titleMedium)
                        Text(emp.role)
                        Text("Salary: ${formatMoney(currency, emp.salary)}")
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(if (emp.isPresent) "Present" else "Absent")
                            Switch(
                                checked = emp.isPresent,
                                onCheckedChange = { scope.launch(Dispatchers.IO) { appContainer.employeeRepository.toggleAttendance(emp) } }
                            )
                        }
                        TextButton(onClick = {
                            scope.launch(Dispatchers.IO) {
                                appContainer.employeeRepository.addSalaryPayment(emp.id, emp.salary, "Monthly payout")
                            }
                        }) { Text("Pay Salary") }
                    }
                }
            }
        }
    }
}
