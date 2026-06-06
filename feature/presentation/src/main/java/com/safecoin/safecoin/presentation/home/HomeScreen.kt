package com.safecoin.safecoin.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.safecoin.safecoin.design.components.EmptyState
import com.safecoin.safecoin.design.components.SectionHeader
import com.safecoin.safecoin.design.theme.PrimaryBlue
import com.safecoin.safecoin.design.theme.PrimaryBlueDark
import com.safecoin.safecoin.presentation.R
import com.safecoin.safecoin.presentation.components.BankAccountCarousel
import com.safecoin.safecoin.presentation.components.TransactionDialog
import com.safecoin.safecoin.presentation.components.TransactionListItem

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            item {
                HomeHeader(
                    accountName = uiState.accounts.getOrNull(uiState.selectedAccountIndex)?.name,
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.my_accounts))
                BankAccountCarousel(
                    accounts = uiState.accounts,
                    selectedIndex = uiState.selectedAccountIndex,
                    onPageChanged = viewModel::onAccountSelected,
                    onDeposit = viewModel::openDepositDialog,
                    onWithdraw = viewModel::openWithdrawDialog,
                )
            }

            item {
                SectionHeader(title = stringResource(R.string.recent_operations))
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }

            if (uiState.transactions.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.ReceiptLong,
                        title = stringResource(R.string.no_transactions),
                        subtitle = stringResource(R.string.no_transactions_hint),
                    )
                }
            } else {
                items(uiState.transactions, key = { it.id }) { transaction ->
                    TransactionListItem(transaction = transaction)
                }
            }
        }
    }

    TransactionDialog(
        visible = uiState.showTransactionDialog,
        type = uiState.transactionType,
        amount = uiState.amountInput,
        description = uiState.descriptionInput,
        onAmountChange = viewModel::onAmountChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onConfirm = viewModel::confirmTransaction,
        onDismiss = viewModel::dismissDialog,
    )
}

@Composable
private fun HomeHeader(accountName: String?) {
    val gradient = Brush.verticalGradient(
        colors = listOf(PrimaryBlue, PrimaryBlueDark.copy(alpha = 0.85f)),
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .padding(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = stringResource(R.string.home_greeting),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
            )
            Text(
                text = accountName ?: stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
            )
        }
    }
}
