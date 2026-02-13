package com.vyaparlite.data

import android.content.Context
import com.vyaparlite.data.local.AppDatabase
import com.vyaparlite.data.local.AppSettingEntity
import com.vyaparlite.data.local.CategoryEntity
import com.vyaparlite.data.local.EmployeeEntity
import com.vyaparlite.data.local.ExpenseEntity
import com.vyaparlite.data.local.ProductEntity
import com.vyaparlite.data.local.SaleEntity
import com.vyaparlite.data.local.SaleItemEntity
import com.vyaparlite.data.local.SalaryPaymentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale

class InventoryRepository(private val db: AppDatabase) {
    fun observeProducts(query: String, categoryId: Long?): Flow<List<ProductEntity>> =
        db.productDao().observeAll(query, categoryId)

    fun observeCategories(): Flow<List<CategoryEntity>> = db.categoryDao().observeAll()

    fun observeLowStockCount(): Flow<Int> = db.productDao().observeLowStockCount()

    suspend fun addOrUpdateProduct(item: ProductEntity) {
        if (item.id == 0L) db.productDao().insert(item) else db.productDao().update(item)
    }

    suspend fun deleteProduct(id: Long) = db.productDao().delete(id)

    suspend fun addCategory(name: String) = db.categoryDao().insert(CategoryEntity(name = name))

    suspend fun renameCategory(id: Long, name: String, isDefault: Boolean) =
        db.categoryDao().update(CategoryEntity(id = id, name = name, isDefault = isDefault))

    suspend fun deleteCategory(id: Long) = db.categoryDao().delete(id)
}

class EmployeeRepository(private val db: AppDatabase) {
    fun observeEmployees(): Flow<List<EmployeeEntity>> = db.employeeDao().observeAll()

    fun observeSalaryPayments(employeeId: Long): Flow<List<SalaryPaymentEntity>> =
        db.salaryPaymentDao().observeByEmployee(employeeId)

    suspend fun upsertEmployee(employee: EmployeeEntity) {
        if (employee.id == 0L) db.employeeDao().insert(employee) else db.employeeDao().update(employee)
    }

