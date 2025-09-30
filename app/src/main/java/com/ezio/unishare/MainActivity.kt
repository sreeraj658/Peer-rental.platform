package com.ezio.unishare

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        // --- Find Views ---
        val collegeMailEditText = findViewById<EditText>(R.id.editTextName)
        val collegeMailLayout = findViewById<TextInputLayout>(R.id.textInputLayoutName)
        val passwordEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordLayout = findViewById<TextInputLayout>(R.id.textInputLayoutEmail)
        val joinButton = findViewById<Button>(R.id.buttonJoin)
        val createAccountButton = findViewById<Button>(R.id.buttonCreateAccount)
        val forgotPasswordTextView = findViewById<TextView>(R.id.textViewForgotPassword)
        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim)

        // --- Helper to clear errors ---
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

        // --- Login Button Logic ---
        joinButton.setOnClickListener {
            val email = collegeMailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            var isValid = true

            // --- Input Validation ---
            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

            // --- USE FIREBASE AUTH TO SIGN IN ---
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI and navigate
                        Log.d("AUTH", "signInWithEmail:success")
                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("USER_EMAIL", email)
                        }
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                        finish() // Prevent user from coming back to login screen
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("AUTH", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed. Check credentials.", Toast.LENGTH_SHORT).show()
                        passwordLayout.error = "Invalid email or password"
                        passwordLayout.startAnimation(shakeAnimation)
                    }
                }
        }

        // --- Navigation to other activities ---
        createAccountButton.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        forgotPasswordTextView.setOnClickListener {
            startActivity(Intent(this, ForgetActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}

