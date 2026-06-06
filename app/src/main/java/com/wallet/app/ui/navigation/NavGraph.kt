package com.wallet.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wallet.app.ui.dashboard.DashboardScreen
import com.wallet.app.ui.transaction.list.TransactionListScreen
import com.wallet.app.ui.stats.StatsScreen
import com.wallet.app.ui.profile.ProfileScreen
import com.wallet.app.ui.transaction.add.AddTransactionScreen
import com.wallet.app.ui.wallet.WalletListScreen
import com.wallet.app.ui.category.CategoryListScreen
import com.wallet.app.ui.budget.BudgetScreen
import com.wallet.app.ui.recurring.RecurringScreen
import com.wallet.app.ui.profile.SettingsScreen
import com.wallet.app.ui.profile.ExportScreen
import com.wallet.app.ui.transaction.detail.TransactionDetailScreen

@Composable
fun WalletNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in Screen.bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn(tween(200)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut(tween(200)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn(tween(200)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(200)) }
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                    onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                    onNavigateToWallets = { navController.navigate(Screen.WalletList.route) },
                    onNavigateToBudgets = { navController.navigate(Screen.Budget.route) }
                )
            }

            composable(Screen.Transactions.route) {
                TransactionListScreen(
                    onAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                    onTransactionClick = { id -> navController.navigate(Screen.TransactionDetail.createRoute(id)) }
                )
            }

            composable(Screen.Statistics.route) {
                StatsScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToWallets = { navController.navigate(Screen.WalletList.route) },
                    onNavigateToCategories = { navController.navigate(Screen.CategoryList.route) },
                    onNavigateToBudgets = { navController.navigate(Screen.Budget.route) },
                    onNavigateToRecurring = { navController.navigate(Screen.Recurring.route) },
                    onNavigateToExport = { navController.navigate(Screen.Export.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }

            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.TransactionDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0L
                TransactionDetailScreen(
                    transactionId = id,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.WalletList.route) {
                WalletListScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.CategoryList.route) {
                CategoryListScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Budget.route) {
                BudgetScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Recurring.route) {
                RecurringScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Export.route) {
                ExportScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
