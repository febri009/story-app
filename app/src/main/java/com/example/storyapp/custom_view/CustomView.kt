package com.example.storyapp.custom_view

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class CustomDialog(
    context: Context,
    private val message: Int,
    private val action: (() -> Unit)? = null
): AlertDialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.error_layout)

        val errorView = findViewById<TextView>(R.id.errorTextView)
        errorView.text = context.getString(message)

        val button = findViewById<Button>(R.id.dismissButton)
        button.setOnClickListener {
            dismiss()
            action?.invoke()
        }

        setCancelable(false)
    }
}

class CustomButton : MaterialButton {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setBackgroundResource(R.drawable.bg_button)
        setTextColor(ContextCompat.getColor(context, android.R.color.background_light))
    }
}

class CustomTextField : TextInputEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        addTextChangedListener(cekEmail)
    }

    private val cekEmail = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(text: Editable?) {
            error = if (emailValid(text.toString())) {
                null
            } else {
                context.getString(R.string.email_invalid)
            }
        }
    }

    private fun emailValid (email: String): Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

class CustomPassword : TextInputEditText {

    private val minPassword = 8

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }


    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(text: Editable?) {
                if (text.toString().length >= minPassword) {
                    this@CustomPassword.error = null
                } else {
                    this@CustomPassword.error = context.getString(R.string.pw_invalid)
                }
            }
        })
    }
}



