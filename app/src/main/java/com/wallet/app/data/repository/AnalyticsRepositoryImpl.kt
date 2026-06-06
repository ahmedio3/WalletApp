package com.wallet.app.data.repository

import com.wallet.app.data.local.dao.CategoryDao
import com.wallet.app.data.local.dao.TransactionDao
import com.wallet.app.domain.model.CategoryBreakdown
import com.wallet.app.domain.model.MonthlyReport
import com.wallet.app.domain.repository.AnalyticsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao
) : AnalyticsRepository {

    override fun getMonthlyReport(year: Int, month: Int): Flow<MonthlyReport> = flow {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1, 0, 0, 0)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val startDate = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endDate = cal.timeInMillis

        val income = transactionDao.getTotalIncome(startDate, endDate).first() ?: 0.0
        val expense = transactionDao.getTotalExpense(startDate, endDate).first() ?: 0.0

        val categoryExpenses = transactionDao.getExpenseByCategory(startDate, endDate).first()
        val totalExpense = categoryExpenses.sumOf { it.total }

        val categories = categoryExpenses.map { ce ->
            val category = categoryDao.getCategoryById(ce.categoryId)
            CategoryBreakdown(
                categoryId = ce.categoryId,
                categoryName = category?.name ?: "Unknown",
                categoryEmoji = category?.emoji ?: "\uD83D\uDCB0",
                categoryColor = category?.color ?: 0xFF6366F1,
                amount = ce.total,
                percentage = if (totalExpense > 0) (ce.total / totalExpense).toFloat() else 0f
            )
        }

        val monthName = java.text.DateFormatSymbols().months[month]
        emit(MonthlyReport(
            month = "$monthName $year",
            income = income,
            expense = expense,
            categories = categories.sortedByDescending { it.amount }
        ))
    }

    override fun getYearlyReport(year: Int): Flow<List<MonthlyReport>> = flow {
        val reports = (0..11).map { month ->
            getMonthlyReport(year, month).first()
        }
        emit(reports)
    }

    override fun getCategoryBreakdown(startDate: Long, endDate: Long): Flow<List<CategoryBreakdown>> = flow {
        val categoryExpenses = transactionDao.getExpenseByCategory(startDate, endDate).first()
        val totalExpense = categoryExpenses.sumOf { it.total }

        val breakdown = categoryExpenses.map { ce ->
            val category = categoryDao.getCategoryById(ce.categoryId)
            CategoryBreakdown(
                categoryId = ce.categoryId,
                categoryName = category?.name ?: "Unknown",
                categoryEmoji = category?.emoji ?: "\uD83D\uDCB0",
                categoryColor = category?.color ?: 0xFF6366F1,
                amount = ce.total,
                percentage = if (totalExpense > 0) (ce.total / totalExpense).toFloat() else 0f
            )
        }
        emit(breakdown.sortedByDescending { it.amount })
    }
}
