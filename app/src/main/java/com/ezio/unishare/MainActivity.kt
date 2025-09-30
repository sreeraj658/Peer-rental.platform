package com.ezio.unishare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import android.util.Log

/**
 * MainActivity
 * ------------
 * Handles login using Realtime Database.
 * Checks email and password stored under "users" node.
 * Shows proper messages for invalid email/password.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ------------------ Find Views ------------------
        val collegeMailEditText = findViewById<EditText>(R.id.editTextName)
        val collegeMailLayout = findViewById<TextInputLayout>(R.id.textInputLayoutName)
        val passwordEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordLayout = findViewById<TextInputLayout>(R.id.textInputLayoutEmail)
        val joinButton = findViewById<Button>(R.id.buttonJoin)
        val createAccountButton = findViewById<Button>(R.id.buttonCreateAccount)
        val forgotPasswordTextView = findViewById<TextView>(R.id.textViewForgotPassword)
        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim)

        // ------------------ Helper: Clear error while typing ------------------
        fun addTextWatcherToClearError(editText: EditText, layout: TextInputLayout) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.isNotEmpty() == true && layout.error != null) {
                        layout.error = null
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        addTextWatcherToClearError(collegeMailEditText, collegeMailLayout)
        addTextWatcherToClearError(passwordEditText, passwordLayout)

        // ------------------ LOGIN BUTTON ------------------
        joinButton.setOnClickListener {
            val email = collegeMailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            var isValid = true

            // ------------------ Input Validation ------------------
            if (email.isBlank()) {
                collegeMailLayout.error = "College mail ID is required"
                collegeMailLayout.startAnimation(shakeAnimation)
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                collegeMailLayout.error = "Enter a valid email address"
                collegeMailLayout.startAnimation(shakeAnimation)
                isValid = false
            } else {
                collegeMailLayout.error = null
            }

            if (password.isBlank()) {
                passwordLayout.error = "Password is required"
                passwordLayout.startAnimation(shakeAnimation)
                isValid = false
            } else {
                passwordLayout.error = null
            }

            if (!isValid) return@setOnClickListener

            // ------------------ Check Realtime Database ------------------
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val emailKey = email.replace(".", "_") // Replace "." to match key format

            usersRef.child(emailKey).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val storedPassword = snapshot.child("password").getValue(String::class.java)
                    if (storedPassword == password) {
                        Toast.makeText(this, "Login successful ✅", Toast.LENGTH_SHORT).show()
                        Log.d("Login", "User $email logged in successfully")
                        // TODO: Navigate to HomeActivity or Main content
                        // Example:
                        // startActivity(Intent(this, HomeActivity::class.java))
                    } else {
                        passwordLayout.error = "Invalid password!"
                        passwordLayout.startAnimation(shakeAnimation)
                        Toast.makeText(this, "Invalid password!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    collegeMailLayout.error = "Invalid email!"
                    collegeMailLayout.startAnimation(shakeAnimation)
                    Toast.makeText(this, "Invalid email!", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Database error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("Login", "Error reading DB", e)
            }
        }

        // ------------------ CREATE ACCOUNT ------------------
        createAccountButton.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        // ------------------ FORGOT PASSWORD ------------------
        forgotPasswordTextView.setOnClickListener {
            startActivity(Intent(this, ForgetActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}
