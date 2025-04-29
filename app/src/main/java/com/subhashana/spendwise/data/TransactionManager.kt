package com.subhashana.spendwise.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.subhashana.spendwise.model.Transaction
import com.subhashana.spendwise.model.TransactionType
import com.subhashana.spendwise.notification.NotificationManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TransactionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val notificationManager = NotificationManager(context)

    companion object {
        private const val PREFS_NAME = "SpendWisePrefs"
        private const val KEY_TRANSACTIONS = "transactions"
        private const val KEY_MONTHLY_BUDGET = "monthly_budget"
        private const val KEY_LAST_TRANSACTION_ID = "last_transaction_id"
    }

    fun saveTransaction(transaction: Transaction): Transaction {
        val transactions = getTransactions().toMutableList()
        val newId = getNextTransactionId()
        val newTransaction = transaction.copy(id = newId)
        transactions.add(newTransaction)
        saveTransactions(transactions)
        if (transaction.type == TransactionType.EXPENSE) {
            checkBudgetStatus()
        }
        return newTransaction
    }

    fun getTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(KEY_TRANSACTIONS, null)
        return if (json != null) {
            try {
                val type = object : TypeToken<List<Transaction>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    fun deleteTransaction(transactionId: Long) {
        val transactions = getTransactions()
        val deletedTransaction = transactions.find { it.id == transactionId }
        val updatedTransactions = transactions.filter { it.id != transactionId }
        saveTransactions(updatedTransactions)
        if (deletedTransaction?.type == TransactionType.EXPENSE) {
            checkBudgetStatus()
        }
    }

    fun updateTransaction(transaction: Transaction) {
        val transactions = getTransactions()
        val oldTransaction = transactions.find { it.id == transaction.id }
        val updatedTransactions = transactions.map {
            if (it.id == transaction.id) transaction else it
        }
        saveTransactions(updatedTransactions)
        if (oldTransaction?.type == TransactionType.EXPENSE || transaction.type == TransactionType.EXPENSE) {
            checkBudgetStatus()
        }
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        try {
            val json = gson.toJson(transactions)
            sharedPreferences.edit().putString(KEY_TRANSACTIONS, json).apply()
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error saving transactions", e)
        }
    }

    private fun getNextTransactionId(): Long {
        val lastId = sharedPreferences.getLong(KEY_LAST_TRANSACTION_ID, 0)
        val newId = lastId + 1
        sharedPreferences.edit().putLong(KEY_LAST_TRANSACTION_ID, newId).apply()
        return newId
    }

    fun setMonthlyBudget(budget: Double) {
        if (budget >= 0) {
            sharedPreferences.edit().putFloat(KEY_MONTHLY_BUDGET, budget.toFloat()).apply()
            checkBudgetStatus()
        }
    }

    fun getMonthlyBudget(): Double {
        return sharedPreferences.getFloat(KEY_MONTHLY_BUDGET, 0f).toDouble()
    }

    fun getTotalExpenses(): Double {
        return getTransactions()
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }
    }

    fun getTotalIncome(): Double {
        return getTransactions()
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }
    }

    fun getCategoryExpenses(): Map<String, Double> {
        return getTransactions()
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }
    }

    private fun checkBudgetStatus() {
        try {
            val monthlyBudget = getMonthlyBudget()
            if (monthlyBudget > 0) {
                val totalExpenses = getTotalExpenses()
                Log.d("TransactionManager", "Checking budget: $totalExpenses / $monthlyBudget")
                notificationManager.showBudgetAlert(totalExpenses, monthlyBudget)
            }
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error checking budget status", e)
        }
    }

    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }

    fun getIncomeForPeriod(startTime: Long, endTime: Long): Double {
        return getTransactions()
            .filter { it.type == TransactionType.INCOME && it.date.time in startTime..endTime }
            .sumOf { it.amount }
    }

    fun getExpenseForPeriod(startTime: Long, endTime: Long): Double {
        return getTransactions()
            .filter { it.type == TransactionType.EXPENSE && it.date.time in startTime..endTime }
            .sumOf { it.amount }
    }
} 