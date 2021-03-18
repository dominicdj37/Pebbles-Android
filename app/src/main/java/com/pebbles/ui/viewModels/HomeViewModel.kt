package com.pebbles.ui.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pebbles.api.model.ApiResponse
import com.pebbles.api.model.Pebble
import com.pebbles.api.model.Pebbles
import com.pebbles.api.repository.PebblesRepository
import com.pebbles.api.repository.SessionRepository

class HomeViewModel: ViewModel() {

    val pebblesLiveData: MutableLiveData<ApiResponse> = MutableLiveData()
    val pebbles: Pebbles?
        get() = PebblesRepository.getInstance().pebbles

    fun getPebbles() {
        val username = SessionRepository.getInstance().user?.username ?: ""
        PebblesRepository.getInstance().getPebbles(username, pebblesLiveData)
    }

}