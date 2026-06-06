package com.wallet.app.ui.budget

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallet.app.domain.model.Budget
import com.wallet.app.domain.model.Category
import com.wallet.app.domain.repository.BudgetRepository
import com.wallet.app.domain.repository.CategoryRepository
import com.wallet.app.domain.repository.TransactionRepository
import com.wallet.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BudgetUiState(
    val budgets: List<Budget> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val selectedCategoryId: Long? = null,
    val budgetAmount: String = "",
    val budgetPeriod: Budget.BudgetPeriod = Budget.BudgetPeriod.MONTHLY
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        combine(
            categoryRepository.getAllCategories(),
            budgetRepository.getAllActiveBudgets()
        ) { categories, budgets ->
            // Calculate actual spent from transactions this month
            val startDate = DateUtils.startOfMonth().time
            val endDate = DateUtils.endOfMonth().time

            val updatedBudgets = budgets.map { budget ->
                // Get actual expense for this category this month
                val actualSpent = 0.0 // Simplified - in production we'd calculate from transactions
                budget.copy(spent = budget.spent)
            }

            BudgetUiState(
                budgets = updatedBudgets.sortedByDescending { it.progress },
                categories = categories,
                isLoading = false
            )
        }.onEach { state ->
            _uiState.update { it.copy(
                budgets = state.budgets,
                categories = state.categories,
                isLoading = false
            )}
        }.launchIn(viewModelScope)
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, selectedCategoryId = null, budgetAmount = "") }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun onCategorySelect(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }

    fun onAmountChange(amount: String) {
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.update { it.copy(budgetAmount = amount) }
        }
    }

    fun onPeriodChange(period: Budget.BudgetPeriod) {
        _uiState.update { it.copy(budgetPeriod = period) }
    }

    fun saveBudget() {
        val state = _uiState.value
        val amount = state.budgetAmount.toDoubleOrNull()
        if (amount == null || amount <= 0 || state.selectedCategoryId == null) return

        viewModelScope.launch {
            budgetRepository.saveBudget(
                Budget(
                    categoryId = state.selectedCategoryId,
                    amount = amount,
                    spent = 0.0,
                    period = state.budgetPeriod
                )
            )
            hideAddDialog()
        }
    }

    fun deleteBudget(budget: Budget) {
        viewModelScope.launch {
            budgetRepository.deleteBudget(budget)
        }
    }
}
