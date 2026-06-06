package com.wallet.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallets")
data class WalletEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val emoji: String = "\uD83D\uDCB5",
    val balance: Double = 0.0,
    val isPrimary: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
