package com.wallet.app.domain.model

data class DashboardData(
    val totalBalance: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val recentTransactions: List<Transaction> = emptyList(),
    val walletCount: Int = 0,
    val budgetProgress: Float = 0f,
    val budgetTotal: Double = 0.0,
    val budgetSpent: Double = 0.0
)
