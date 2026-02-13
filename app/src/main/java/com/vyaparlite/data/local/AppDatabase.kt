package com.vyaparlite.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        EmployeeEntity::class,
        SalaryPaymentEntity::class,
        SaleEntity::class,
        SaleItemEntity::class,
        ExpenseEntity::class,
        AppSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun salaryPaymentDao(): SalaryPaymentDao
    abstract fun salesDao(): SalesDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun settingsDao(): SettingsDao
    abstract fun maintenanceDao(): MaintenanceDao

    companion object {
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "vyapar_lite.db"
            ).fallbackToDestructiveMigration().build()
        }
    }
}
