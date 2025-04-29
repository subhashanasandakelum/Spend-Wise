package com.subhashana.spendwise.model

import java.util.Date

data class Transaction(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: Date,
    val type: TransactionType
)

enum class TransactionType {
    INCOME,
    EXPENSE
} 