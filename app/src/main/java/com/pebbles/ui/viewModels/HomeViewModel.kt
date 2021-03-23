package com.pebbles.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pebbles.Utils.SessionUtils
import com.pebbles.api.model.ApiResponse
import com.pebbles.api.model.Pebble
import com.pebbles.api.model.Pebbles
import com.pebbles.api.repository.NotificationRepository
import com.pebbles.api.repository.PebblesRepository
import com.pebbles.api.repository.SessionRepository
import com.pebbles.core.sessionUtils

class HomeViewModel: ViewModel() {

    val pebblesLiveData: MutableLiveData<ApiResponse> = MutableLiveData()
    val pebbles: Pebbles?
        get() = PebblesRepository.getInstance().pebbles

    fun getPebbles() {
        val username = SessionRepository.getInstance().user?.username ?: ""
        PebblesRepository.getInstance().getPebbles(username, pebblesLiveData)
    }

    fun connectNewPebble(name: String, key: String) {
        PebblesRepository.getInstance().connectNewPebble(name, key, pebblesLiveData)
    }

    fun sharePebble(id: Int, username: String) {
        PebblesRepository.getInstance().sharePebble(id, username, pebblesLiveData)
    }


    fun registerToken(token:String): MutableLiveData<ApiResponse> {
        return NotificationRepository.getInstance().registerToken(token)
    }

    fun deRegisterToken(): MutableLiveData<ApiResponse> {
        val token = sessionUtils.getLastDeviceToken() ?: SessionRepository.getInstance().user?.mobileDevice?.last()?.token
        return if (!token.isNullOrBlank()) {
            NotificationRepository.getInstance().deRegisterToken(token!!)
        } else {
            MutableLiveData(ApiResponse(true))
        }
    }

}