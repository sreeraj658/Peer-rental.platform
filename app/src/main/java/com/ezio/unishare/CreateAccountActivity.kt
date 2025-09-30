package com.ezio.unishare

import android.content.Intent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ✅ Firebase imports
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var textViewPasswordCriteriaErrorsCreate: TextView

    // Password strength criteria (consistent with SetNewPasswordActivity)
    private val passwordMinLength = 8
    private val hasUpperCasePattern = ".*[A-Z].*".toRegex()
    private val hasLowerCasePattern = ".*[a-z].*".toRegex()
    private val hasDigitPattern = ".*\\d.*".toRegex()
    private val hasSpecialCharPattern =
        ".*[!@#$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>/?].*".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val firstNameLayout = findViewById<TextInputLayout>(R.id.textInputLayoutFirstName)
        val lastNameLayout = findViewById<TextInputLayout>(R.id.textInputLayoutLastName)
        val emailLayout = findViewById<TextInputLayout>(R.id.textInputLayoutCollegeEmail)
        val phoneLayout = findViewById<TextInputLayout>(R.id.textInputLayoutPhone)
        val passwordLayout = findViewById<TextInputLayout>(R.id.textInputLayoutPassword)
        val confirmPasswordLayout =
            findViewById<TextInputLayout>(R.id.textInputLayoutConfirmPassword)

        val firstNameEditText = findViewById<EditText>(R.id.editTextFirstName)
        val lastNameEditText = findViewById<EditText>(R.id.editTextLastName)
        val collegeEmailEditText = findViewById<EditText>(R.id.editTextCollegeEmail)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.editTextConfirmPassword)
        val createAccountButton = findViewById<Button>(R.id.buttonCreateAccountSubmit)

        textViewPasswordCriteriaErrorsCreate =
            findViewById(R.id.textViewPasswordCriteriaErrorsCreate)

        val shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim)

        fun addTextWatcherToClearError(editText: EditText, layout: TextInputLayout) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
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
            } else if (!email.lowercase().endsWith("@tkmce.ac.in")) {
                emailLayout.error = "Please enter a valid TKMCE email"
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

            // --- TODO: Add password and confirm password validation here ---

            // --- If valid, proceed ---
            if (isValid) {
                it.isEnabled = false
                Toast.makeText(this, "Sending OTP...", Toast.LENGTH_SHORT).show()
                sendOtpAndProceed(email, it as Button, firstName, lastName, phone, password)
            } else {
                Toast.makeText(this, "Please correct the errors.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendOtpAndProceed(
        email: String,
        button: Button,
        firstName: String,
        lastName: String,
        phone: String,
        password: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val emailService = EmailService()
                val otp = emailService.sendOtp(email)

                withContext(Dispatchers.Main) {
                    if (otp != null) {
                        Toast.makeText(
                            applicationContext,
                            "OTP Sent! Please check your email.",
                            Toast.LENGTH_LONG
                        ).show()

                        // ✅ Save user data in Firebase Realtime Database
                        val database = FirebaseDatabase.getInstance()
                        val usersRef = database.getReference("users")
                        val key = email.replace(".", "_")
                        val userData = mapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "collegeMail" to email,
                            "phone" to phone,
                            "password" to password // ⚠️ Only for testing — remove in production!
                        )
                        usersRef.child(key).setValue(userData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    applicationContext,
                                    "User data saved to Firebase ✅",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("FirebaseDB", "User data saved successfully")
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    applicationContext,
                                    "Failed to save user data: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                Log.e("FirebaseDB", "Error saving user data", e)
                            }

                        // ✅ Navigate to OTP verification screen
                        val intent =
                            Intent(this@CreateAccountActivity, OtpVerificationActivity::class.java).apply {
                                putExtra("EXTRA_OTP", otp)
                                putExtra("EXTRA_FIRST_NAME", firstName)
                                putExtra("EXTRA_LAST_NAME", lastName)
                                putExtra("EXTRA_EMAIL", email)
                                putExtra("EXTRA_PHONE", phone)
                                putExtra("EXTRA_PASSWORD", password)
                            }
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish()
                    } else {
                        button.isEnabled = true
                        Toast.makeText(
                            applicationContext,
                            "Failed to send OTP. Check internet or logs.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    button.isEnabled = true
                    Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                    e.printStackTrace()
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
