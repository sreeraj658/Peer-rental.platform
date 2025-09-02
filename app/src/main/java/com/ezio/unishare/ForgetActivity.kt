package com.ezio.unishare

import android.content.Intent // Added import
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

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
        textViewOtpTimer = findViewById(R.id.textViewOtpTimer)

        buttonVerifyOtp.setOnClickListener {
            val otp = editTextOtp.text.toString().trim()

            if (otp.isEmpty()) {
                editTextOtp.error = "OTP is required"
                Toast.makeText(this, "Please enter the OTP.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (otp.length != 6) {
                editTextOtp.error = "OTP must be 6 digits"
                Toast.makeText(this, "OTP must be 6 digits.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- OTP Verification Logic ---
            // TODO: Implement your actual OTP verification logic here
            // For example, compare with an OTP sent to the user or verify with a backend service.
            // The "123456" is a placeholder for successful verification.
            if (otp == "123456") { // Replace "123456" with your actual OTP success condition
                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_LONG).show()
                countDownTimer?.cancel() // Stop timer on successful verification

                // Navigate to SetNewPasswordActivity
                val intent = Intent(this, SetNewPasswordActivity::class.java)
                // You might want to pass some identifier from the previous screen if needed, e.g.:
                // intent.putExtra("USER_EMAIL", getIntent().getStringExtra("USER_EMAIL_KEY"))
                startActivity(intent)
                finish() // Finish ForgetActivity so the user cannot navigate back to it
            } else {
                Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_LONG).show()
            }
        }

        textViewResendOtp.setOnClickListener {
            if (!isTimerRunning) {
                // TODO: Implement your actual OTP resend logic here
                Toast.makeText(this, "Resending OTP...", Toast.LENGTH_SHORT).show()
                startTimer()
            } else {
                Toast.makeText(this, "Please wait for the current timer to finish.", Toast.LENGTH_SHORT).show()
            }
        }

        startTimer()
    }

    private fun startTimer() {
        countDownTimer?.cancel()

        isTimerRunning = true
        textViewResendOtp.isEnabled = false
        textViewResendOtp.alpha = 0.5f

        countDownTimer = object : CountDownTimer(otpTimerDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                textViewOtpTimer.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                isTimerRunning = false
                textViewOtpTimer.text = "00:00"
                textViewResendOtp.isEnabled = true
                textViewResendOtp.alpha = 1.0f
                Toast.makeText(this@ForgetActivity, "Timer finished. You can now resend OTP.", Toast.LENGTH_SHORT).show()
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
