package com.subhashana.spendwise.dialog

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.subhashana.spendwise.R
import com.subhashana.spendwise.model.Transaction
import com.subhashana.spendwise.model.TransactionType
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.Spinner

class EditTransactionDialog : DialogFragment() {
    private var transaction: Transaction? = null
    private var onTransactionUpdated: ((Transaction) -> Unit)? = null
    private var onTransactionDeleted: ((Transaction) -> Unit)? = null

    private fun containsNumbers(text: String): Boolean {
        return text.any { it.isDigit() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<TextInputEditText>(R.id.etTitle)
        val etAmount = view.findViewById<TextInputEditText>(R.id.etAmount)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerType = view.findViewById<Spinner>(R.id.spinnerType)
        val btnSave = view.findViewById<MaterialButton>(R.id.btnSave)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)
        val btnCancel = view.findViewById<MaterialButton>(R.id.btnCancel)

        // Setup category spinner
        val categories = arrayOf(
            getString(R.string.category_food),
            getString(R.string.category_transport),
            getString(R.string.category_bills),
            getString(R.string.category_entertainment),
            getString(R.string.category_shopping),
            getString(R.string.category_other)
        )
        spinnerCategory.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        // Setup type spinner
        val types = arrayOf(
            getString(R.string.type_income),
            getString(R.string.type_expense)
        )
        spinnerType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            types
        )

        // Populate fields with existing transaction data
        transaction?.let { trans ->
            etTitle.setText(trans.title)
            etAmount.setText(trans.amount.toString())
            spinnerCategory.setSelection(categories.indexOf(trans.category))
            spinnerType.setSelection(types.indexOf(
                when (trans.type) {
                    TransactionType.INCOME -> getString(R.string.type_income)
                    TransactionType.EXPENSE -> getString(R.string.type_expense)
                }
            ))
        }

        btnSave.setOnClickListener {
            val title = etTitle.text.toString()
            val amountStr = etAmount.text.toString()
            val category = spinnerCategory.selectedItem.toString()
            val type = when (spinnerType.selectedItem.toString()) {
                getString(R.string.type_income) -> TransactionType.INCOME
                else -> TransactionType.EXPENSE
            }

            if (title.isBlank()) {
                Toast.makeText(context, R.string.error_empty_title, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (containsNumbers(title)) {
                Toast.makeText(context, "Title cannot contain numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amountStr.isBlank()) {
                Toast.makeText(context, R.string.error_empty_amount, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(context, R.string.error_invalid_amount, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            transaction?.let { trans ->
                val updatedTransaction = trans.copy(
                    title = title,
                    amount = amount,
                    category = category,
                    type = type
                )
                onTransactionUpdated?.invoke(updatedTransaction)
                dismiss()
            }
        }

        btnDelete.setOnClickListener {
            transaction?.let { trans ->
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.delete_transaction)
                    .setMessage(R.string.delete_transaction_confirmation)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        onTransactionDeleted?.invoke(trans)
                        dismiss()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(
            transaction: Transaction,
            onTransactionUpdated: (Transaction) -> Unit,
            onTransactionDeleted: (Transaction) -> Unit
        ): EditTransactionDialog {
            return EditTransactionDialog().apply {
                this.transaction = transaction
                this.onTransactionUpdated = onTransactionUpdated
                this.onTransactionDeleted = onTransactionDeleted
            }
        }
    }
} 