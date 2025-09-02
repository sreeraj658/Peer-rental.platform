package com.ezio.unishare

import android.content.Intent // Added for Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var textViewPasswordCriteriaErrorsCreate: TextView

    // Password strength criteria (consistent with SetNewPasswordActivity)
    private val passwordMinLength = 8
    private val hasUpperCasePattern = ".*[A-Z].*".toRegex()
    private val hasLowerCasePattern = ".*[a-z].*".toRegex()
    private val hasDigitPattern = ".*\\d.*".toRegex()
    private val hasSpecialCharPattern = ".*[!@#\$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>/?].*".toRegex()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val firstNameLayout = findViewById<TextInputLayout>(R.id.textInputLayoutFirstName)
        val lastNameLayout = findViewById<TextInputLayout>(R.id.textInputLayoutLastName)
        val emailLayout = findViewById<TextInputLayout>(R.id.textInputLayoutCollegeEmail)
        val phoneLayout = findViewById<TextInputLayout>(R.id.textInputLayoutPhone)
        val passwordLayout = findViewById<TextInputLayout>(R.id.textInputLayoutPassword)
        val confirmPasswordLayout = findViewById<TextInputLayout>(R.id.textInputLayoutConfirmPassword)

        val firstNameEditText = findViewById<EditText>(R.id.editTextFirstName)
        val lastNameEditText = findViewById<EditText>(R.id.editTextLastName)
        val collegeEmailEditText = findViewById<EditText>(R.id.editTextCollegeEmail)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextConfirmPassword)
        val createAccountButton = findViewById<Button>(R.id.buttonCreateAccountSubmit)

        textViewPasswordCriteriaErrorsCreate = findViewById(R.id.textViewPasswordCriteriaErrorsCreate)

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim)

        fun addTextWatcherToClearError(editText: EditText, layout: TextInputLayout) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.isNotEmpty() == true && layout.error != null) {
                        layout.error = null
                        if (editText == passwordEditText) {
                            textViewPasswordCriteriaErrorsCreate.visibility = View.GONE
                        }
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        addTextWatcherToClearError(firstNameEditText, firstNameLayout)
        addTextWatcherToClearError(lastNameEditText, lastNameLayout)
        addTextWatcherToClearError(collegeEmailEditText, emailLayout)
        addTextWatcherToClearError(phoneEditText, phoneLayout)
        addTextWatcherToClearError(passwordEditText, passwordLayout)
        addTextWatcherToClearError(confirmPasswordEditText, confirmPasswordLayout)

        createAccountButton.setOnClickListener {
            val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)
            it.startAnimation(scaleAnimation)

            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = collegeEmailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            var isValid = true

            // --- First Name Validation ---
            if (firstName.isEmpty()) {
                firstNameLayout.error = "First name is required"
                firstNameLayout.startAnimation(shake)
                isValid = false
            } else {
                firstNameLayout.error = null
            }

            // --- Last Name Validation ---
            if (lastName.isEmpty()) {
                lastNameLayout.error = "Last name is required"
                lastNameLayout.startAnimation(shake)
                isValid = false
            } else {
                lastNameLayout.error = null
            }

            // --- College Email Validation ---
            if (email.isEmpty()) {
                emailLayout.error = "College email is required"
                emailLayout.startAnimation(shake)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailLayout.error = "Enter a valid email address"
                emailLayout.startAnimation(shake)
                isValid = false
            } else if (!email.lowercase().endsWith(".in")) { // Current check from your file
                emailLayout.error = "please enter a valid educational email" // Current message
                emailLayout.startAnimation(shake)
                isValid = false
            } else {
                emailLayout.error = null
            }

            // --- Phone Validation ---
            if (phone.isEmpty()) {
                phoneLayout.error = "Phone number is required"
                phoneLayout.startAnimation(shake)
                isValid = false
            } else {
                phoneLayout.error = null
            }

            // --- Password Validation (with strength checks) ---
            textViewPasswordCriteriaErrorsCreate.visibility = View.GONE
            textViewPasswordCriteriaErrorsCreate.text = ""
            val errorMessages = mutableListOf<String>()

            if (password.isEmpty()) {
                passwordLayout.error = "Password is required"
                passwordLayout.startAnimation(shake)
                isValid = false
            } else {
                if (password.length < passwordMinLength) {
                    errorMessages.add("Password must be at least $passwordMinLength characters long.")
                }
                if (!password.matches(hasUpperCasePattern)) {
                    errorMessages.add("Password must contain at least one uppercase letter.")
                }
                if (!password.matches(hasLowerCasePattern)) {
                    errorMessages.add("Password must contain at least one lowercase letter.")
                }
                if (!password.matches(hasDigitPattern)) {
                    errorMessages.add("Password must contain at least one digit.")
                }
                if (!password.matches(hasSpecialCharPattern)) {
                    errorMessages.add("Password must contain at least one special character (e.g., !@#\$%^&*).")
                }

                if (errorMessages.isNotEmpty()) {
                    textViewPasswordCriteriaErrorsCreate.text = errorMessages.joinToString("\\n")
                    textViewPasswordCriteriaErrorsCreate.visibility = View.VISIBLE
                    passwordLayout.error = "Please check password criteria below." // Adjusted message
                    passwordLayout.startAnimation(shake)
                    isValid = false
                } else {
                    passwordLayout.error = null
                }
            }

            // --- Confirm Password Validation ---
            if (password.isEmpty() && confirmPassword.isEmpty()) {
                confirmPasswordLayout.error = null
            } else if (confirmPassword.isEmpty()) {
                confirmPasswordLayout.error = "Confirm password is required"
                confirmPasswordLayout.startAnimation(shake)
                isValid = false
            } else if (password != confirmPassword) {
                confirmPasswordLayout.error = "Passwords do not match"
                confirmPasswordLayout.startAnimation(shake)
                isValid = false
            } else {
                confirmPasswordLayout.error = null
            }


            if (isValid) {
                // TODO: Here you would typically initiate the account creation process on your backend
                // For now, we'll proceed directly to OTP verification.
                // The actual OTP should be sent by your backend after receiving user details.

                Toast.makeText(this, "Proceeding to OTP verification...", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, OtpVerificationActivity::class.java).apply {
                    putExtra(OtpVerificationActivity.EXTRA_VERIFICATION_TARGET, email)
                }
                startActivity(intent)
                // If you want CreateAccountActivity to finish after navigating to OTP, add finish() here.
                // For a typical create account -> OTP flow, you might want this activity to finish.
                // finish()
            } else {
                Toast.makeText(this, "Please correct the errors.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

