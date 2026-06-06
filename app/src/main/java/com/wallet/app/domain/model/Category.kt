package com.wallet.app.domain.model

import androidx.compose.ui.graphics.Color

data class Category(
    val id: Long = 0,
    val name: String,
    val emoji: String = "\uD83D\uDCB0",
    val color: Color = Color(0xFF6366F1),
    val sortOrder: Int = 0
)
