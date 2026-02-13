package com.vyaparlite.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories ORDER BY name")
    suspend fun getAll(): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CategoryEntity): Long

    @Update
    suspend fun update(item: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface ProductDao {
    @Query(
        """
        SELECT * FROM products
        WHERE (:query = '' OR name LIKE '%' || :query || '%')
          AND (:categoryId IS NULL OR categoryId = :categoryId)
        ORDER BY name
        """
    )
    fun observeAll(query: String, categoryId: Long?): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products ORDER BY name")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): ProductEntity?

    @Query("SELECT COUNT(*) FROM products WHERE quantity <= lowStockThreshold")
    fun observeLowStockCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ProductEntity): Long

    @Update
    suspend fun update(item: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface EmployeeDao {
    @Query("SELECT * FROM employees ORDER BY name")
    fun observeAll(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees ORDER BY name")
    suspend fun getAll(): List<EmployeeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: EmployeeEntity): Long

    @Update
    suspend fun update(item: EmployeeEntity)

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface SalaryPaymentDao {
    @Query("SELECT * FROM salary_payments WHERE employeeId = :employeeId ORDER BY paidAt DESC")
    fun observeByEmployee(employeeId: Long): Flow<List<SalaryPaymentEntity>>

    @Query("SELECT * FROM salary_payments")
    suspend fun getAll(): List<SalaryPaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SalaryPaymentEntity)
}

@Dao
interface SalesDao {
    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    fun observeAllSales(): Flow<List<SaleEntity>>

    @Query("SELECT * FROM sales ORDER BY createdAt DESC")
    suspend fun getAllSales(): List<SaleEntity>

    @Query("SELECT * FROM sale_items ORDER BY id DESC")
    suspend fun getAllSaleItems(): List<SaleItemEntity>

    @Insert
    suspend fun insertSale(item: SaleEntity): Long

    @Insert
    suspend fun insertSaleItems(items: List<SaleItemEntity>)

    @Transaction
    suspend fun insertSaleWithItems(sale: SaleEntity, items: List<SaleItemEntity>) {
        val saleId = insertSale(sale)
        insertSaleItems(items.map { it.copy(saleId = saleId) })
    }
}

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    suspend fun getAll(): List<ExpenseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun delete(id: Long)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings")
    fun observeAll(): Flow<List<AppSettingEntity>>

    @Query("SELECT * FROM app_settings")
    suspend fun getAll(): List<AppSettingEntity>

    @Query("SELECT value FROM app_settings WHERE `key` = :key")
    suspend fun getByKey(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: AppSettingEntity)
}

@Dao
interface MaintenanceDao {
    @Query("DELETE FROM sale_items")
    suspend fun clearSaleItems()

    @Query("DELETE FROM sales")
    suspend fun clearSales()

    @Query("DELETE FROM salary_payments")
    suspend fun clearSalaryPayments()

    @Query("DELETE FROM expenses")
    suspend fun clearExpenses()

    @Query("DELETE FROM employees")
    suspend fun clearEmployees()

    @Query("DELETE FROM products")
    suspend fun clearProducts()

    @Query("DELETE FROM categories")
    suspend fun clearCategories()

    @Query("DELETE FROM app_settings")
    suspend fun clearSettings()
}
