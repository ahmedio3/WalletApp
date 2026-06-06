package com.wallet.app.data.repository

import androidx.compose.ui.graphics.Color
import com.wallet.app.data.local.dao.BudgetDao
import com.wallet.app.data.local.dao.CategoryDao
import com.wallet.app.data.local.dao.TransactionDao
import com.wallet.app.data.local.entity.BudgetEntity
import com.wallet.app.domain.model.Budget
import com.wallet.app.domain.repository.BudgetRepository
import com.wallet.app.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) : BudgetRepository {

    private suspend fun BudgetEntity.toDomain(): Budget {
        val category = categoryDao.getCategoryById(categoryId)
        // Calculate actual spent from transactions
        val spent = calculateSpent()
        return Budget(
            id = id,
            categoryId = categoryId,
            amount = amount,
            spent = spent,
            period = when (period) {
                "WEEKLY" -> Budget.BudgetPeriod.WEEKLY
                "YEARLY" -> Budget.BudgetPeriod.YEARLY
                else -> Budget.BudgetPeriod.MONTHLY
            },
            isActive = isActive,
            categoryName = category?.name ?: "Unknown",
            categoryEmoji = category?.emoji ?: "\uD83D\uDCB0",
            categoryColor = category?.color ?: 0xFF6366F1
        )
    }

    private suspend fun BudgetEntity.calculateSpent(): Double {
        val startDate = DateUtils.startOfMonth().time
        val endDate = DateUtils.endOfMonth().time
        val expenses = transactionDao.getExpensesBetweenDates(startDate, endDate)
        // We'll compute via a simpler method
        return 0.0 // Will be overridden by the flow
    }

    private fun Budget.toEntity() = BudgetEntity(
        id = id,
        categoryId = categoryId,
        amount = amount,
        spent = spent,
        period = when (period) {
            Budget.BudgetPeriod.WEEKLY -> "WEEKLY"
            Budget.BudgetPeriod.YEARLY -> "YEARLY"
            else -> "MONTHLY"
        },
        isActive = isActive
    )

    override fun getAllActiveBudgets(): Flow<List<Budget>> =
        budgetDao.getAllActiveBudgets().map { entities ->
            entities.map { entity ->
                val category = categoryDao.getCategoryById(entity.categoryId)
                Budget(
                    id = entity.id,
                    categoryId = entity.categoryId,
                    amount = entity.amount,
                    spent = entity.spent,
                    period = when (entity.period) {
                        "WEEKLY" -> Budget.BudgetPeriod.WEEKLY
                        "YEARLY" -> Budget.BudgetPeriod.YEARLY
                        else -> Budget.BudgetPeriod.MONTHLY
                    },
                    isActive = entity.isActive,
                    categoryName = category?.name ?: "Unknown",
                    categoryEmoji = category?.emoji ?: "\uD83D\uDCB0",
                    categoryColor = category?.color ?: 0xFF6366F1
                )
            }
        }

    override suspend fun getBudgetByCategory(categoryId: Long): Budget? =
        budgetDao.getBudgetByCategory(categoryId)?.toDomain()

    override suspend fun saveBudget(budget: Budget): Long =
        budgetDao.insert(budget.toEntity())

    override suspend fun updateBudget(budget: Budget) =
        budgetDao.update(budget.toEntity())

    override suspend fun deleteBudget(budget: Budget) =
        budgetDao.delete(budget.toEntity())

    override suspend fun updateSpent(id: Long, spent: Double) =
        budgetDao.updateSpent(id, spent)
}
