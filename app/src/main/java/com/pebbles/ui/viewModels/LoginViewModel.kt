package com.pebbles.ui.viewModels

import android.se.omapi.Session
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pebbles.api.model.ApiResponse
import com.pebbles.api.repository.SessionRepository

class LoginViewModel: ViewModel() {

    fun login(username:String, password:String): MutableLiveData<ApiResponse> {
        return SessionRepository.getInstance().login(username, password)
    }

}