    suspend fun toggleAttendance(employee: EmployeeEntity) {
        db.employeeDao().update(
            employee.copy(
                isPresent = !employee.isPresent,
                attendanceUpdatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun addSalaryPayment(employeeId: Long, amount: Double, note: String) {
        db.salaryPaymentDao().insert(
            SalaryPaymentEntity(employeeId = employeeId, amount = amount, note = note)
        )
    }

    suspend fun deleteEmployee(id: Long) = db.employeeDao().delete(id)
}

data class SaleDraftItem(
    val productId: Long,
    val quantity: Int,
    val salePrice: Double
)

class SalesRepository(private val db: AppDatabase) {
    fun observeSales(): Flow<List<SaleEntity>> = db.salesDao().observeAllSales()

    fun observeExpenses(): Flow<List<ExpenseEntity>> = db.expenseDao().observeAll()

    suspend fun addExpense(title: String, amount: Double) {
        db.expenseDao().insert(ExpenseEntity(title = title, amount = amount))
    }

    suspend fun addSale(items: List<SaleDraftItem>) {
        if (items.isEmpty()) return
        val products = db.productDao().getAll().associateBy { it.id }

        var total = 0.0
        var profit = 0.0

        val saleItems = items.mapNotNull { draft ->
            val p = products[draft.productId] ?: return@mapNotNull null
            val effectiveQty = draft.quantity.coerceAtMost(p.quantity).coerceAtLeast(0)
            if (effectiveQty == 0) return@mapNotNull null
            total += effectiveQty * draft.salePrice
            profit += effectiveQty * (draft.salePrice - p.purchasePrice)
            SaleItemEntity(
                saleId = 0,
                productId = p.id,
                quantity = effectiveQty,
                salePrice = draft.salePrice,
                purchasePrice = p.purchasePrice
            )
        }

        if (saleItems.isEmpty()) return

        db.salesDao().insertSaleWithItems(
            sale = SaleEntity(totalAmount = total, totalProfit = profit),
            items = saleItems
        )

        saleItems.forEach { item ->
            val p = products[item.productId] ?: return@forEach
            db.productDao().update(
                p.copy(
                    quantity = (p.quantity - item.quantity).coerceAtLeast(0),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}

data class AnalyticsSummary(
    val todayRevenue: Double,
    val weekRevenue: Double,
    val monthRevenue: Double,
    val monthProfit: Double,
    val monthExpense: Double,
    val topProducts: List<Pair<String, Int>>,
    val stockCount: Int,
    val lowStockCount: Int
)

class AnalyticsRepository(private val db: AppDatabase) {
    fun observeSummary(): Flow<AnalyticsSummary> {
        return combine(
            db.salesDao().observeAllSales(),
            db.expenseDao().observeAll(),
            db.productDao().observeAll("", null)
        ) { sales, expenses, products ->
            val now = Instant.now().atZone(ZoneId.systemDefault())
            val weekFields = WeekFields.of(Locale.getDefault())
            val day = now.dayOfYear
            val week = now.get(weekFields.weekOfWeekBasedYear())
            val month = now.monthValue
            val year = now.year

            fun SaleEntity.matchesDay(): Boolean {
                val t = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault())
                return t.year == year && t.dayOfYear == day
            }

            fun SaleEntity.matchesWeek(): Boolean {
                val t = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault())
                return t.year == year && t.get(weekFields.weekOfWeekBasedYear()) == week
            }

            fun SaleEntity.matchesMonth(): Boolean {
                val t = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault())
                return t.year == year && t.monthValue == month
            }

            fun ExpenseEntity.matchesMonth(): Boolean {
                val t = Instant.ofEpochMilli(createdAt).atZone(ZoneId.systemDefault())
                return t.year == year && t.monthValue == month
            }

            val todayRevenue = sales.filter { it.matchesDay() }.sumOf { it.totalAmount }
            val weekRevenue = sales.filter { it.matchesWeek() }.sumOf { it.totalAmount }
            val monthSales = sales.filter { it.matchesMonth() }
            val monthRevenue = monthSales.sumOf { it.totalAmount }
            val monthProfit = monthSales.sumOf { it.totalProfit }
            val monthExpense = expenses.filter { it.matchesMonth() }.sumOf { it.amount }

            val saleItems = db.salesDao().getAllSaleItems()
            val productMap = products.associateBy { it.id }
            val topProducts = saleItems
                .groupBy { it.productId }
                .mapValues { it.value.sumOf { item -> item.quantity } }
                .toList()
                .sortedByDescending { it.second }
                .take(5)
                .mapNotNull { (id, qty) -> productMap[id]?.name?.let { name -> name to qty } }

            AnalyticsSummary(
                todayRevenue = todayRevenue,
                weekRevenue = weekRevenue,
                monthRevenue = monthRevenue,
                monthProfit = monthProfit,
                monthExpense = monthExpense,
                topProducts = topProducts,
                stockCount = products.sumOf { it.quantity },
                lowStockCount = products.count { it.quantity <= it.lowStockThreshold }
            )
        }
    }
}

@Serializable
data class BackupPayload(
    val categories: List<CategoryEntity>,
    val products: List<ProductEntity>,
    val employees: List<EmployeeEntity>,
    val salaryPayments: List<SalaryPaymentEntity>,
    val sales: List<SaleEntity>,
    val saleItems: List<SaleItemEntity>,
    val expenses: List<ExpenseEntity>,
    val settings: List<AppSettingEntity>
)

class SettingsRepository(private val db: AppDatabase, private val context: Context) {
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    fun observeSettings(): Flow<Map<String, String>> =
        db.settingsDao().observeAll().map { rows -> rows.associate { it.key to it.value } }

    suspend fun getCurrency(): String = db.settingsDao().getByKey(KEY_CURRENCY) ?: "₹"

    suspend fun setCurrency(value: String) {
        db.settingsDao().upsert(AppSettingEntity(KEY_CURRENCY, value))
    }

    suspend fun setCustomFields(fields: List<String>) {
        db.settingsDao().upsert(
            AppSettingEntity(KEY_CUSTOM_FIELDS, fields.filter { it.isNotBlank() }.distinct().joinToString(","))
        )
    }

    suspend fun backupToLocalFile(): File {
        val payload = BackupPayload(
            categories = db.categoryDao().getAll(),
            products = db.productDao().getAll(),
            employees = db.employeeDao().getAll(),
            salaryPayments = db.salaryPaymentDao().getAll(),
            sales = db.salesDao().getAllSales(),
            saleItems = db.salesDao().getAllSaleItems(),
            expenses = db.expenseDao().getAll(),
            settings = db.settingsDao().getAll()
        )
        val dir = File(context.filesDir, "backup").apply { mkdirs() }
        val file = File(dir, "vyapar_lite_backup.json")
        file.writeText(json.encodeToString(payload))
        return file
    }

    suspend fun restoreFromLocalFile(): Boolean {
        val file = File(context.filesDir, "backup/vyapar_lite_backup.json")
        if (!file.exists()) return false
        val payload = json.decodeFromString<BackupPayload>(file.readText())

        val maintenance = db.maintenanceDao()
        maintenance.clearSaleItems()
        maintenance.clearSales()
        maintenance.clearSalaryPayments()
        maintenance.clearExpenses()
        maintenance.clearEmployees()
        maintenance.clearProducts()
        maintenance.clearCategories()
        maintenance.clearSettings()

        payload.categories.forEach { db.categoryDao().insert(it) }
        payload.products.forEach { db.productDao().insert(it) }
        payload.employees.forEach { db.employeeDao().insert(it) }
        payload.salaryPayments.forEach { db.salaryPaymentDao().insert(it) }
        payload.sales.forEach { db.salesDao().insertSale(it) }
        db.salesDao().insertSaleItems(payload.saleItems)
        payload.expenses.forEach { db.expenseDao().insert(it) }
        payload.settings.forEach { db.settingsDao().upsert(it) }
        return true
    }

    suspend fun ensureSeedData() {
        if (db.categoryDao().getAll().isEmpty()) {
            listOf("Grocery", "Snacks", "Medicine", "Stationery").forEach {
                db.categoryDao().insert(CategoryEntity(name = it, isDefault = true))
            }
        }
        if (db.settingsDao().getByKey(KEY_CURRENCY) == null) {
            db.settingsDao().upsert(AppSettingEntity(KEY_CURRENCY, "₹"))
        }
    }

    companion object {
        private const val KEY_CURRENCY = "currency"
        private const val KEY_CUSTOM_FIELDS = "custom_fields"
    }
}

class AppContainer(context: Context) {
    private val db = AppDatabase.create(context)

    val inventoryRepository = InventoryRepository(db)
    val employeeRepository = EmployeeRepository(db)
    val salesRepository = SalesRepository(db)
    val analyticsRepository = AnalyticsRepository(db)
    val settingsRepository = SettingsRepository(db, context)

    suspend fun seedIfNeeded() {
        settingsRepository.ensureSeedData()
    }
}
