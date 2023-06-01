package com.example.storyapp.view


import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils.isEmpty
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import com.example.storyapp.R
import com.example.storyapp.custom_view.CustomDialog
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.model_responses.LoginModel
import com.example.storyapp.model_responses.LoginResponse
import com.example.storyapp.preference.PreferenceLogin
import com.example.storyapp.viewmodel.LoginViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var factory: ViewModelFactory
    private val loginViewModel: LoginViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(binding.root.context)

        supportActionBar?.hide()

        handlerRegister()
        handlerLogin()
        setMyButtonEnable()
        setTextChangedListener()
        playAnimation()
    }


    private fun handlerRegister() {
        binding.btRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handlerLogin() {
        binding.btLogin.setOnClickListener {
            val email = binding.tfEmail.text.toString()
            val password = binding.tfPassword.text.toString()

            if (!isEmpty(email) && !isEmpty(password)) {
                loginHandling(email, password)
            } else {
                CustomDialog(this, R.string.validation_error).show()
            }
        }
    }

    private fun loginHandling(email: String, password: String) {
        loginViewModel.postLogin(email, password).observe(this@LoginActivity) { result ->
            if (result != null) {
                when(result) {
                    is Result.Loading -> {
                        handlerLoading(true)
                    }
                    is Result.Eror -> {
                        handlerLoading(false)
                        handlerError()
                    }
                    is Result.Berhasil -> {
                        handlerSuccess(result.data)
                    }
                }
            }
        }
    }

    private fun handlerLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.root.visibility = View.VISIBLE
            binding.root.visibility = View.GONE
        } else {
            binding.root.visibility = View.GONE
            binding.root.visibility = View.VISIBLE
        }
    }

    private fun handlerSuccess(loginResponse: LoginResponse) {
        simpanDataLogin(loginResponse)
        navigasiKeHome()
    }

    private fun handlerError() {
        CustomDialog(this, R.string.pesan_error).show()
    }

    private fun simpanDataLogin(loginResponse: LoginResponse) {
        val preferenceLogin = PreferenceLogin(this)
        val loginResult = loginResponse.loginResult
        val loginModel = LoginModel(
            name = loginResult?.name, userId = loginResult?.userId, token = loginResult?.token
        )

        preferenceLogin.setLogin(loginModel)
    }

    private fun navigasiKeHome() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun setMyButtonEnable() {
        val emailEditText = binding.tfEmail.text
        val passwordEditText = binding.tfPassword.text
        binding.btLogin.isEnabled =
            isValidEmail(emailEditText.toString()) && validateMinLength(passwordEditText.toString())
    }

    private fun isValidEmail(email: String): Boolean {
        return !isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateMinLength(password: String): Boolean {
        return !isEmpty(password) && password.length >= 8
    }

    private fun setTextChangedListener() {
        binding.tfEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.tfPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setMyButtonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logoLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 2000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}