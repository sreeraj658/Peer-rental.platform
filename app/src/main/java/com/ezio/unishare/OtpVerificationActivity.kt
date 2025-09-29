package com.ezio.unishare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class OtpVerificationActivity : AppCompatActivity() {

    private lateinit var editTextOtp: EditText
    private lateinit var buttonVerifyOtp: Button
    private lateinit var textViewResendOtp: TextView
    private lateinit var textViewOtpTimer: TextView
    private lateinit var textViewInstruction: TextView

    private var countDownTimer: CountDownTimer? = null
    private val otpTimerDuration = 5 * 60 * 1000L // 5 minutes in milliseconds
    private var isTimerRunning = false
    private var timeRemainingInMillis = otpTimerDuration

    // Key for receiving data like email from CreateAccountActivity
    companion object {
        const val EXTRA_VERIFICATION_TARGET = "com.ezio.unishare.EXTRA_VERIFICATION_TARGET"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp_verification)

        editTextOtp = findViewById(R.id.editTextOtp)
        buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp)
        textViewResendOtp = findViewById(R.id.textViewResendOtp)
        textViewOtpTimer = findViewById(R.id.textViewOtpTimer)
        textViewInstruction = findViewById(R.id.textViewForgetInstruction) // Using the same ID

        val verificationTarget = intent.getStringExtra(EXTRA_VERIFICATION_TARGET)
        if (!verificationTarget.isNullOrEmpty()) {
            textViewInstruction.text = "Enter the OTP sent to $verificationTarget to verify your account."
        } else {
            // Default text if no target is passed (though it's better to always pass it)
            textViewInstruction.text = "Enter the OTP sent to your registered email/phone to verify your account."
        }

        val shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_anim)

        startOtpTimer(otpTimerDuration)

        buttonVerifyOtp.setOnClickListener {
            val otp = editTextOtp.text.toString().trim()

            if (otp.isEmpty()) {
                editTextOtp.error = "OTP cannot be empty"
                editTextOtp.startAnimation(shakeAnimation)
                Toast.makeText(this, "Please enter the OTP.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (otp.length != 6) {
                editTextOtp.error = "OTP must be 6 digits"
                editTextOtp.startAnimation(shakeAnimation)
                Toast.makeText(this, "OTP must be 6 digits.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- Placeholder OTP Verification Logic ---
            // TODO: Replace with your actual OTP verification logic (e.g., API call)
            if (otp == "123456") { // Example OTP
                Toast.makeText(this, "Account Verified Successfully!", Toast.LENGTH_LONG).show()
                countDownTimer?.cancel()
                isTimerRunning = false
                // TODO: Define next step, e.g., navigate to login or main app screen, or finish with a result.
                // For now, just finish and assume CreateAccountActivity will handle the next step
                // or that CreateAccountActivity has already finished and this leads to login.
                // If CreateAccountActivity is still in the back stack and should be closed:
                // Intent(this, LoginActivity::class.java).apply {
                //    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                //    startActivity(this)
                // }
                finish() // Finishes OtpVerificationActivity
            } else {
                editTextOtp.error = "Invalid OTP"
                editTextOtp.startAnimation(shakeAnimation)
                Toast.makeText(this, "Invalid OTP. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        textViewResendOtp.setOnClickListener {
            if (!isTimerRunning) {
                // TODO: Implement logic to actually resend OTP via your backend
                Toast.makeText(this, "Resending OTP...", Toast.LENGTH_SHORT).show()
                editTextOtp.text.clear()
                editTextOtp.error = null
                startOtpTimer(otpTimerDuration) // Restart timer
            } else {
                Toast.makeText(this, "Please wait for the current timer to finish.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startOtpTimer(duration: Long) {
        countDownTimer?.cancel() // Cancel any existing timer
        timeRemainingInMillis = duration
        textViewResendOtp.isEnabled = false // Disable resend until timer finishes
        textViewResendOtp.alpha = 0.5f      // Visually indicate disabled

        countDownTimer = object : CountDownTimer(timeRemainingInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemainingInMillis = millisUntilFinished
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
                Toast.makeText(this@OtpVerificationActivity, "OTP expired. Please request a new one.", Toast.LENGTH_SHORT).show()
                textViewInstruction.text = "OTP Expired. Click Resend." // Update instruction
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    // Handle back press to match ForgetActivity (if desired)
    // override fun onBackPressed() {
    //     super.onBackPressed()
    //     // Or navigate to a specific screen like LoginActivity
    //     // Intent(this, LoginActivity::class.java).also {
    //     //     it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    //     //     startActivity(it)
    //     // }
    //     // finish()
    // }

    // Adding the finish animation consistent with other activities
    override fun finish() {
        super.finish()
        // Assuming you have these animations. If not, this might cause an error or do nothing.
        // Check if these are the correct animations for your desired transition.
        // For OTP screen, usually, it slides out to reveal the previous screen or a new one.
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
