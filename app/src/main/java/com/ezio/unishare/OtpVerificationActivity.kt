package com.ezio.unishare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class OtpVerificationActivity : AppCompatActivity() {

    // These variables are declared to hold your UI components
    private lateinit var editTextOtp: EditText
    private lateinit var buttonVerifyOtp: Button
    private lateinit var textViewResendOtp: TextView
    private lateinit var textViewOtpTimer: TextView
    private lateinit var textViewInstruction: TextView

    private var countDownTimer: CountDownTimer? = null
    private val otpTimerDuration = 5 * 60 * 1000L // 5 minutes
    private var isTimerRunning = false
    private var correctOtp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the content view to your existing XML layout file
        setContentView(R.layout.activity_otp_verification)

        // --- 1. Find views using the IDs FROM YOUR XML LAYOUT ---
        // I have updated these IDs to match your code
        editTextOtp = findViewById(R.id.editTextOtp)
        buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp)
        textViewResendOtp = findViewById(R.id.textViewResendOtp)
        textViewOtpTimer = findViewById(R.id.textViewOtpTimer)
        textViewInstruction = findViewById(R.id.textViewForgetInstruction) // Using your ID

        // --- 2. Receive all data passed from CreateAccountActivity ---
        correctOtp = intent.getStringExtra("EXTRA_OTP")
        val firstName = intent.getStringExtra("EXTRA_FIRST_NAME")
        val lastName = intent.getStringExtra("EXTRA_LAST_NAME")
        val email = intent.getStringExtra("EXTRA_EMAIL")
        val phone = intent.getStringExtra("EXTRA_PHONE")
        val password = intent.getStringExtra("EXTRA_PASSWORD")

        Log.d("OtpVerification", "Correct OTP received: $correctOtp")

        // Update the instruction text with the user's email
        if (!email.isNullOrEmpty()) {
            textViewInstruction.text = "Enter the OTP sent to $email to verify your account."
        }

        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim)
        startOtpTimer(otpTimerDuration)

        // --- 3. Verify Button Logic ---
        buttonVerifyOtp.setOnClickListener {
            val enteredOtp = editTextOtp.text.toString().trim()

            if (enteredOtp.length != 6) {
                editTextOtp.error = "OTP must be 6 digits"
                editTextOtp.startAnimation(shakeAnimation)
                return@setOnClickListener
            }

            // --- REAL OTP VERIFICATION LOGIC ---
            if (enteredOtp == correctOtp) {
                Toast.makeText(this, "Account Verified Successfully!", Toast.LENGTH_LONG).show()
                countDownTimer?.cancel()


                Log.d("OtpVerification", "SUCCESS! User to be created: $firstName, $email")
                // Here, you would call your Firebase or SQLite database manager to create the new user.

                // After saving, navigate to the main part of your app or login screen
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            } else {
                editTextOtp.error = "Invalid OTP"
                editTextOtp.startAnimation(shakeAnimation)
                Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        // --- 4. Resend OTP Logic ---
        textViewResendOtp.setOnClickListener {
            if (!isTimerRunning && !email.isNullOrEmpty()) {
                Toast.makeText(this, "Resending OTP...", Toast.LENGTH_SHORT).show()
                resendOtpInBackground(email)
            } else {
                Toast.makeText(this, "Please wait for the timer to finish.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resendOtpInBackground(email: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val emailService = EmailService()
            val newOtp = emailService.sendOtp(email)
            withContext(Dispatchers.Main) {
                if (newOtp != null) {
                    correctOtp = newOtp // Update the correct OTP
                    Log.d("OtpVerification", "New OTP sent and updated: $correctOtp")
                    Toast.makeText(applicationContext, "A new OTP has been sent.", Toast.LENGTH_SHORT).show()
                    editTextOtp.text.clear()
                    editTextOtp.error = null
                    startOtpTimer(otpTimerDuration) // Restart timer
                } else {
                    Toast.makeText(applicationContext, "Failed to resend OTP.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startOtpTimer(duration: Long) {
        countDownTimer?.cancel()
        textViewResendOtp.isEnabled = false
        textViewResendOtp.alpha = 0.5f

        countDownTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                isTimerRunning = true
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                textViewOtpTimer.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }
            override fun onFinish() {
                isTimerRunning = false
                textViewOtpTimer.text = "00:00"
                textViewResendOtp.isEnabled = true
                textViewResendOtp.alpha = 1.0f
                Toast.makeText(this@OtpVerificationActivity, "OTP expired.", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}

