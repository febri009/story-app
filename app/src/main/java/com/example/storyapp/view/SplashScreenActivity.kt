package com.example.storyapp.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.model_responses.LoginModel
import com.example.storyapp.preference.PreferenceLogin


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private var delay = 700
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var mPreferenceLogin: PreferenceLogin
    private lateinit var loginModel: LoginModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mPreferenceLogin = PreferenceLogin(this)
        loginModel = mPreferenceLogin.getUser()

        if (loginModel.name != null && loginModel.userId != null && loginModel.token != null) {
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            navigate(intent)
        } else {
            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
            navigate(intent)
        }

    }

    private fun navigate(intent: Intent) {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
            finish()
        }, delay.toLong())
    }
}