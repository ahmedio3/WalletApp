package com.wallet.app.domain.usecase

import com.wallet.app.domain.model.Transaction
import com.wallet.app.domain.repository.TransactionRepository
import com.wallet.app.domain.repository.WalletRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
) {
    suspend operator fun invoke(transaction: Transaction): Long {
        val id = transactionRepository.saveTransaction(transaction)
        val balanceChange = when (transaction.type) {
            Transaction.TransactionType.INCOME -> transaction.amount
            Transaction.TransactionType.EXPENSE -> -transaction.amount
        }
        walletRepository.updateBalance(transaction.walletId, balanceChange)
        return id
    }
}
