package com.ezio.unishare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout // Added import

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views from the layout
        val nameEditText = findViewById<EditText>(R.id.editTextName) // This is for College Mail ID
        val emailEditText = findViewById<EditText>(R.id.editTextEmail) // This is for Password

        // Find TextInputLayouts
        val nameTextInputLayout = findViewById<TextInputLayout>(R.id.textInputLayoutName)
        val emailTextInputLayout = findViewById<TextInputLayout>(R.id.textInputLayoutEmail)

        val joinButton = findViewById<Button>(R.id.buttonJoin)
        val createAccountButton = findViewById<Button>(R.id.buttonCreateAccount)

        // Load animations
        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim)

        // Helper function to add TextWatcher for clearing error
        fun addTextWatcherToClearError(editText: EditText, layout: TextInputLayout) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.isNotEmpty() == true && layout.error != null) {
                        layout.error = null // Clear error from TextInputLayout
                    }
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }

        addTextWatcherToClearError(nameEditText, nameTextInputLayout)
        addTextWatcherToClearError(emailEditText, emailTextInputLayout)

        // Set a click listener for the JOIN button
        joinButton.setOnClickListener {
            // Load a new animation instance for this specific click
            val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)
            it.startAnimation(scaleAnimation)

            val collegeMail = nameEditText.text.toString().trim() // Changed variable name for clarity
            val password = emailEditText.text.toString().trim()   // Changed variable name for clarity

            var isValid = true

            // Validation for College Mail ID
            if (collegeMail.isBlank()) {
                nameTextInputLayout.error = "College mail ID is required"
                nameTextInputLayout.startAnimation(shakeAnimation)
                isValid = false
            } else {
                nameTextInputLayout.error = null // Clear error if previously set
            }

            // Validation for Password
            if (password.isBlank()) {
                emailTextInputLayout.error = "Password is required"
                emailTextInputLayout.startAnimation(shakeAnimation)
                isValid = false
            } else {
                emailTextInputLayout.error = null // Clear error if previously set
            }

            if (isValid) {
                Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()
                // TODO: Add actual login logic here
            } else {
                // Optional: A general toast if there are errors, or rely on individual field errors
                // Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Set a click listener for the CREATE ACCOUNT button
        createAccountButton.setOnClickListener {
            // Load another new animation instance for this specific click
            val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim)
            it.startAnimation(scaleAnimation)

            // Start CreateAccountActivity
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}
