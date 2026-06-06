package com.wallet.app.data.local.dao

import androidx.room.*
import com.wallet.app.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallets ORDER BY isPrimary DESC, createdAt ASC")
    fun getAllWallets(): Flow<List<WalletEntity>>

    @Query("SELECT * FROM wallets WHERE isPrimary = 1 LIMIT 1")
    fun getPrimaryWallet(): Flow<WalletEntity?>

    @Query("SELECT * FROM wallets WHERE id = :id")
    suspend fun getWalletById(id: Long): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallet: WalletEntity): Long

    @Update
    suspend fun update(wallet: WalletEntity)

    @Delete
    suspend fun delete(wallet: WalletEntity)

    @Query("UPDATE wallets SET isPrimary = 0")
    suspend fun clearPrimaryFlag()

    @Query("UPDATE wallets SET balance = balance + :amount WHERE id = :walletId")
    suspend fun updateBalance(walletId: Long, amount: Double)
}
