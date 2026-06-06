package com.wallet.app.domain.model

data class Budget(
    val id: Long = 0,
    val categoryId: Long,
    val amount: Double,
    val spent: Double = 0.0,
    val period: BudgetPeriod = BudgetPeriod.MONTHLY,
    val isActive: Boolean = true,
    val categoryName: String = "",
    val categoryEmoji: String = "\uD83D\uDCB0",
    val categoryColor: Long = 0xFF6366F1
) {
    enum class BudgetPeriod { MONTHLY, WEEKLY, YEARLY }

    val remaining: Double get() = amount - spent
    val progress: Float get() = if (amount > 0) (spent / amount).toFloat().coerceIn(0f, 1f) else 0f
    val isOverspent: Boolean get() = spent > amount
}
