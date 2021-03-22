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
import com.pebbles.core.sessionUtils
import com.pebbles.ui.viewModels.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login_new.*
import kotlinx.android.synthetic.main.activity_login_new.loginButton
import kotlinx.android.synthetic.main.activity_login_new.newUserLabel
import kotlinx.android.synthetic.main.activity_login_new.passwordEditText
import kotlinx.android.synthetic.main.activity_login_new.passwordTextInputField
import kotlinx.android.synthetic.main.activity_login_new.userNameEditText
import kotlinx.android.synthetic.main.activity_login_new.userNameTextInputField
import kotlinx.coroutines.*

class LoginActivity : BaseActivity() {


    lateinit var viewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_new)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        progress_bar?.visibility = View.VISIBLE
        tryAutoLogin() {
            setLoginUi()
        }

    }

    private fun tryAutoLogin(askLogin: () -> Unit) {

        viewModel.autoLogin().observe(this, Observer {response ->
            checkResponse(response = response,
                customErrors = { params->
                    params?.errorType?.takeIf{ it == "unauthorized_user"}?.let {
                        askLogin.invoke()
                    }
                }, onSuccess = {
                    navigateToHome()
                    finish()
                }, onRetry = {
                    tryAutoLogin(askLogin)
                })

        })
    }

    private fun initMotionLayout() {
    }

    private fun setLoginUi() {
        loginNewMotionLayout?.setTransition(R.id.startToSignInTransition)
        loginNewMotionLayout?.transitionToEnd()
        progress_bar?.visibility = View.INVISIBLE

        loginButton?.setOnClickListener {
            login()
        }

        newUserLabel?.setOnClickListener {
            progress_bar?.visibility = View.INVISIBLE

            emailSignUpTextInputField?.error = null
            userNameSignUpEditText?.error = null
            passwordSignUpTextInputField?.error = null

            emailSignUpEditText?.setText("")
            userNameSignUpEditText?.setText("")
            passwordSignUpEditText?.setText("")

            loginNewMotionLayout?.setTransition(R.id.signIntoSignUpTransition)
            loginNewMotionLayout?.transitionToEnd()

        }

        backToLoginLabel?.setOnClickListener {
            progress_bar?.visibility = View.INVISIBLE

            userNameTextInputField?.error = null
            passwordTextInputField?.error = null
            loginNewMotionLayout?.setTransition(R.id.signIntoSignUpTransition)
            loginNewMotionLayout?.transitionToStart()

        }

        signUpButton?.setOnClickListener {
            signUp()
        }

    }

    private fun signUp() {

        userNameTextInputField?.error = null
        passwordTextInputField?.error = null


        emailSignUpTextInputField?.error = null
        userNameSignUpTextInputField?.error = null
        passwordSignUpTextInputField?.error = null

        passwordEditText.setText("test")
        userNameEditText.setText("daniel")


        val email = emailSignUpEditText?.text.toString()
        val username = userNameSignUpEditText?.text.toString()
        val password = passwordSignUpEditText?.text.toString()

        if(!StringUtils.isValidEmail(email)) {
            emailSignUpTextInputField.error = "Not a valid email"
            return
        }

        signUpButton?.isEnabled = false

        progress_bar?.visibility = View.VISIBLE

        viewModel.signUp(username, password, email).observe(this, Observer {
            checkResponse(response = it, onSuccess = { data ->
                showDismissiveAlertDialog("Sucess","Please Sign in to continue")

                //set username and email for ease
                userNameSignUpEditText?.text?.toString()?.let {username ->
                    userNameEditText?.setText(username)
                }
                passwordSignUpEditText?.text?.toString()?.let {password->
                    passwordEditText?.setText(password)
                }

                progress_bar?.visibility = View.INVISIBLE
                signUpButton?.isEnabled = true

                backToLoginLabel.callOnClick()

            }, customErrors= { params->
                showErrorsForSignUp(params)
            }, onFailure = {
                progress_bar?.visibility = View.INVISIBLE
                signUpButton?.isEnabled = true
            })
        })


    }

    private fun login() {

        userNameTextInputField?.error = null
        passwordTextInputField?.error = null



        val password = passwordEditText.text.toString()
        val username = userNameEditText.text.toString()

        progress_bar?.visibility = View.VISIBLE

        loginButton?.isEnabled = false
        viewModel.login(username, password).observe(this, Observer {
            checkResponse(response = it, onSuccess = { data ->
                navigateToHome()
                //getSettings()
            }, customErrors= { params->
                showErrorsForLogin(params)
            }, onFailure = {
                progress_bar?.visibility = View.INVISIBLE
                loginButton?.isEnabled = true
            })
        })
    }

    private fun showErrorsForSignUp(it: ErrorCodeParams?) {
        when (it?.errorType) {
            "invalid_password" -> {
                passwordSignUpTextInputField?.error = "Password do not match"
            }
            "empty_username" -> {
                userNameSignUpTextInputField?.error = "Username cannot be empty"
            }
            "invalid_username" -> {
                userNameSignUpTextInputField?.error = "User not found"
            }
            "empty_password" -> {
                passwordSignUpTextInputField?.error = "Password cannot be empty"
            }

            "empty_email" -> {
                emailSignUpTextInputField?.error = "Email cannot be empty"
            }
            "username_taken" -> {
                userNameSignUpTextInputField?.error = "Username already taken"
            }
        }
    }

    private fun showErrorsForLogin(it: ErrorCodeParams?) {
        when (it?.errorType) {
            "invalid_password" -> {
                passwordTextInputField?.error = "Password do not match"
            }
            "empty_username" -> {
                userNameTextInputField?.error = "Username cannot be empty"
            }
            "invalid_username" -> {
                userNameTextInputField?.error = "User not found"
            }
            "empty_password" -> {
                passwordTextInputField?.error = "Password cannot be empty"
            }

            "empty_email" -> {
                emailTextInputField?.error = "Email cannot be empty"
            }
            "username_taken" -> {
                userNameTextInputField?.error = "Username already taken"
            }
        }
    }


    private fun getSettings() {
        SettingRepository.getEnvironmentSetting().observe(this, Observer {
            checkResponse(it)
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