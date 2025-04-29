package com.subhashana.spendwise.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.subhashana.spendwise.MainActivity
import com.subhashana.spendwise.R
import com.subhashana.spendwise.adapter.TransactionAdapter
import com.subhashana.spendwise.data.TransactionManager
import com.subhashana.spendwise.dialog.AddTransactionDialog
import com.subhashana.spendwise.dialog.EditTransactionDialog
import com.subhashana.spendwise.model.Transaction
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Toast
import android.widget.TextView

class TransactionsFragment : Fragment() {
    private lateinit var transactionManager: TransactionManager
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var rvTransactions: RecyclerView
    private lateinit var fabAddTransaction: FloatingActionButton
    private lateinit var tvNoTransactions: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)
        
        transactionManager = (activity as MainActivity).getTransactionManager()
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
        
        return view
    }

    override fun onResume() {
        super.onResume()
        updateTransactionsList()
    }

    private fun initializeViews(view: View) {
        rvTransactions = view.findViewById(R.id.rvTransactions)
        fabAddTransaction = view.findViewById(R.id.fabAddTransaction)
        tvNoTransactions = view.findViewById(R.id.tvNoTransactions)
    }

    private fun setupRecyclerView() {
        rvTransactions.layoutManager = LinearLayoutManager(context)
        transactionAdapter = TransactionAdapter(emptyList()) { transaction ->
            showEditTransactionDialog(transaction)
        }
        rvTransactions.adapter = transactionAdapter
    }

    private fun setupClickListeners() {
        fabAddTransaction.setOnClickListener {
            showAddTransactionDialog()
        }
    }

    private fun updateTransactionsList() {
        val transactions = transactionManager.getTransactions()
        transactionAdapter.updateTransactions(transactions)
        tvNoTransactions.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showAddTransactionDialog() {
        AddTransactionDialog.newInstance { transaction ->
            try {
                transactionManager.saveTransaction(transaction)
                updateTransactionsList()
                (activity as MainActivity).updateUI()
            } catch (e: Exception) {
                Toast.makeText(context, "Error saving transaction: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.show(parentFragmentManager, "AddTransactionDialog")
    }

    private fun showEditTransactionDialog(transaction: Transaction) {
        EditTransactionDialog.newInstance(
            transaction,
            onTransactionUpdated = { updatedTransaction ->
                try {
                    transactionManager.updateTransaction(updatedTransaction)
                    updateTransactionsList()
                    (activity as MainActivity).updateUI()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error updating transaction: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onTransactionDeleted = { deletedTransaction ->
                try {
                    transactionManager.deleteTransaction(deletedTransaction.id)
                    updateTransactionsList()
                    (activity as MainActivity).updateUI()
                    Toast.makeText(context, "Transaction deleted", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error deleting transaction: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        ).show(parentFragmentManager, "EditTransactionDialog")
    }
} 