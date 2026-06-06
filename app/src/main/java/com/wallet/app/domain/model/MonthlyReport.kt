package com.wallet.app.domain.model

data class MonthlyReport(
    val month: String,
    val income: Double = 0.0,
    val expense: Double = 0.0,
    val categories: List<CategoryBreakdown> = emptyList()
)

data class CategoryBreakdown(
    val categoryId: Long,
    val categoryName: String,
    val categoryEmoji: String,
    val categoryColor: Long,
    val amount: Double,
    val percentage: Float
)
