package com.wallet.app.domain.model

data class Wallet(
    val id: Long = 0,
    val name: String,
    val emoji: String = "\uD83D\uDCB5",
    val balance: Double = 0.0,
    val isPrimary: Boolean = false
)
