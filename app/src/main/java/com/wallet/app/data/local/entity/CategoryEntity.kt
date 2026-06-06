package com.wallet.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val emoji: String = "\uD83D\uDCB0",
    val color: Long = 0xFF6366F1,
    val sortOrder: Int = 0
)
