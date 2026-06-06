package com.wallet.app.domain.usecase

import android.content.Context
import android.net.Uri
import com.wallet.app.domain.model.Transaction
import com.wallet.app.domain.repository.TransactionRepository
import com.wallet.app.util.CsvExporter
import com.wallet.app.util.DateUtils
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ExportUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(
        context: Context,
        uri: Uri,
        startDate: Long? = null,
        endDate: Long? = null
    ): Boolean {
        val transactions = if (startDate != null && endDate != null) {
            transactionRepository.getTransactionsBetweenDates(startDate, endDate).first()
        } else {
            transactionRepository.getAllTransactions().first()
        }

        val rows = transactions.map { t ->
            CsvExporter.ExportRow(
                date = DateUtils.formatDate(t.date),
                type = t.type.name,
                category = t.categoryName,
                amount = t.amount.toString(),
                note = t.note,
                wallet = t.walletName
            )
        }

        return CsvExporter.exportToCsv(context, uri, rows)
    }
}
