package com.example.storyapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.example.storyapp.R
import com.example.storyapp.custom_view.CustomDialog
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.viewmodel.RegisterViewModel
import com.example.storyapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var factory: ViewModelFactory
    private val registerViewModel: RegisterViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        factory = ViewModelFactory.getInstance(binding.root.context)

        title = resources.getString(R.string.register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.backButton.setOnClickListener {
            finish()
        }

        handlerRegisterButton()
        buttonEnable()
        setupEditTextListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
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

    private fun handlerError() {
        CustomDialog(this, R.string.pesan_error).show()
    }

    private fun handlerRegister() {
        CustomDialog(this, R.string.success).show()
        binding.tfEmail.text?.clear()
        binding.tfPassword.text?.clear()
        binding.nameTextField.text?.clear()
    }

    private fun handlerRegisterButton() {
        binding.btRegister.setOnClickListener {
            val email = binding.tfEmail.text.toString()
            val password = binding.tfPassword.text.toString()
            val name = binding.nameTextField.text.toString()

            if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name)) {
                registerHandling(name, email, password)
            } else {
                CustomDialog(this, R.string.validation_error).show()
            }
        }
    }

    private fun registerHandling(name: String, email: String, password: String) {
        registerViewModel.postRegister(name, email, password).observe(this@RegisterActivity) { result ->
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
                        handlerLoading(false)
                        handlerRegister()
                    }
                }
            }
        }
    }

    private fun buttonEnable() {
        val emailTextField = binding.tfEmail.text
        val passwordTextField = binding.tfPassword.text
        val nameTextField = binding.nameTextField.text
        binding.btRegister.isEnabled =
            isValidEmail(emailTextField.toString()) && validateMinLength(nameTextField.toString()) && validateMinLength(passwordTextField.toString())
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateMinLength(password: String): Boolean {
        return !TextUtils.isEmpty(password) && password.length >= 8
    }

    private fun setupEditTextListeners() {
        binding.tfEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                buttonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.nameTextField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (validateMinLength(binding.nameTextField.text.toString())) {
                    binding.nameTextField.error = null
                } else {
                    binding.nameTextField.error = getString(R.string.nama_invalid)
                }
                buttonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.tfPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                buttonEnable()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }
}