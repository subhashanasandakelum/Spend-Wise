package com.subhashana.spendwise.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.subhashana.spendwise.R
import com.subhashana.spendwise.data.ThemeManager

class ThemeSelectionDialog : DialogFragment() {
    private lateinit var themeManager: ThemeManager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        themeManager = ThemeManager(requireContext())
        
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_theme_selection, null)
        
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroupTheme)
        val radioSystem = view.findViewById<RadioButton>(R.id.radioSystem)
        val radioLight = view.findViewById<RadioButton>(R.id.radioLight)
        val radioDark = view.findViewById<RadioButton>(R.id.radioDark)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        // Set the current theme selection
        when (themeManager.getThemeMode()) {
            ThemeManager.MODE_SYSTEM -> radioSystem.isChecked = true
            ThemeManager.MODE_LIGHT -> radioLight.isChecked = true
            ThemeManager.MODE_DARK -> radioDark.isChecked = true
        }

        btnSave.setOnClickListener {
            val selectedTheme = when (radioGroup.checkedRadioButtonId) {
                R.id.radioSystem -> ThemeManager.MODE_SYSTEM
                R.id.radioLight -> ThemeManager.MODE_LIGHT
                R.id.radioDark -> ThemeManager.MODE_DARK
                else -> ThemeManager.MODE_SYSTEM
            }
            themeManager.setThemeMode(selectedTheme)
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(view)
        return builder.create().apply {
            // Remove the default dialog buttons
            setOnShowListener { dialog ->
                (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)?.visibility = android.view.View.GONE
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.visibility = android.view.View.GONE
            }
        }
    }
} 