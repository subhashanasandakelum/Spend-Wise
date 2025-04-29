package com.subhashana.spendwise.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.subhashana.spendwise.R
import com.subhashana.spendwise.data.PasscodeManager
import com.google.android.material.textfield.TextInputEditText

class ChangePasscodeDialog : DialogFragment() {
    private lateinit var passcodeManager: PasscodeManager
    private lateinit var etCurrentPasscode: TextInputEditText
    private lateinit var etNewPasscode: TextInputEditText
    private lateinit var etConfirmPasscode: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_change_passcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passcodeManager = PasscodeManager(requireContext())
        initializeViews(view)
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        etCurrentPasscode = view.findViewById(R.id.etCurrentPasscode)
        etNewPasscode = view.findViewById(R.id.etNewPasscode)
        etConfirmPasscode = view.findViewById(R.id.etConfirmPasscode)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
    }

    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            val currentPasscode = etCurrentPasscode.text.toString()
            val newPasscode = etNewPasscode.text.toString()
            val confirmPasscode = etConfirmPasscode.text.toString()

            when {
                currentPasscode.length != 4 -> {
                    showError(getString(R.string.passcode_length_error))
                }
                !passcodeManager.validatePasscode(currentPasscode) -> {
                    showError(getString(R.string.invalid_current_passcode))
                }
                newPasscode.length != 4 -> {
                    showError(getString(R.string.passcode_length_error))
                }
                newPasscode != confirmPasscode -> {
                    showError(getString(R.string.passcode_mismatch))
                }
                else -> {
                    if (passcodeManager.setPasscode(newPasscode)) {
                        Toast.makeText(context, R.string.passcode_changed, Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        showError(getString(R.string.error_changing_passcode))
                    }
                }
            }
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
} 