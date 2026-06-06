package com.wallet.app.domain.repository

import com.wallet.app.domain.model.Wallet
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getAllWallets(): Flow<List<Wallet>>
    fun getPrimaryWallet(): Flow<Wallet?>
    suspend fun getWalletById(id: Long): Wallet?
    suspend fun saveWallet(wallet: Wallet): Long
    suspend fun updateWallet(wallet: Wallet)
    suspend fun deleteWallet(wallet: Wallet)
    suspend fun setPrimaryWallet(id: Long)
    suspend fun updateBalance(walletId: Long, amount: Double)
}
