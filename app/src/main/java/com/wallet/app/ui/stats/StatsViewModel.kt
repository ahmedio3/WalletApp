package com.wallet.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallet.app.domain.model.MonthlyReport
import com.wallet.app.domain.usecase.GetMonthlyReportUseCase
import com.wallet.app.domain.repository.TransactionRepository
import com.wallet.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class StatsUiState(
    val currentMonthReport: MonthlyReport? = null,
    val isLoading: Boolean = true,
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val balanceHistory: List<BalancePoint> = emptyList()
)

data class BalancePoint(
    val label: String,
    val balance: Double
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val getMonthlyReportUseCase: GetMonthlyReportUseCase,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadReport()
        loadBalanceHistory()
    }

    private fun loadReport() {
        viewModelScope.launch {
            val state = _uiState.value
            getMonthlyReportUseCase(state.selectedYear, state.selectedMonth + 1)
                .onEach { report ->
                    _uiState.update { it.copy(currentMonthReport = report, isLoading = false) }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun loadBalanceHistory() {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -5)
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)

            val points = mutableListOf<BalancePoint>()
            val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

            for (i in 0..5) {
                val startOfMonth = cal.timeInMillis
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                cal.set(Calendar.HOUR_OF_DAY, 23)
                cal.set(Calendar.MINUTE, 59)
                val endOfMonth = cal.timeInMillis

                val income = transactionRepository.getTotalIncome(startOfMonth, endOfMonth).first() ?: 0.0
                val expense = transactionRepository.getTotalExpense(startOfMonth, endOfMonth).first() ?: 0.0

                points.add(
                    BalancePoint(
                        label = months[cal.get(Calendar.MONTH)],
                        balance = income - expense
                    )
                )

                cal.add(Calendar.MONTH, 1)
                cal.set(Calendar.DAY_OF_MONTH, 1)
            }

            _uiState.update { it.copy(balanceHistory = points) }
        }
    }

    fun onMonthChange(month: Int, year: Int) {
        _uiState.update { it.copy(selectedMonth = month, selectedYear = year, isLoading = true) }
        loadReport()
    }
}
