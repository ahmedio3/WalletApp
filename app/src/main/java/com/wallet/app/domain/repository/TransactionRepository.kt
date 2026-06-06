package com.wallet.app.domain.repository

import com.wallet.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByWallet(walletId: Long): Flow<List<Transaction>>
    fun getTransactionsByType(type: String): Flow<List<Transaction>>
    fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>>
    fun getTransactionsBetweenDates(startDate: Long, endDate: Long): Flow<List<Transaction>>
    fun searchTransactions(query: String): Flow<List<Transaction>>
    fun getIncomeBetweenDates(startDate: Long, endDate: Long): Flow<List<Transaction>>
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Transaction>>
    fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double?>
    fun getTotalExpense(startDate: Long, endDate: Long): Flow<Double?>
    suspend fun getTransactionById(id: Long): Transaction?
    suspend fun saveTransaction(transaction: Transaction): Long
    suspend fun updateTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
}
