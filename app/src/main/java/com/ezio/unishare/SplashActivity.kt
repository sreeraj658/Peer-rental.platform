package com.ezio.unishare

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Make activity full-screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        Handler(Looper.getMainLooper()).postDelayed({
            // --- THIS IS THE KEY LOGIC ---
            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser

            if (currentUser != null) {
                // User is already logged in, go directly to HomeActivity
                val intent = Intent(this@SplashActivity, HomeActivity::class.java).apply {
                    // Pass the already logged-in user's email
                    putExtra("USER_EMAIL", currentUser.email)
                }
                startActivity(intent)
            } else {
                // No user is logged in, go to the MainActivity (Login Screen)
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }
            
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish() // Call finish to remove SplashActivity from the back stack
        }, splashTimeOut)
    }
}
