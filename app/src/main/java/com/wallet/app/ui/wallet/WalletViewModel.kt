package com.wallet.app.ui.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallet.app.domain.model.Wallet
import com.wallet.app.domain.repository.WalletRepository
import com.wallet.app.domain.usecase.TransferBetweenWalletsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WalletListUiState(
    val wallets: List<Wallet> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val showTransferDialog: Boolean = false,
    val newWalletName: String = "",
    val newWalletEmoji: String = "\uD83D\uDCB5",
    val transferFromWallet: Wallet? = null,
    val transferToWallet: Wallet? = null,
    val transferAmount: String = "",
    val transferResult: String? = null
)

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val transferUseCase: TransferBetweenWalletsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalletListUiState())
    val uiState: StateFlow<WalletListUiState> = _uiState.asStateFlow()

    init {
        walletRepository.getAllWallets().onEach { wallets ->
            _uiState.update { it.copy(wallets = wallets, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, newWalletName = "", newWalletEmoji = "\uD83D\uDCB5") }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun onNewWalletNameChange(name: String) {
        _uiState.update { it.copy(newWalletName = name) }
    }

    fun onNewWalletEmojiChange(emoji: String) {
        _uiState.update { it.copy(newWalletEmoji = emoji) }
    }

    fun saveWallet() {
        val state = _uiState.value
        if (state.newWalletName.isBlank()) return

        viewModelScope.launch {
            walletRepository.saveWallet(
                Wallet(
                    name = state.newWalletName,
                    emoji = state.newWalletEmoji,
                    balance = 0.0,
                    isPrimary = state.wallets.isEmpty()
                )
            )
            hideAddDialog()
        }
    }

    fun deleteWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletRepository.deleteWallet(wallet)
        }
    }

    fun setPrimaryWallet(wallet: Wallet) {
        viewModelScope.launch {
            walletRepository.setPrimaryWallet(wallet.id)
        }
    }

    fun showTransferDialog() {
        val wallets = _uiState.value.wallets
        if (wallets.size >= 2) {
            _uiState.update {
                it.copy(
                    showTransferDialog = true,
                    transferFromWallet = wallets.find { it.isPrimary } ?: wallets[0],
                    transferToWallet = wallets[1],
                    transferAmount = "",
                    transferResult = null
                )
            }
        }
    }

    fun hideTransferDialog() {
        _uiState.update { it.copy(showTransferDialog = false) }
    }

    fun onTransferFromChange(wallet: Wallet) {
        _uiState.update { it.copy(transferFromWallet = wallet) }
    }

    fun onTransferToChange(wallet: Wallet) {
        _uiState.update { it.copy(transferToWallet = wallet) }
    }

    fun onTransferAmountChange(amount: String) {
        _uiState.update { it.copy(transferAmount = amount, transferResult = null) }
    }

    fun executeTransfer() {
        val state = _uiState.value
        val amount = state.transferAmount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(transferResult = "Invalid amount") }
            return
        }
        if (state.transferFromWallet == null || state.transferToWallet == null) {
            _uiState.update { it.copy(transferResult = "Select wallets") }
            return
        }

        viewModelScope.launch {
            val result = transferUseCase(
                fromWalletId = state.transferFromWallet.id,
                toWalletId = state.transferToWallet.id,
                amount = amount
            )
            when (result) {
                is TransferBetweenWalletsUseCase.TransferResult.Success -> {
                    _uiState.update { it.copy(showTransferDialog = false, transferResult = null) }
                }
                is TransferBetweenWalletsUseCase.TransferResult.Error -> {
                    _uiState.update { it.copy(transferResult = result.message) }
                }
            }
        }
    }
}
