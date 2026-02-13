package com.vyaparlite.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "categories")
@Serializable
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isDefault: Boolean = false
)

@Entity(
    tableName = "products",
    indices = [Index("name"), Index("categoryId")],
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
@Serializable
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryId: Long?,
    val quantity: Int,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val lowStockThreshold: Int = 5,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "employees", indices = [Index("name")])
@Serializable
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String,
    val salary: Double,
    val contactNumber: String,
    val isPresent: Boolean = false,
    val attendanceUpdatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "salary_payments",
    indices = [Index("employeeId")],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Serializable
data class SalaryPaymentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val employeeId: Long,
    val amount: Double,
    val paidAt: Long = System.currentTimeMillis(),
    val note: String = ""
)

@Entity(tableName = "sales")
@Serializable
data class SaleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val totalAmount: Double,
    val totalProfit: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "sale_items",
    indices = [Index("saleId"), Index("productId")],
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["saleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
@Serializable
data class SaleItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val saleId: Long,
    val productId: Long,
    val quantity: Int,
    val salePrice: Double,
    val purchasePrice: Double
)

@Entity(tableName = "expenses", indices = [Index("createdAt")])
@Serializable
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
@Serializable
data class AppSettingEntity(
    @PrimaryKey val key: String,
    val value: String
)
