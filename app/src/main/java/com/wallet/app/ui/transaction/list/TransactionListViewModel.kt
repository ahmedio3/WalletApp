package com.wallet.app.ui.transaction.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallet.app.domain.model.Transaction
import com.wallet.app.domain.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionListUiState(
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: TransactionFilter = TransactionFilter.ALL,
    val isLoading: Boolean = true
)

enum class TransactionFilter { ALL, INCOME, EXPENSE }

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionListUiState())
    val uiState: StateFlow<TransactionListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(TransactionFilter.ALL)

    init {
        combine(
            _searchQuery.debounce(300),
            _selectedFilter
        ) { query, filter -> Pair(query, filter) }
            .flatMapLatest { (query, filter) ->
                when {
                    query.isNotBlank() -> transactionRepository.searchTransactions(query)
                    filter == TransactionFilter.INCOME -> transactionRepository.getTransactionsByType("INCOME")
                    filter == TransactionFilter.EXPENSE -> transactionRepository.getTransactionsByType("EXPENSE")
                    else -> transactionRepository.getAllTransactions()
                }
            }
            .onEach { transactions ->
                _uiState.update {
                    it.copy(
                        transactions = transactions,
                        searchQuery = _searchQuery.value,
                        selectedFilter = _selectedFilter.value,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onFilterChange(filter: TransactionFilter) {
        _selectedFilter.value = filter
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }
}
