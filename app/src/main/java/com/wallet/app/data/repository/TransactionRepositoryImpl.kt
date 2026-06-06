package com.wallet.app.data.repository

import com.wallet.app.data.local.dao.CategoryDao
import com.wallet.app.data.local.dao.TransactionDao
import com.wallet.app.data.local.dao.WalletDao
import com.wallet.app.data.local.entity.TransactionEntity
import com.wallet.app.domain.model.Transaction
import com.wallet.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val walletDao: WalletDao
) : TransactionRepository {

    private suspend fun TransactionEntity.toDomain(): Transaction {
        val category = categoryDao.getCategoryById(categoryId)
        val wallet = walletDao.getWalletById(walletId)
        return Transaction(
            id = id,
            type = if (type == "INCOME") Transaction.TransactionType.INCOME else Transaction.TransactionType.EXPENSE,
            amount = amount,
            categoryId = categoryId,
            walletId = walletId,
            note = note,
            date = Date(date),
            categoryName = category?.name ?: "Unknown",
            categoryEmoji = category?.emoji ?: "\uD83D\uDCB0",
            categoryColor = category?.color ?: 0xFF6366F1,
            walletName = wallet?.name ?: "Unknown"
        )
    }

    private fun Transaction.toEntity() = TransactionEntity(
        id = id,
        type = if (type == Transaction.TransactionType.INCOME) "INCOME" else "EXPENSE",
        amount = amount,
        categoryId = categoryId,
        walletId = walletId,
        note = note,
        date = date.time,
        createdAt = System.currentTimeMillis()
    )

    private fun Flow<List<TransactionEntity>>.mapToDomain(): Flow<List<Transaction>> =
        this.map { entities ->
            val mapped = entities.map { entity ->
                Transaction(
                    id = entity.id,
                    type = if (entity.type == "INCOME") Transaction.TransactionType.INCOME else Transaction.TransactionType.EXPENSE,
                    amount = entity.amount,
                    categoryId = entity.categoryId,
                    walletId = entity.walletId,
                    note = entity.note,
                    date = Date(entity.date),
                    categoryName = categoryDao.getCategoryById(entity.categoryId)?.name ?: "Unknown",
                    categoryEmoji = categoryDao.getCategoryById(entity.categoryId)?.emoji ?: "\uD83D\uDCB0",
                    categoryColor = categoryDao.getCategoryById(entity.categoryId)?.color ?: 0xFF6366F1,
                    walletName = walletDao.getWalletById(entity.walletId)?.name ?: "Unknown"
                )
            }
            mapped
        }

    override fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().mapToDomain()

    override fun getTransactionsByWallet(walletId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByWallet(walletId).mapToDomain()

    override fun getTransactionsByType(type: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type).mapToDomain()

    override fun getTransactionsByCategory(categoryId: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByCategory(categoryId).mapToDomain()

    override fun getTransactionsBetweenDates(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsBetweenDates(startDate, endDate).mapToDomain()

    override fun searchTransactions(query: String): Flow<List<Transaction>> =
        transactionDao.searchTransactions(query).mapToDomain()

    override fun getIncomeBetweenDates(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getIncomeBetweenDates(startDate, endDate).mapToDomain()

    override fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<Transaction>> =
        transactionDao.getExpensesBetweenDates(startDate, endDate).mapToDomain()

    override fun getTotalIncome(startDate: Long, endDate: Long): Flow<Double?> =
        transactionDao.getTotalIncome(startDate, endDate)

    override fun getTotalExpense(startDate: Long, endDate: Long): Flow<Double?> =
        transactionDao.getTotalExpense(startDate, endDate)

    override suspend fun getTransactionById(id: Long): Transaction? =
        transactionDao.getTransactionById(id)?.toDomain()

    override suspend fun saveTransaction(transaction: Transaction): Long =
        transactionDao.insert(transaction.toEntity())

    override suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.update(transaction.toEntity())

    override suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.delete(transaction.toEntity())
}
