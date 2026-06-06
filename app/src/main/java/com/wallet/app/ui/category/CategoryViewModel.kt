package com.wallet.app.ui.category

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wallet.app.domain.model.Category
import com.wallet.app.domain.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryListUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val newCategoryName: String = "",
    val newCategoryEmoji: String = "\uD83D\uDCA1",
    val newCategoryColor: Long = 0xFF6366F1
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryListUiState())
    val uiState: StateFlow<CategoryListUiState> = _uiState.asStateFlow()

    val presetEmojis = listOf(
        "\uD83C\uDF54", "\uD83D\uDE97", "\uD83C\uDFE0", "\uD83D\uDC8A", "\uD83C\uDFAE",
        "\uD83D\uDED2", "\u2708\uFE0F", "\uD83D\uDCDA", "\uD83D\uDCBC", "\uD83D\uDCB0",
        "\uD83D\uDCC8", "\uD83C\uDF81", "\uD83D\uDCB5", "\uD83C\uDFE6", "\uD83D\uDCB3",
        "\uD83D\uDCAA", "\uD83C\uDFC0", "\uD83D\uDCF1", "\uD83D\uDCBB", "\uD83D\uDEAA"
    )

    val presetColors = listOf(
        0xFFF59E0B, 0xFF3B82F6, 0xFF8B5CF6, 0xFFEC4899, 0xFF14B8A6,
        0xFFF97316, 0xFF06B6D4, 0xFF6366F1, 0xFF10B981, 0xFF22C55E,
        0xFF7C3AED, 0xFFE11D48, 0xFF34D399, 0xFF64748B, 0xFFEAB308
    )

    init {
        categoryRepository.getAllCategories().onEach { categories ->
            _uiState.update { it.copy(categories = categories, isLoading = false) }
        }.launchIn(viewModelScope)
    }

    fun showAddDialog() {
        _uiState.update { it.copy(showAddDialog = true, newCategoryName = "", newCategoryEmoji = "\uD83D\uDCA1", newCategoryColor = 0xFF6366F1) }
    }

    fun hideAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    fun onNewNameChange(name: String) {
        _uiState.update { it.copy(newCategoryName = name) }
    }

    fun onNewEmojiChange(emoji: String) {
        _uiState.update { it.copy(newCategoryEmoji = emoji) }
    }

    fun onNewColorChange(color: Long) {
        _uiState.update { it.copy(newCategoryColor = color) }
    }

    fun saveCategory() {
        val state = _uiState.value
        if (state.newCategoryName.isBlank()) return

        viewModelScope.launch {
            categoryRepository.saveCategory(
                Category(
                    name = state.newCategoryName,
                    emoji = state.newCategoryEmoji,
                    color = Color(state.newCategoryColor),
                    sortOrder = state.categories.size
                )
            )
            hideAddDialog()
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            categoryRepository.deleteCategory(category)
        }
    }
}
