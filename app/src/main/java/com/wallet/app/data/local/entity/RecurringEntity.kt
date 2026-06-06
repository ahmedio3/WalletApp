package com.wallet.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recurring_transactions")
data class RecurringEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String = "EXPENSE",
    val amount: Double,
    val categoryId: Long,
    val walletId: Long,
    val note: String = "",
    val interval: String = "MONTHLY", // DAILY, WEEKLY, MONTHLY, YEARLY
    val nextDate: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
