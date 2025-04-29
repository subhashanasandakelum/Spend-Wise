package com.subhashana.spendwise

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.subhashana.spendwise.data.PasscodeManager

class PasscodeActivity : AppCompatActivity() {
    private lateinit var passcodeManager: PasscodeManager
    private lateinit var etPasscode: EditText
    private lateinit var tvError: TextView
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passcode)

        passcodeManager = PasscodeManager(this)
        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etPasscode = findViewById(R.id.etPasscode)
        tvError = findViewById(R.id.tvError)
        btnSubmit = findViewById(R.id.btnSubmit)
    }

    private fun setupClickListeners() {
        btnSubmit.setOnClickListener {
            val input = etPasscode.text.toString()
            if (input.length == 4) {
                if (passcodeManager.validatePasscode(input)) {
                    startMainActivity()
                } else {
                    showError(getString(R.string.invalid_passcode))
                }
            } else {
                showError(getString(R.string.passcode_length_error))
            }
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
        etPasscode.text.clear()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
} 