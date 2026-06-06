package com.wallet.app.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wallet.app.domain.model.Transaction
import com.wallet.app.ui.components.*
import com.wallet.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToWallets: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Good ${getGreeting()}!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Here's your summary",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToWallets) {
                        BadgedBox(badge = {
                            if (uiState.dashboard.walletCount > 1) {
                                Badge { Text("${uiState.dashboard.walletCount}") }
                            }
                        }) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallets")
                        }
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddTransaction,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Balance Card
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    BalanceCard(
                        totalBalance = uiState.dashboard.totalBalance,
                        monthlyIncome = uiState.dashboard.monthlyIncome,
                        monthlyExpense = uiState.dashboard.monthlyExpense
                    )
                }

                // Quick Stats Row
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Income",
                            value = String.format("$%.0f", uiState.dashboard.monthlyIncome),
                            icon = Icons.Default.TrendingUp,
                            color = IncomeGreen,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Expense",
                            value = String.format("$%.0f", uiState.dashboard.monthlyExpense),
                            icon = Icons.Default.TrendingDown,
                            color = ExpenseRed,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Budget",
                            value = "${(uiState.dashboard.budgetProgress * 100).toInt()}%",
                            icon = Icons.Default.PieChart,
                            color = Amber500,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Quick Actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AssistChip(
                            onClick = onNavigateToAddTransaction,
                            label = { Text("Add Income") },
                            leadingIcon = { Icon(Icons.Default.ArrowUpward, null, tint = IncomeGreen) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        AssistChip(
                            onClick = onNavigateToAddTransaction,
                            label = { Text("Add Expense") },
                            leadingIcon = { Icon(Icons.Default.ArrowDownward, null, tint = ExpenseRed) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Budget Progress Section
                if (uiState.dashboard.budgetTotal > 0) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Budget Overview",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(onClick = onNavigateToBudgets) {
                                        Text("See All")
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                BudgetProgressBar(
                                    categoryName = "Total Budget",
                                    categoryEmoji = "\uD83D\uDCCB",
                                    spent = uiState.dashboard.budgetSpent,
                                    budget = uiState.dashboard.budgetTotal,
                                    color = Indigo500
                                )
                            }
                        }
                    }
                }

                // Recent Transactions Section
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Transactions",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = onNavigateToTransactions) {
                            Text("See All")
                        }
                    }
                }

                if (uiState.dashboard.recentTransactions.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            EmptyState(
                                emoji = "\uD83D\uDCB0",
                                title = "No transactions yet",
                                subtitle = "Tap the + button to add your first transaction"
                            )
                        }
                    }
                } else {
                    items(uiState.dashboard.recentTransactions) { transaction ->
                        TransactionRow(
                            transaction = transaction,
                            onClick = { /* detail */ }
                        )
                    }
                }
            }
        }
    }
}

private fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "Morning"
        in 12..16 -> "Afternoon"
        in 17..20 -> "Evening"
        else -> "Night"
    }
}
