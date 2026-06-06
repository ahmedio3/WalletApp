package com.wallet.app.domain.repository

import com.wallet.app.domain.model.CategoryBreakdown
import com.wallet.app.domain.model.MonthlyReport
import kotlinx.coroutines.flow.Flow

interface AnalyticsRepository {
    fun getMonthlyReport(year: Int, month: Int): Flow<MonthlyReport>
    fun getYearlyReport(year: Int): Flow<List<MonthlyReport>>
    fun getCategoryBreakdown(startDate: Long, endDate: Long): Flow<List<CategoryBreakdown>>
}
