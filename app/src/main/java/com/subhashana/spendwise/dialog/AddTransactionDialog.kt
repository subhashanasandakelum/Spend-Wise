package com.subhashana.spendwise.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.subhashana.spendwise.R
import com.subhashana.spendwise.model.Transaction
import com.subhashana.spendwise.model.TransactionType
import java.util.Date

class AddTransactionDialog : DialogFragment() {
    private var onTransactionAdded: ((Transaction) -> Unit)? = null

    private fun containsNumbers(text: String): Boolean {
        return text.any { it.isDigit() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val spinnerType = view.findViewById<Spinner>(R.id.spinnerType)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

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

            val transaction = Transaction(
                title = title,
                amount = amount,
                category = category,
                date = Date(),
                type = type
            )

            onTransactionAdded?.invoke(transaction)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(onTransactionAdded: (Transaction) -> Unit): AddTransactionDialog {
            return AddTransactionDialog().apply {
                this.onTransactionAdded = onTransactionAdded
            }
        }
    }
} 