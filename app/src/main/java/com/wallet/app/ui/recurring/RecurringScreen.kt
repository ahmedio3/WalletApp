package com.wallet.app.ui.recurring

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import com.wallet.app.domain.model.RecurringTransaction
import com.wallet.app.domain.model.Transaction
import com.wallet.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringScreen(
    onBack: () -> Unit,
    viewModel: RecurringViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showAddDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Recurring")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.recurringList.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("\uD83D\uDD04", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("No recurring transactions", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("Tap + to set up recurring bills", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("${uiState.recurringList.size} active recurring", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

                uiState.recurringList.forEach { recurring ->
                    RecurringCard(
                        recurring = recurring,
                        onToggleActive = { viewModel.toggleActive(recurring) },
                        onDelete = { viewModel.deleteRecurring(recurring) }
                    )
                }
            }
        }

        // Add Dialog
        if (uiState.showAddDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideAddDialog() },
                title = { Text("New Recurring Transaction") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Type toggle
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = uiState.newType == Transaction.TransactionType.EXPENSE,
                                onClick = { viewModel.onTypeChange(Transaction.TransactionType.EXPENSE) },
                                label = { Text("Expense") }
                            )
                            FilterChip(
                                selected = uiState.newType == Transaction.TransactionType.INCOME,
                                onClick = { viewModel.onTypeChange(Transaction.TransactionType.INCOME) },
                                label = { Text("Income") }
                            )
                        }

                        OutlinedTextField(
                            value = uiState.newAmount,
                            onValueChange = { viewModel.onAmountChange(it) },
                            label = { Text("Amount") },
                            prefix = { Text("$") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Text("Category:", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            uiState.categories.forEach { category ->
                                FilterChip(
                                    selected = category.id == uiState.newCategoryId,
                                    onClick = { viewModel.onCategorySelect(category.id) },
                                    label = { Text("${category.emoji} ${category.name}") }
                                )
                            }
                        }

                        Text("Interval:", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            RecurringTransaction.RecurringInterval.values().forEach { interval ->
                                FilterChip(
                                    selected = uiState.newInterval == interval,
                                    onClick = { viewModel.onIntervalChange(interval) },
                                    label = { Text(interval.name) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.saveRecurring() },
                        enabled = uiState.newAmount.toDoubleOrNull() != null && uiState.newCategoryId != null
                    ) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideAddDialog() }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun RecurringCard(
    recurring: RecurringTransaction,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    val isIncome = recurring.type == Transaction.TransactionType.INCOME
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    val amountPrefix = if (isIncome) "+" else "-"
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (recurring.isActive) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(Color(recurring.categoryColor).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(recurring.categoryEmoji, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(recurring.categoryName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Text(
                    "${recurring.interval.name} - Next: ${dateFormat.format(recurring.nextDate)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$amountPrefix$${String.format("%.2f", recurring.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
                Row {
                    IconButton(onClick = onToggleActive, modifier = Modifier.size(28.dp)) {
                        Icon(
                            if (recurring.isActive) Icons.Default.Notifications else Icons.Default.NotificationsOff,
                            contentDescription = if (recurring.isActive) "Active" else "Inactive",
                            tint = if (recurring.isActive) IncomeGreen else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}
