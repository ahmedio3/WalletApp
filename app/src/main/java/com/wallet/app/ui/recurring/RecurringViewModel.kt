package com.wallet.app.ui.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallet.app.domain.model.Category
import com.wallet.app.domain.model.RecurringTransaction
import com.wallet.app.domain.model.Transaction
import com.wallet.app.domain.model.Wallet
import com.wallet.app.domain.repository.CategoryRepository
import com.wallet.app.domain.repository.RecurringRepository
import com.wallet.app.domain.repository.WalletRepository
import com.wallet.app.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class RecurringUiState(
    val recurringList: List<RecurringTransaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val newType: Transaction.TransactionType = Transaction.TransactionType.EXPENSE,
    val newAmount: String = "",
    val newCategoryId: Long? = null,
    val newWalletId: Long? = null,
    val newNote: String = "",
    val newInterval: RecurringTransaction.RecurringInterval = RecurringTransaction.RecurringInterval.MONTHLY
)

@HiltViewModel
class RecurringViewModel @Inject constructor(
    private val recurringRepository: RecurringRepository,
    private val categoryRepository: CategoryRepository,
    private val walletRepository: WalletRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecurringUiState())
    val uiState: StateFlow<RecurringUiState> = _uiState.asStateFlow()

    init {
        combine(
            recurringRepository.getAllRecurring(),
            categoryRepository.getAllCategories(),
            walletRepository.getAllWallets()
        ) { recurring, categories, wallets ->
            RecurringUiState(
                recurringList = recurring,
                categories = categories,
                wallets = wallets,
                isLoading = false
            )
        }.onEach { state ->
            _uiState.update { it.copy(
                recurringList = state.recurringList,
                categories = state.categories,
                wallets = state.wallets,
                isLoading = false
            )}
        }.launchIn(viewModelScope)
    }

    fun showAddDialog() {
        _uiState.update {
            it.copy(
                showAddDialog = true,
                newAmount = "",
                newType = Transaction.TransactionType.EXPENSE,
                newCategoryId = null,
                newWalletId = it.wallets.find { w -> w.isPrimary }?.id,
                newNote = "",
                newInterval = RecurringTransaction.RecurringInterval.MONTHLY
            )
        }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun onTypeChange(type: Transaction.TransactionType) {
        _uiState.update { it.copy(newType = type) }
    }

    fun onAmountChange(amount: String) {
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.update { it.copy(newAmount = amount) }
        }
    }

    fun onCategorySelect(categoryId: Long) {
        _uiState.update { it.copy(newCategoryId = categoryId) }
    }

    fun onWalletSelect(walletId: Long) {
        _uiState.update { it.copy(newWalletId = walletId) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(newNote = note) }
    }

    fun onIntervalChange(interval: RecurringTransaction.RecurringInterval) {
        _uiState.update { it.copy(newInterval = interval) }
    }

    fun saveRecurring() {
        val state = _uiState.value
        val amount = state.newAmount.toDoubleOrNull()
        if (amount == null || amount <= 0 || state.newCategoryId == null || state.newWalletId == null) return

        viewModelScope.launch {
            recurringRepository.saveRecurring(
                RecurringTransaction(
                    type = state.newType,
                    amount = amount,
                    categoryId = state.newCategoryId,
                    walletId = state.newWalletId,
                    note = state.newNote,
                    interval = state.newInterval,
                    nextDate = Date()
                )
            )
            hideAddDialog()
        }
    }

    fun toggleActive(recurring: RecurringTransaction) {
        viewModelScope.launch {
            recurringRepository.updateRecurring(recurring.copy(isActive = !recurring.isActive))
        }
    }

    fun deleteRecurring(recurring: RecurringTransaction) {
        viewModelScope.launch {
            recurringRepository.deleteRecurring(recurring)
        }
    }
}


