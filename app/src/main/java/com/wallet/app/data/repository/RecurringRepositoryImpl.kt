package com.wallet.app.data.repository

import com.wallet.app.data.local.dao.CategoryDao
import com.wallet.app.data.local.dao.RecurringDao
import com.wallet.app.data.local.entity.RecurringEntity
import com.wallet.app.domain.model.RecurringTransaction
import com.wallet.app.domain.model.Transaction
import com.wallet.app.domain.repository.RecurringRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecurringRepositoryImpl @Inject constructor(
    private val recurringDao: RecurringDao,
    private val categoryDao: CategoryDao
) : RecurringRepository {

    private fun RecurringEntity.toDomain(): RecurringTransaction {
        val category = categoryDao.getCategoryById(categoryId)
        return RecurringTransaction(
            id = id,
            type = if (type == "INCOME") Transaction.TransactionType.INCOME else Transaction.TransactionType.EXPENSE,
            amount = amount,
            categoryId = categoryId,
            walletId = walletId,
            note = note,
            interval = when (interval) {
                "DAILY" -> RecurringTransaction.RecurringInterval.DAILY
                "WEEKLY" -> RecurringTransaction.RecurringInterval.WEEKLY
                "YEARLY" -> RecurringTransaction.RecurringInterval.YEARLY
                else -> RecurringTransaction.RecurringInterval.MONTHLY
            },
            nextDate = Date(nextDate),
            isActive = isActive,
            categoryName = category?.name ?: "Unknown",
            categoryEmoji = category?.emoji ?: "\uD83D\uDCB0"
        )
    }

    private fun RecurringTransaction.toEntity() = RecurringEntity(
        id = id,
        type = if (type == Transaction.TransactionType.INCOME) "INCOME" else "EXPENSE",
        amount = amount,
        categoryId = categoryId,
        walletId = walletId,
        note = note,
        interval = when (interval) {
            RecurringTransaction.RecurringInterval.DAILY -> "DAILY"
            RecurringTransaction.RecurringInterval.WEEKLY -> "WEEKLY"
            RecurringTransaction.RecurringInterval.YEARLY -> "YEARLY"
            else -> "MONTHLY"
        },
        nextDate = nextDate.time,
        isActive = isActive
    )

    override fun getAllRecurring(): Flow<List<RecurringTransaction>> =
        recurringDao.getAllRecurring().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun saveRecurring(recurring: RecurringTransaction): Long =
        recurringDao.insert(recurring.toEntity())

    override suspend fun updateRecurring(recurring: RecurringTransaction) =
        recurringDao.update(recurring.toEntity())

    override suspend fun deleteRecurring(recurring: RecurringTransaction) =
        recurringDao.delete(recurring.toEntity())
}
