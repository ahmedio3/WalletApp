package com.wallet.app.domain.repository

import com.wallet.app.domain.model.RecurringTransaction
import kotlinx.coroutines.flow.Flow

interface RecurringRepository {
    fun getAllRecurring(): Flow<List<RecurringTransaction>>
    suspend fun saveRecurring(recurring: RecurringTransaction): Long
    suspend fun updateRecurring(recurring: RecurringTransaction)
    suspend fun deleteRecurring(recurring: RecurringTransaction)
}
