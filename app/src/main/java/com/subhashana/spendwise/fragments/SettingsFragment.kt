package com.subhashana.spendwise.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.subhashana.spendwise.MainActivity
import com.subhashana.spendwise.R
import com.subhashana.spendwise.data.TransactionManager
import com.subhashana.spendwise.dialog.BackupRestoreDialog
import com.subhashana.spendwise.dialog.ChangePasscodeDialog
import com.subhashana.spendwise.dialog.CurrencySelectionDialog
import com.subhashana.spendwise.dialog.SetBudgetDialog
import com.subhashana.spendwise.dialog.ThemeSelectionDialog
import com.subhashana.spendwise.service.ReminderService
import android.widget.Button
import android.widget.Toast
import com.google.android.material.switchmaterial.SwitchMaterial
import android.content.Context

class SettingsFragment : Fragment() {
    private lateinit var transactionManager: TransactionManager
    private lateinit var btnBackupRestore: Button
    private lateinit var btnSetBudget: Button
    private lateinit var btnChangePasscode: Button
    private lateinit var btnChangeCurrency: Button
    private lateinit var btnChangeTheme: Button
    private lateinit var switchDailyReminder: SwitchMaterial

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        
        transactionManager = (activity as MainActivity).getTransactionManager()
        initializeViews(view)
        setupClickListeners()
        
        return view
    }

    private fun initializeViews(view: View) {
        btnBackupRestore = view.findViewById(R.id.btnBackupRestore)
        btnSetBudget = view.findViewById(R.id.btnSetBudget)
        btnChangePasscode = view.findViewById(R.id.btnChangePasscode)
        btnChangeCurrency = view.findViewById(R.id.btnChangeCurrency)
        btnChangeTheme = view.findViewById(R.id.btnChangeTheme)
        switchDailyReminder = view.findViewById(R.id.switchDailyReminder)
        
        // Restore the switch state
        val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        switchDailyReminder.isChecked = sharedPreferences.getBoolean("daily_reminder_enabled", false)
    }

    private fun setupClickListeners() {
        btnBackupRestore.setOnClickListener {
            showBackupRestoreDialog()
        }

        btnSetBudget.setOnClickListener {
            showSetBudgetDialog()
        }

        btnChangePasscode.setOnClickListener {
            showChangePasscodeDialog()
        }

        btnChangeCurrency.setOnClickListener {
            showCurrencySelectionDialog()
        }

        btnChangeTheme.setOnClickListener {
            showThemeSelectionDialog()
        }

        switchDailyReminder.setOnCheckedChangeListener { _, isChecked ->
            // Save the switch state
            val sharedPreferences = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("daily_reminder_enabled", isChecked).apply()
            
            val intent = Intent(requireContext(), ReminderService::class.java).apply {
                action = if (isChecked) {
                    ReminderService.ACTION_START_REMINDER
                } else {
                    ReminderService.ACTION_STOP_REMINDER
                }
            }
            requireContext().startService(intent)
        }
    }

    private fun showChangePasscodeDialog() {
        ChangePasscodeDialog().show(parentFragmentManager, "ChangePasscodeDialog")
    }

    private fun showSetBudgetDialog() {
        SetBudgetDialog.newInstance { budget ->
            try {
                transactionManager.setMonthlyBudget(budget)
                (activity as MainActivity).updateUI()
                Toast.makeText(context, "Monthly budget set successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error setting budget: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.show(parentFragmentManager, "SetBudgetDialog")
    }

    private fun showCurrencySelectionDialog() {
        CurrencySelectionDialog.newInstance { currency ->
            (activity as MainActivity).updateUI()
            Toast.makeText(context, "Currency changed to $currency", Toast.LENGTH_SHORT).show()
        }.show(parentFragmentManager, "CurrencySelectionDialog")
    }

    private fun showThemeSelectionDialog() {
        ThemeSelectionDialog().show(parentFragmentManager, "ThemeSelectionDialog")
    }

    private fun showBackupRestoreDialog() {
        BackupRestoreDialog.newInstance(
            onBackupCreated = { backupPath ->
                // Backup created successfully
                Toast.makeText(context, "Backup created successfully", Toast.LENGTH_SHORT).show()
            },
            onBackupRestored = { backupData ->
                try {
                    // Clear existing data
                    transactionManager.clearAllData()
                    
                    // Restore transactions
                    backupData.transactions.forEach { transaction ->
                        transactionManager.saveTransaction(transaction)
                    }
                    
                    // Restore budget
                    transactionManager.setMonthlyBudget(backupData.monthlyBudget)
                    (activity as MainActivity).updateUI()
                    Toast.makeText(context, "Backup restored successfully", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error restoring backup: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        ).show(parentFragmentManager, "BackupRestoreDialog")
    }
} 