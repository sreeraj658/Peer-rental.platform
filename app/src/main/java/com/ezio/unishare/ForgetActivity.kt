package com.ezio.unishare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.Locale
// Import these if you plan to use the example animations
// import android.view.animation.AnimationUtils

class ForgetActivity : AppCompatActivity() {

    private lateinit var editTextOtp: EditText
    private lateinit var buttonVerifyOtp: Button
    private lateinit var textViewResendOtp: TextView
    private lateinit var textViewOtpTimer: TextView

    private var countDownTimer: CountDownTimer? = null
    private val otpTimerDuration = 300000L // 5 minutes in milliseconds (5 * 60 * 1000)
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget)

        editTextOtp = findViewById(R.id.editTextOtp)
        buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp)
        textViewResendOtp = findViewById(R.id.textViewResendOtp)
        textViewOtpTimer = findViewById(R.id.textViewOtpTimer) // Initialize the timer TextView

        // Optional: Load animations if you plan to use them
        // val shake = AnimationUtils.loadAnimation(this, R.anim.shake_anim) // You'd need to create shake_anim.xml
        // val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.button_scale_anim) // You'd need to create button_scale_anim.xml

        buttonVerifyOtp.setOnClickListener {
            // it.startAnimation(scaleAnimation) // Optional animation
            val otp = editTextOtp.text.toString().trim()

            if (otp.isEmpty()) {
                editTextOtp.error = "OTP is required"
                // editTextOtp.startAnimation(shake) // Optional animation
                Toast.makeText(this, "Please enter the OTP.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (otp.length != 6) {
                editTextOtp.error = "OTP must be 6 digits"
                // editTextOtp.startAnimation(shake) // Optional animation
                Toast.makeText(this, "OTP must be 6 digits.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- OTP Verification Logic ---
            // TODO: Implement your actual OTP verification logic here
            Toast.makeText(this, "Verifying OTP: $otp", Toast.LENGTH_LONG).show()

            if (otp == "123456") { 
                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_LONG).show()
                countDownTimer?.cancel() // Stop timer on successful verification
                finish()
            } else {
                Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_LONG).show()
                // editTextOtp.startAnimation(shake) // Optional animation
            }
        }

        textViewResendOtp.setOnClickListener {
            if (!isTimerRunning) {
                // TODO: Implement your actual OTP resend logic here
                // For example, make a network call to your backend to request a new OTP.
                Toast.makeText(this, "Resending OTP...", Toast.LENGTH_SHORT).show()
                startTimer() // Restart the timer
            } else {
                Toast.makeText(this, "Please wait for the current timer to finish.", Toast.LENGTH_SHORT).show()
            }
        }

        startTimer() // Start the timer when the activity is created
    }

    private fun startTimer() {
        countDownTimer?.cancel() // Cancel any existing timer

        isTimerRunning = true
        textViewResendOtp.isEnabled = false // Disable resend while timer is active
        textViewResendOtp.alpha = 0.5f // Visually indicate disabled state

        countDownTimer = object : CountDownTimer(otpTimerDuration, 1000) { // Tick every second
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                textViewOtpTimer.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                isTimerRunning = false
                textViewOtpTimer.text = "00:00"
                textViewResendOtp.isEnabled = true // Re-enable resend
                textViewResendOtp.alpha = 1.0f
                Toast.makeText(this@ForgetActivity, "Timer finished. You can now resend OTP.", Toast.LENGTH_SHORT).show()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel() // Important to prevent memory leaks
    }
}
