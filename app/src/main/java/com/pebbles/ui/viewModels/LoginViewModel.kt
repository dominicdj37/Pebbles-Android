package com.pebbles.ui.viewModels

import android.se.omapi.Session
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pebbles.api.model.ApiResponse
import com.pebbles.api.repository.SessionRepository

class LoginViewModel: ViewModel() {

    fun autoLogin(): MutableLiveData<ApiResponse> {
        return SessionRepository.getInstance().autoLogin()
    }

    fun login(username:String, password:String): MutableLiveData<ApiResponse> {
        return SessionRepository.getInstance().login(username, password)
    }

    fun signUp(username:String, password:String, email:String): MutableLiveData<ApiResponse> {
        return SessionRepository.getInstance().signUp(username, password, email)
    }

}