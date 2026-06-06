package com.wallet.app.data.local.dao

import androidx.room.*
import com.wallet.app.data.local.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE isActive = 1 ORDER BY startDate DESC")
    fun getAllActiveBudgets(): Flow<List<BudgetEntity>>

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND isActive = 1 LIMIT 1")
    suspend fun getBudgetByCategory(categoryId: Long): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE id = :id")
    suspend fun getBudgetById(id: Long): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: BudgetEntity): Long

    @Update
    suspend fun update(budget: BudgetEntity)

    @Delete
    suspend fun delete(budget: BudgetEntity)

    @Query("UPDATE budgets SET spent = :spent WHERE id = :id")
    suspend fun updateSpent(id: Long, spent: Double)

    @Query("SELECT * FROM budgets WHERE isActive = 1")
    suspend fun getAllActiveBudgetsOnce(): List<BudgetEntity>
}
