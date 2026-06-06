package com.wallet.app.util

object Constants {
    const val DATABASE_NAME = "wallet_db"
    const val DEFAULT_CURRENCY = "USD"
    const val PREFS_NAME = "wallet_prefs"
    const val THEME_KEY = "theme_mode"
    const val CURRENCY_KEY = "currency"

    val DEFAULT_CATEGORIES = listOf(
        CategoryData("\uD83C\uDF54", "Food & Dining", 0xFFF59E0B),
        CategoryData("\uD83D\uDE97", "Transport", 0xFF3B82F6),
        CategoryData("\uD83C\uDFE0", "Rent & Housing", 0xFF8B5CF6),
        CategoryData("\uD83D\uDC8A", "Health", 0xFFEC4899),
        CategoryData("\uD83C\uDFAE", "Entertainment", 0xFF14B8A6),
        CategoryData("\uD83D\uDED2", "Shopping", 0xFFF97316),
        CategoryData("\u2708\uFE0F", "Travel", 0xFF06B6D4),
        CategoryData("\uD83D\uDCDA", "Education", 0xFF6366F1),
        CategoryData("\uD83D\uDCBC", "Salary", 0xFF10B981),
        CategoryData("\uD83D\uDCB0", "Freelance", 0xFF22C55E),
        CategoryData("\uD83D\uDCC8", "Investment", 0xFF7C3AED),
        CategoryData("\uD83C\uDF81", "Gift", 0xFFE11D48),
        CategoryData("\uD83D\uDCB5", "Cash", 0xFF34D399),
        CategoryData("\uD83C\uDFE6", "Bills & Utilities", 0xFF64748B),
        CategoryData("\uD83D\uDCB3", "Insurance", 0xFFEAB308)
    )
}

data class CategoryData(
    val emoji: String,
    val name: String,
    val color: Long
)
