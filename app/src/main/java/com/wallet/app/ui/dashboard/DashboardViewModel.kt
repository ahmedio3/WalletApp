package com.wallet.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallet.app.domain.model.DashboardData
import com.wallet.app.domain.usecase.GetDashboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val dashboard: DashboardData = DashboardData(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        getDashboardUseCase().onEach { dashboard ->
            _uiState.update { it.copy(dashboard = dashboard, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadDashboard()
    }
}
