package com.wallet.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    data object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    data object Transactions : Screen("transactions", "Transactions", Icons.Default.Receipt)
    data object Statistics : Screen("statistics", "Statistics", Icons.Default.BarChart)
    data object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Nested screens
    data object AddTransaction : Screen("add_transaction", "Add Transaction")
    data object TransactionDetail : Screen("transaction_detail/{id}", "Transaction Detail") {
        fun createRoute(id: Long) = "transaction_detail/$id"
    }
    data object WalletList : Screen("wallet_list", "Wallets")
    data object WalletDetail : Screen("wallet_detail/{id}", "Wallet Detail") {
        fun createRoute(id: Long) = "wallet_detail/$id"
    }
    data object AddWallet : Screen("add_wallet", "Add Wallet")
    data object CategoryList : Screen("category_list", "Categories")
    data object AddCategory : Screen("add_category", "Add Category")
    data object Budget : Screen("budget", "Budgets")
    data object AddBudget : Screen("add_budget", "Add Budget")
    data object Recurring : Screen("recurring", "Recurring")
    data object AddRecurring : Screen("add_recurring", "Add Recurring")
    data object Export : Screen("export", "Export Data")
    data object Settings : Screen("settings", "Settings")

    companion object {
        val bottomNavItems = listOf(Dashboard, Transactions, Statistics, Profile)
    }
}
