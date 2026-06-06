package com.wallet.app.domain.model

import java.util.Date

data class Transaction(
    val id: Long = 0,
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: Double = 0.0,
    val categoryId: Long = 0,
    val walletId: Long = 0,
    val note: String = "",
    val date: Date = Date(),
    val categoryName: String = "",
    val categoryEmoji: String = "\uD83D\uDCB0",
    val categoryColor: Long = 0xFF6366F1,
    val walletName: String = ""
) {
    enum class TransactionType { INCOME, EXPENSE }
}
