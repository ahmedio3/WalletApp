package com.wallet.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String = "EXPENSE", // INCOME or EXPENSE
    val amount: Double = 0.0,
    val categoryId: Long = 0,
    val walletId: Long = 0,
    val note: String = "",
    val date: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
