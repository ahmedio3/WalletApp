package com.wallet.app.domain.usecase

import com.wallet.app.domain.repository.WalletRepository
import javax.inject.Inject

class TransferBetweenWalletsUseCase @Inject constructor(
    private val walletRepository: WalletRepository
) {
    sealed class TransferResult {
        data object Success : TransferResult()
        data class Error(val message: String) : TransferResult()
    }

    suspend operator fun invoke(
        fromWalletId: Long,
        toWalletId: Long,
        amount: Double
    ): TransferResult {
        if (amount <= 0) return TransferResult.Error("Amount must be positive")
        if (fromWalletId == toWalletId) return TransferResult.Error("Cannot transfer to the same wallet")

        val fromWallet = walletRepository.getWalletById(fromWalletId)
        val toWallet = walletRepository.getWalletById(toWalletId)

        if (fromWallet == null) return TransferResult.Error("Source wallet not found")
        if (toWallet == null) return TransferResult.Error("Destination wallet not found")
        if (fromWallet.balance < amount) return TransferResult.Error("Insufficient balance")

        walletRepository.updateBalance(fromWalletId, -amount)
        walletRepository.updateBalance(toWalletId, amount)

        return TransferResult.Success
    }
}
