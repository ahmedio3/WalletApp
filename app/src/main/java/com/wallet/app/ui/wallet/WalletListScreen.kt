package com.wallet.app.ui.wallet

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
import com.wallet.app.domain.model.Wallet
import com.wallet.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletListScreen(
    onBack: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallets", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.wallets.size >= 2) {
                        IconButton(onClick = { viewModel.showTransferDialog() }) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Transfer")
                        }
                    }
                    IconButton(onClick = { viewModel.showAddDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Wallet")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.wallets.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No wallets yet. Tap + to add one.")
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
                // Total balance card
                val totalBalance = uiState.wallets.sumOf { it.balance }
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Indigo600)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Total in Wallets", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            String.format("$%.2f", totalBalance),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text("Your Wallets", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                uiState.wallets.forEach { wallet ->
                    WalletCard(
                        wallet = wallet,
                        onSetPrimary = { viewModel.setPrimaryWallet(wallet) },
                        onDelete = { viewModel.deleteWallet(wallet) }
                    )
                }
            }
        }

        // Add Wallet Dialog
        if (uiState.showAddDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideAddDialog() },
                title = { Text("New Wallet") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = uiState.newWalletName,
                            onValueChange = { viewModel.onNewWalletNameChange(it) },
                            label = { Text("Wallet Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Choose emoji:", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        val emojis = listOf("\uD83D\uDCB5", "\uD83D\uDCB3", "\uD83C\uDFE6", "\uD83D\uDCB0", "\uD83D\uDCB2", "\uD83D\uDC51", "\u2601\uFE0F")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            emojis.forEach { emoji ->
                                FilterChip(
                                    selected = uiState.newWalletEmoji == emoji,
                                    onClick = { viewModel.onNewWalletEmojiChange(emoji) },
                                    label = { Text(emoji) }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.saveWallet() },
                        enabled = uiState.newWalletName.isNotBlank()
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideAddDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Transfer Dialog
        if (uiState.showTransferDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideTransferDialog() },
                title = { Text("Transfer Between Wallets") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // From
                        Text("From:", style = MaterialTheme.typography.labelMedium)
                        uiState.wallets.forEach { wallet ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.onTransferFromChange(wallet) }
                                    .background(if (wallet.id == uiState.transferFromWallet?.id) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(wallet.emoji)
                                Spacer(Modifier.width(8.dp))
                                Text(wallet.name, modifier = Modifier.weight(1f))
                                Text(String.format("$%.2f", wallet.balance), style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        HorizontalDivider()

                        // To
                        Text("To:", style = MaterialTheme.typography.labelMedium)
                        uiState.wallets.filter { it.id != uiState.transferFromWallet?.id }.forEach { wallet ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.onTransferToChange(wallet) }
                                    .background(if (wallet.id == uiState.transferToWallet?.id) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(wallet.emoji)
                                Spacer(Modifier.width(8.dp))
                                Text(wallet.name, modifier = Modifier.weight(1f))
                                Text(String.format("$%.2f", wallet.balance), style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        // Amount
                        OutlinedTextField(
                            value = uiState.transferAmount,
                            onValueChange = { viewModel.onTransferAmountChange(it) },
                            label = { Text("Amount") },
                            prefix = { Text("$") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Error
                        if (uiState.transferResult != null) {
                            Text(
                                text = uiState.transferResult ?: "",
                                color = ExpenseRed,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.executeTransfer() }) {
                        Text("Transfer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.hideTransferDialog() }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun WalletCard(
    wallet: Wallet,
    onSetPrimary: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji circle
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Indigo100),
                contentAlignment = Alignment.Center
            ) {
                Text(text = wallet.emoji, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = wallet.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (wallet.isPrimary) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Indigo500.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Primary",
                                style = MaterialTheme.typography.labelSmall,
                                color = Indigo600,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = String.format("$%.2f", wallet.balance),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Actions
            if (!wallet.isPrimary) {
                IconButton(onClick = onSetPrimary) {
                    Icon(Icons.Default.StarBorder, contentDescription = "Set as primary", tint = Amber500)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed)
                }
            }
        }
    }
}
