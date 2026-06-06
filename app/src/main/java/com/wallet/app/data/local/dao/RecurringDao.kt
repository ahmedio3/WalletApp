package com.wallet.app.data.local.dao

import androidx.room.*
import com.wallet.app.data.local.entity.RecurringEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringDao {
    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1 ORDER BY nextDate ASC")
    fun getAllActiveRecurring(): Flow<List<RecurringEntity>>

    @Query("SELECT * FROM recurring_transactions ORDER BY nextDate ASC")
    fun getAllRecurring(): Flow<List<RecurringEntity>>

    @Query("SELECT * FROM recurring_transactions WHERE id = :id")
    suspend fun getRecurringById(id: Long): RecurringEntity?

    @Query("SELECT * FROM recurring_transactions WHERE isActive = 1 AND nextDate <= :currentDate")
    suspend fun getDueRecurring(currentDate: Long): List<RecurringEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recurring: RecurringEntity): Long

    @Update
    suspend fun update(recurring: RecurringEntity)

    @Delete
    suspend fun delete(recurring: RecurringEntity)
}
