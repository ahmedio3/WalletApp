package com.wallet.app.domain.repository

import com.wallet.app.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllActiveBudgets(): Flow<List<Budget>>
    suspend fun getBudgetByCategory(categoryId: Long): Budget?
    suspend fun saveBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
    suspend fun updateSpent(id: Long, spent: Double)
}
