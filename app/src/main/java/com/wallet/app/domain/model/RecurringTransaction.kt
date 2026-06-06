package com.wallet.app.domain.model

import java.util.Date

data class RecurringTransaction(
    val id: Long = 0,
    val type: Transaction.TransactionType = Transaction.TransactionType.EXPENSE,
    val amount: Double,
    val categoryId: Long,
    val walletId: Long,
    val note: String = "",
    val interval: RecurringInterval = RecurringInterval.MONTHLY,
    val nextDate: Date = Date(),
    val isActive: Boolean = true,
    val categoryName: String = "",
    val categoryEmoji: String = "\uD83D\uDCB0",
    val categoryColor: Long = 0xFF6366F1
) {
    enum class RecurringInterval { DAILY, WEEKLY, MONTHLY, YEARLY }
}
