package com.pebbles.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pebbles.R
import com.pebbles.ui.viewModels.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {


    lateinit var viewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)


        initMotionLayout()
    }

    private fun initMotionLayout() {

        userNameTextInputField?.isEnabled = false
        emailTextInputField?.isEnabled = false
        passwordTextInputField?.isEnabled = false
        loginButton?.isEnabled = true

        loginButton?.setOnClickListener {

            if(userNameTextInputField?.isEnabled == true) {
                login()
            }

            userNameTextInputField?.isEnabled = true
            passwordTextInputField?.isEnabled = true
            loginMotionLayout?.setTransition(R.id.transitionSignIn)
            loginMotionLayout?.transitionToEnd()
        }

        newUserLabel?.setOnClickListener {
            loginButton?.isEnabled = false
            userNameTextInputField?.isEnabled = true
            emailTextInputField?.isEnabled = true
            passwordTextInputField?.isEnabled = true
            loginMotionLayout?.setTransition(R.id.transitionRegistration)
            loginMotionLayout?.transitionToEnd()
        }

        backToLogin?.setOnClickListener {
            loginButton?.isEnabled = true
            userNameTextInputField?.isEnabled = true
            emailTextInputField?.isEnabled = false
            passwordTextInputField?.isEnabled = true
            loginMotionLayout?.setTransition(R.id.transitionBackToSignIn)
            loginMotionLayout?.transitionToEnd()
        }
    }

    private fun login() {
        val password = "daniel"
        val username = "nissis"


        viewModel.login(username, password).observe(this, Observer {
            checkResponse(response = it, onSuccess = { data ->

            }, errorCodeParams= {

            })
        })
    }

    //region Lifecycle
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}