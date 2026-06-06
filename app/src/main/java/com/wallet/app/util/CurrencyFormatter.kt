package com.wallet.app.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val format: NumberFormat by lazy {
        NumberFormat.getCurrencyInstance(Locale.US)
    }

    fun format(amount: Double, currency: String = Constants.DEFAULT_CURRENCY): String {
        val fmt = NumberFormat.getCurrencyInstance(Locale.US)
        return fmt.format(amount)
    }

    fun formatCompact(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
            else -> String.format("%.2f", amount)
        }
    }
}
