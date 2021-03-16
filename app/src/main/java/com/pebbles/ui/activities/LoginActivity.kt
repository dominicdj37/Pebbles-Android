package com.pebbles.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pebbles.R
import com.pebbles.Utils.StringUtils
import com.pebbles.api.model.ErrorCodeParams
import com.pebbles.api.repository.SettingRepository
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

    private fun getSettings() {
        SettingRepository.getEnvironmentSetting().observe(this, Observer {
            checkResponse(it)
        })
    }

    private fun initMotionLayout() {
        progress_signIn?.visibility = View.INVISIBLE
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

            userNameTextInputField?.error = null
            passwordTextInputField?.error = null

            loginMotionLayout?.setTransition(R.id.transitionRegistration)
            loginMotionLayout?.transitionToEnd()
        }

        backToLogin?.setOnClickListener {
            loginButton?.isEnabled = true
            userNameTextInputField?.isEnabled = true
            emailTextInputField?.isEnabled = false
            passwordTextInputField?.isEnabled = true


            userNameTextInputField?.error = null
            passwordTextInputField?.error = null
            emailTextInputField?.error = null

            loginMotionLayout?.setTransition(R.id.transitionBackToSignIn)
            loginMotionLayout?.transitionToEnd()
        }

        completeSignUpButton?.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {


        userNameTextInputField?.error = null
        passwordTextInputField?.error = null
        emailTextInputField?.error = null

        val password = passwordEditText.text.toString()
        val username = userNameEditText.text.toString()
        var email = emailEditText.text.toString()

        if(!StringUtils.isValidEmail(email)) {
            emailTextInputField.error = "Not a valid email"
            return
        }
        progress_signIn?.visibility = View.VISIBLE
        viewModel.signUp(username, password, email).observe(this, Observer {
            checkResponse(response = it, onSuccess = { data ->
                progress_signIn?.visibility = View.INVISIBLE
                showDismissiveAlertDialog("Sucess","Please Sign in to continue")
                emailEditText.setText("")
                backToLogin.callOnClick()
            }, errorCodeParams= {params->
                showErrors(params)
            }, onEnd = {
                progress_signIn?.visibility = View.INVISIBLE
            })
        })


    }

    private fun login() {
        val password = passwordEditText.text.toString()
        val username = userNameEditText.text.toString()

        progress_signIn?.visibility = View.VISIBLE
        viewModel.login(username, password).observe(this, Observer {
            checkResponse(response = it, onSuccess = { data ->
                progress_signIn?.visibility = View.INVISIBLE
                showDismissiveAlertDialog("wonderful","thanks for testing")
                getSettings()
            }, errorCodeParams= {params->
                showErrors(params)
            }, onEnd = {
                progress_signIn?.visibility = View.INVISIBLE
            })
        })
    }

    private fun showErrors(it: ErrorCodeParams?) {
        when (it?.errorType) {
            "invalid_password" -> {
                passwordTextInputField.error = "Password do not match"
            }
            "empty_username" -> {
                userNameTextInputField.error = "Username cannot be empty"
            }
            "invalid_username" -> {
                userNameTextInputField.error = "User not found"
            }
            "empty_password" -> {
                passwordTextInputField.error = "Password cannot be empty"
            }

            "empty_email" -> {
                emailTextInputField.error = "Email cannot be empty"
            }
            "username_taken" -> {
                userNameTextInputField.error = "Username already taken"
            }
        }
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