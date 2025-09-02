package com.ezio.unishare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

class SetNewPasswordActivity : AppCompatActivity() {

    private lateinit var textInputLayoutNewPassword: TextInputLayout
    private lateinit var textInputLayoutConfirmPassword: TextInputLayout
    private lateinit var editTextNewPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var buttonSetNewPassword: Button
    private lateinit var textViewPasswordCriteriaErrors: TextView

    private lateinit var shakeAnimation: Animation

    private val passwordMinLength = 8
    private val hasUpperCasePattern = ".*[A-Z].*".toRegex()
    private val hasLowerCasePattern = ".*[a-z].*".toRegex()
    private val hasDigitPattern = ".*\\d.*".toRegex()
    // Definitively correct special character pattern
    private val hasSpecialCharPattern = ".*[!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>/?].*".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_new_password)

        textInputLayoutNewPassword = findViewById(R.id.textInputLayoutNewPassword)
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayoutConfirmPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword)
        buttonSetNewPassword = findViewById(R.id.buttonSetNewPassword)
        textViewPasswordCriteriaErrors = findViewById(R.id.textViewPasswordCriteriaErrors)

        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim)

        // Add TextWatcher for New Password
        editTextNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    if (textInputLayoutNewPassword.error != null) {
                        textInputLayoutNewPassword.error = null // Clear error to restore eye icon
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Add TextWatcher for Confirm Password
        editTextConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    if (textInputLayoutConfirmPassword.error != null) {
                        textInputLayoutConfirmPassword.error = null // Clear error to restore eye icon
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        buttonSetNewPassword.setOnClickListener {
            val newPassword = editTextNewPassword.text.toString().trim()
            val confirmPassword = editTextConfirmPassword.text.toString().trim()

            textInputLayoutNewPassword.error = null
            textInputLayoutConfirmPassword.error = null
            textViewPasswordCriteriaErrors.visibility = View.GONE
            textViewPasswordCriteriaErrors.text = ""

            var isValid = true
            val errorMessages = mutableListOf<String>()

            if (newPassword.isEmpty()) {
                textInputLayoutNewPassword.error = "New password cannot be empty"
                textInputLayoutNewPassword.startAnimation(shakeAnimation)
                Toast.makeText(this, "New password cannot be empty", Toast.LENGTH_SHORT).show()
                isValid = false
            } else {
                if (newPassword.length < passwordMinLength) {
                    errorMessages.add("Password must be at least $passwordMinLength characters long.")
                }
                if (!newPassword.matches(hasUpperCasePattern)) {
                    errorMessages.add("Password must contain at least one uppercase letter.")
                }
                if (!newPassword.matches(hasLowerCasePattern)) {
                    errorMessages.add("Password must contain at least one lowercase letter.")
                }
                if (!newPassword.matches(hasDigitPattern)) {
                    errorMessages.add("Password must contain at least one digit.")
                }
                if (!newPassword.matches(hasSpecialCharPattern)) {
                    errorMessages.add("Password must contain at least one special character (e.g., !@#$%^&*).")
                }

                if (errorMessages.isNotEmpty()) {
                    textViewPasswordCriteriaErrors.text = errorMessages.joinToString("\n")
                    textViewPasswordCriteriaErrors.visibility = View.VISIBLE
                    textInputLayoutNewPassword.error = "Please check password criteria above."
                    textInputLayoutNewPassword.startAnimation(shakeAnimation)
                    isValid = false
                }
            }

            if (!isValid) {
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                textInputLayoutConfirmPassword.error = "Confirm password cannot be empty"
                textInputLayoutConfirmPassword.startAnimation(shakeAnimation)
                Toast.makeText(this, "Confirm password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                textInputLayoutConfirmPassword.error = "Passwords do not match"
                textInputLayoutConfirmPassword.startAnimation(shakeAnimation)
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Password successfully set ", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
