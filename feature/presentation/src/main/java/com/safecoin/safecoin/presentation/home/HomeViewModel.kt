package com.safecoin.safecoin.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.safecoin.safecoin.domain.model.BankAccount
import com.safecoin.safecoin.domain.model.Transaction
import com.safecoin.safecoin.domain.model.TransactionType
import com.safecoin.safecoin.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val accounts: List<BankAccount> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val selectedAccountIndex: Int = 0,
    val showTransactionDialog: Boolean = false,
    val transactionType: TransactionType = TransactionType.DEPOSIT,
    val amountInput: String = "",
    val descriptionInput: String = "",
    val isLoading: Boolean = false,
    val message: String? = null,
)

class HomeViewModel(
    private val repository: FinanceRepository,
) : ViewModel() {

    private val dialogState = MutableStateFlow(
        DialogState(show = false, type = TransactionType.DEPOSIT, amount = "", description = ""),
    )
    private val feedback = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeAccounts(),
        repository.observeTransactions(limit = 20),
        dialogState,
        feedback,
    ) { accounts, transactions, dialog, message ->
        HomeUiState(
            accounts = accounts,
            transactions = transactions,
            selectedAccountIndex = dialog.selectedIndex.coerceIn(0, (accounts.size - 1).coerceAtLeast(0)),
            showTransactionDialog = dialog.show,
            transactionType = dialog.type,
            amountInput = dialog.amount,
            descriptionInput = dialog.description,
            message = message,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onAccountSelected(index: Int) {
        dialogState.update { it.copy(selectedIndex = index) }
    }

    fun openDepositDialog(accountIndex: Int) {
        dialogState.value = DialogState(
            show = true,
            type = TransactionType.DEPOSIT,
            selectedIndex = accountIndex,
        )
    }

    fun openWithdrawDialog(accountIndex: Int) {
        dialogState.value = DialogState(
            show = true,
            type = TransactionType.WITHDRAW,
            selectedIndex = accountIndex,
        )
    }

    fun dismissDialog() {
        dialogState.value = DialogState(show = false, selectedIndex = dialogState.value.selectedIndex)
        feedback.value = null
    }

    fun onAmountChange(value: String) {
        dialogState.update { it.copy(amount = value.filter { c -> c.isDigit() || c == '.' }) }
    }

    fun onDescriptionChange(value: String) {
        dialogState.update { it.copy(description = value) }
    }

    fun confirmTransaction() {
        val dialog = dialogState.value
        val amount = dialog.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            feedback.value = "Enter a valid amount"
            return
        }
        val accounts = uiState.value.accounts
        val account = accounts.getOrNull(dialog.selectedIndex) ?: return

        viewModelScope.launch {
            try {
                val description = dialog.description.ifBlank {
                    if (dialog.type == TransactionType.DEPOSIT) "Deposit" else "Withdrawal"
                }
                val category = if (dialog.type == TransactionType.DEPOSIT) "Transfer" else "Other"
                if (dialog.type == TransactionType.DEPOSIT) {
                    repository.deposit(account.id, amount, description, category)
                    feedback.value = "Deposited successfully"
                } else {
                    repository.withdraw(account.id, amount, description, category)
                    feedback.value = "Withdrawn successfully"
                }
                dismissDialog()
            } catch (e: Exception) {
                feedback.value = e.message ?: "Transaction failed"
            }
        }
    }

    fun clearMessage() {
        feedback.value = null
    }

    private data class DialogState(
        val show: Boolean = false,
        val type: TransactionType = TransactionType.DEPOSIT,
        val amount: String = "",
        val description: String = "",
        val selectedIndex: Int = 0,
    )

    companion object {
        fun factory(repository: FinanceRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    HomeViewModel(repository) as T
            }
    }
}
