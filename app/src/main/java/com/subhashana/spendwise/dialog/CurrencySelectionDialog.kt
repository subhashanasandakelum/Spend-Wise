package com.subhashana.spendwise.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.subhashana.spendwise.R
import com.subhashana.spendwise.data.CurrencyManager
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class CurrencySelectionDialog : DialogFragment() {
    private lateinit var currencyManager: CurrencyManager
    private var onCurrencySelected: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_currency_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currencyManager = CurrencyManager(requireContext())
        val autoCompleteCurrency = view.findViewById<MaterialAutoCompleteTextView>(R.id.autoCompleteCurrency)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        // Set up currency spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            CurrencyManager.SUPPORTED_CURRENCIES
        )
        autoCompleteCurrency.setAdapter(adapter)
        autoCompleteCurrency.setText(currencyManager.getCurrency(), false)

        btnSave.setOnClickListener {
            val selectedCurrency = autoCompleteCurrency.text.toString()
            if (selectedCurrency in CurrencyManager.SUPPORTED_CURRENCIES) {
                if (currencyManager.setCurrency(selectedCurrency)) {
                    onCurrencySelected?.invoke(selectedCurrency)
                    dismiss()
                } else {
                    Toast.makeText(context, "Error setting currency", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please select a valid currency", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    companion object {
        fun newInstance(onCurrencySelected: (String) -> Unit): CurrencySelectionDialog {
            return CurrencySelectionDialog().apply {
                this.onCurrencySelected = onCurrencySelected
            }
        }
    }
} 