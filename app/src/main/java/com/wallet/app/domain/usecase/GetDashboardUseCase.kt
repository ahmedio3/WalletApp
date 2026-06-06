package com.wallet.app.domain.usecase

import com.wallet.app.domain.model.DashboardData
import com.wallet.app.domain.model.Transaction
import com.wallet.app.domain.repository.*
import com.wallet.app.util.DateUtils
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class GetDashboardUseCase @Inject constructor(
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(): Flow<DashboardData> = combine(
        walletRepository.getAllWallets(),
        transactionRepository.getTransactionsBetweenDates(
            DateUtils.startOfMonth().time,
            DateUtils.endOfMonth().time
        ),
        budgetRepository.getAllActiveBudgets()
    ) { wallets, monthlyTransactions, budgets ->
        val totalBalance = wallets.sumOf { it.balance }
        val monthlyIncome = monthlyTransactions
            .filter { it.type == Transaction.TransactionType.INCOME }
            .sumOf { it.amount }
        val monthlyExpense = monthlyTransactions
            .filter { it.type == Transaction.TransactionType.EXPENSE }
            .sumOf { it.amount }
        val recentTransactions = monthlyTransactions
            .sortedByDescending { it.date.time }
            .take(5)

        val totalBudget = budgets.sumOf { it.amount }
        val totalSpent = budgets.sumOf { it.spent }
        val budgetProgress = if (totalBudget > 0) (totalSpent / totalBudget).toFloat() else 0f

        DashboardData(
            totalBalance = totalBalance,
            monthlyIncome = monthlyIncome,
            monthlyExpense = monthlyExpense,
            recentTransactions = recentTransactions,
            walletCount = wallets.size,
            budgetProgress = budgetProgress.coerceIn(0f, 1f),
            budgetTotal = totalBudget,
            budgetSpent = totalSpent
        )
    }
}
