package com.wallet.app.domain.usecase

import com.wallet.app.domain.model.CategoryBreakdown
import com.wallet.app.domain.model.MonthlyReport
import com.wallet.app.domain.repository.AnalyticsRepository
import com.wallet.app.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject

class GetMonthlyReportUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val analyticsRepository: AnalyticsRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<MonthlyReport> = flow {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1, 0, 0, 0)
        val startDate = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endDate = cal.timeInMillis

        val income = transactionRepository.getTotalIncome(startDate, endDate).first() ?: 0.0
        val expense = transactionRepository.getTotalExpense(startDate, endDate).first() ?: 0.0

        val categoryBreakdown = analyticsRepository.getCategoryBreakdown(startDate, endDate).first()

        val monthName = java.text.DateFormatSymbols().months[month - 1]
        emit(
            MonthlyReport(
                month = "$monthName $year",
                income = income,
                expense = expense,
                categories = categoryBreakdown
            )
        )
    }
}
