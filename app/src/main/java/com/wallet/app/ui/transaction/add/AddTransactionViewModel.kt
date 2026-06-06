package com.wallet.app.ui.transaction.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallet.app.domain.model.Category
import com.wallet.app.domain.model.Transaction
import com.wallet.app.domain.model.Wallet
import com.wallet.app.domain.repository.CategoryRepository
import com.wallet.app.domain.repository.WalletRepository
import com.wallet.app.domain.usecase.AddTransactionUseCase
import com.wallet.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class AddTransactionUiState(
    val type: Transaction.TransactionType = Transaction.TransactionType.EXPENSE,
    val amount: String = "",
    val selectedCategory: Category? = null,
    val selectedWallet: Wallet? = null,
    val note: String = "",
    val date: Date = Date(),
    val categories: List<Category> = emptyList(),
    val wallets: List<Wallet> = emptyList(),
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val categoryRepository: CategoryRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            walletRepository.getAllWallets().collect { wallets ->
                val primaryWallet = wallets.find { it.isPrimary } ?: wallets.firstOrNull()
                _uiState.update {
                    it.copy(
                        wallets = wallets,
                        selectedWallet = it.selectedWallet ?: primaryWallet
                    )
                }
            }
        }
    }

    fun onTypeChange(type: Transaction.TransactionType) {
        _uiState.update { it.copy(type = type) }
    }

    fun onAmountChange(amount: String) {
        if (amount.isEmpty() || amount.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
            _uiState.update { it.copy(amount = amount, error = null) }
        }
    }

    fun onCategorySelect(category: Category) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onWalletSelect(wallet: Wallet) {
        _uiState.update { it.copy(selectedWallet = wallet) }
    }

    fun onNoteChange(note: String) {
        _uiState.update { it.copy(note = note) }
    }

    fun onDateChange(date: Date) {
        _uiState.update { it.copy(date = date) }
    }

    fun save() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull()

        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(error = "Please enter a valid amount") }
            return
        }
        if (state.selectedCategory == null) {
            _uiState.update { it.copy(error = "Please select a category") }
            return
        }
        if (state.selectedWallet == null) {
            _uiState.update { it.copy(error = "Please select a wallet") }
            return
        }

        _uiState.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                addTransactionUseCase(
                    Transaction(
                        type = state.type,
                        amount = amount,
                        categoryId = state.selectedCategory.id,
                        walletId = state.selectedWallet.id,
                        note = state.note,
                        date = state.date
                    )
                )
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Failed to save") }
            }
        }
    }
}
