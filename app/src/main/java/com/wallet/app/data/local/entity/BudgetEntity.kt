package com.wallet.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val spent: Double = 0.0,
    val period: String = "MONTHLY", // MONTHLY, WEEKLY, YEARLY
    val startDate: Long = System.currentTimeMillis(),
    val endDate: Long? = null,
    val isActive: Boolean = true
)
