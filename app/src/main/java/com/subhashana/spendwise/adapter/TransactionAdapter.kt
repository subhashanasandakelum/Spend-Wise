package com.subhashana.spendwise.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.subhashana.spendwise.R
import com.subhashana.spendwise.model.Transaction
import com.subhashana.spendwise.model.TransactionType
import com.subhashana.spendwise.data.CurrencyManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private lateinit var currencyManager: CurrencyManager

    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        currencyManager = CurrencyManager(parent.context)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val currencyFormat = NumberFormat.getCurrencyInstance(currencyManager.getCurrencyLocale())
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        holder.tvTitle.text = transaction.title
        holder.tvAmount.text = currencyFormat.format(transaction.amount)
        holder.tvCategory.text = transaction.category
        holder.tvDate.text = dateFormat.format(transaction.date)

        // Set text color based on transaction type
        val amountColor = when (transaction.type) {
            TransactionType.INCOME -> holder.itemView.context.getColor(android.R.color.holo_green_dark)
            TransactionType.EXPENSE -> holder.itemView.context.getColor(android.R.color.holo_red_dark)
        }
        holder.tvAmount.setTextColor(amountColor)

        holder.itemView.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount() = transactions.size

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
} 