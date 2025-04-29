package com.subhashana.spendwise.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.subhashana.spendwise.R
import com.google.android.material.textfield.TextInputEditText

class SetBudgetDialog : DialogFragment() {
    private var onBudgetSet: ((Double) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_set_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etBudget = view.findViewById<TextInputEditText>(R.id.etBudget)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        btnSave.setOnClickListener {
            val budgetStr = etBudget.text.toString()

            if (budgetStr.isBlank()) {
                Toast.makeText(context, R.string.error_empty_amount, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val budget = budgetStr.toDoubleOrNull()
            if (budget == null || budget < 0) {
                Toast.makeText(context, R.string.error_invalid_budget, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onBudgetSet?.invoke(budget)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(onBudgetSet: (Double) -> Unit): SetBudgetDialog {
            return SetBudgetDialog().apply {
                this.onBudgetSet = onBudgetSet
            }
        }
    }
} 