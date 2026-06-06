package com.wallet.app.data.repository

import com.wallet.app.data.local.dao.WalletDao
import com.wallet.app.data.local.entity.WalletEntity
import com.wallet.app.domain.model.Wallet
import com.wallet.app.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val walletDao: WalletDao
) : WalletRepository {

    private fun WalletEntity.toDomain() = Wallet(
        id = id,
        name = name,
        emoji = emoji,
        balance = balance,
        isPrimary = isPrimary
    )

    private fun Wallet.toEntity() = WalletEntity(
        id = id,
        name = name,
        emoji = emoji,
        balance = balance,
        isPrimary = isPrimary
    )

    override fun getAllWallets(): Flow<List<Wallet>> =
        walletDao.getAllWallets().map { list -> list.map { it.toDomain() } }

    override fun getPrimaryWallet(): Flow<Wallet?> =
        walletDao.getPrimaryWallet().map { it?.toDomain() }

    override suspend fun getWalletById(id: Long): Wallet? =
        walletDao.getWalletById(id)?.toDomain()

    override suspend fun saveWallet(wallet: Wallet): Long =
        walletDao.insert(wallet.toEntity())

    override suspend fun updateWallet(wallet: Wallet) =
        walletDao.update(wallet.toEntity())

    override suspend fun deleteWallet(wallet: Wallet) =
        walletDao.delete(wallet.toEntity())

    override suspend fun setPrimaryWallet(id: Long) {
        walletDao.clearPrimaryFlag()
        val wallet = walletDao.getWalletById(id) ?: return
        walletDao.update(wallet.copy(isPrimary = true))
    }

    override suspend fun updateBalance(walletId: Long, amount: Double) {
        walletDao.updateBalance(walletId, amount)
    }
}
