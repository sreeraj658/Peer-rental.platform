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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var textViewPasswordCriteriaErrorsCreate: TextView

    // Password strength criteria
    private val passwordMinLength = 8
    private val hasUpperCasePattern = ".*[A-Z].*".toRegex()
    private val hasLowerCasePattern = ".*[a-z].*".toRegex()
    private val hasDigitPattern = ".*\\d.*".toRegex()
    private val hasSpecialCharPattern = ".*[!@#\$%^&*()_+\\-=\\[\\]{};':\\\"\\\\|,.<>/?].*".toRegex()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        firebaseAuth = FirebaseAuth.getInstance()

        // --- Find Views ---
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

        // --- Add Text Watchers to clear errors ---
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
            // --- Assume your validation logic is here and sets isValid correctly ---
            // For brevity, the full validation logic is omitted, but it should check all fields.

            // --- If validation passes, create the user ---
            if (isValid) {
                // Disable button to prevent multiple clicks
                it.isEnabled = false
                Toast.makeText(this, "Creating Account...", Toast.LENGTH_SHORT).show()

                // --- NEW AUTHENTICATION AND DATABASE LOGIC ---
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("AUTH", "Firebase Auth user created successfully.")
                            // Now save the user's profile information to the Realtime Database
                            saveUserDetailsToDatabase(firstName, lastName, email, phone)

                            // Send them to the login screen to sign in for the first time
                            Toast.makeText(this, "Account created successfully! Please log in.", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, MainActivity::class.java).apply {
                                // Clear all previous activities from the stack
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()

                        } else {
                            // If user creation fails, show an error and re-enable the button
                            Log.w("AUTH", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Account creation failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            it.isEnabled = true
                        }
                    }
            } else {
                Toast.makeText(this, "Please correct the errors above.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserDetailsToDatabase(firstName: String, lastName: String, email: String, phone: String) {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        // Use the email as a key, replacing characters that are invalid in Firebase keys
        val key = email.replace(".", "_")

        // Create a map of user data. NOTICE: NO PASSWORD IS SAVED.
        val userData = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "collegeMail" to email, // The field name in your DB
            "phone" to phone
        )

        usersRef.child(key).setValue(userData)
            .addOnSuccessListener {
                Log.d("DB_SAVE", "User profile saved to Realtime Database.")
            }
            .addOnFailureListener { e ->
                Log.e("DB_SAVE", "Failed to save user profile.", e)
            }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

