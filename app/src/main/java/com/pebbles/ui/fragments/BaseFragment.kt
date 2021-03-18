package com.pebbles.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import com.pebbles.R
import com.pebbles.api.model.ApiResponse
import com.pebbles.api.model.Error
import com.pebbles.api.model.ErrorCodeParams
import com.pebbles.api.model.HttpStatusCode
import com.pebbles.core.DatabaseHelper
import com.pebbles.core.Repo
import com.pebbles.core.awaitTransitionComplete
import com.pebbles.data.Device
import com.pebbles.ui.adapters.CommonListAdapter
import com.pebbles.ui.adapters.ChatDataHolder
import com.pebbles.ui.adapters.FriendDataHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.peoples_list_layout.*
import kotlinx.coroutines.*


open class BaseFragment : Fragment() {


    private var actHolder: Activity? =  null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun checkResponse(response: ApiResponse,
                      showError:Boolean = true,
                      onSuccess: ((Any) -> Unit) ? = null,
                      customErrors:((ErrorCodeParams?) -> Unit) ? = null,
                      onFailure: ((Error?) -> Unit) ? = null,
                      onRetry: (() -> Unit) ? = null)
    {
        when {
            response.sucess == true -> {
                onSuccess?.invoke(true)
                return
            }

            response.error?.params != null -> {
                onFailure?.invoke(response.error)
                customErrors?.invoke(response.error?.params)
                return
            }

            response.error?.mCode == HttpStatusCode.notFound -> {
                onFailure?.invoke(response.error)
                if(showError) {
                    showDismissiveAlertDialog("Oops","We could not find what you are looking for!") {
                        onRetry?.invoke()
                    }
                } else {
                    onRetry?.invoke()
                }
            }

            response.error?.mCode == HttpStatusCode.noInternet -> {
                onFailure?.invoke(response.error)
                if(showError) {
                    showDismissiveAlertDialog("Oops","Failed to connect to server. Please try again later.") {
                        onRetry?.invoke()
                    }
                } else {
                    onRetry?.invoke()
                }
            }


            response.error?.mCode == HttpStatusCode.timeout -> {
                onFailure?.invoke(response.error)
                if(showError) {
                    showDismissiveAlertDialog("Oops","The request could not be fulfilled!") {
                        onRetry?.invoke()
                    }
                } else {
                    onRetry?.invoke()
                }
            }

            response.error?.mCode == HttpStatusCode.unauthorized -> {
                handleSessionOut()
                onFailure?.invoke(response.error)
                if(showError) {
                    showDismissiveAlertDialog("Oops","You do not have permission to access this.") {
                        onRetry?.invoke()
                    }
                } else {
                    onRetry?.invoke()
                }
            }

            else -> {
                onFailure?.invoke(response.error)
                if(showError) {
                    showDismissiveAlertDialog("Oops","Something went wrong!") {
                        onRetry?.invoke()
                    }
                } else {
                    onRetry?.invoke()
                }
            }
        }
    }

    private fun handleSessionOut() {
        //todo add sessionOut
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            actHolder = context
        }
    }

    fun showDismissiveAlertDialog(title: String, message: String, onDismiss: () -> Unit? = {}) {
         actHolder?.let {
             val mAlertDialog = AlertDialog.Builder(it).create()
             mAlertDialog.setTitle(title)
             mAlertDialog.setMessage(message)
             mAlertDialog.setCancelable(false)
             mAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ok", Message())
             mAlertDialog.setOnShowListener {
                 val positive = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                 positive?.transformationMethod = null

                 positive?.setOnClickListener {
                     mAlertDialog.dismiss()
                     onDismiss.invoke()
                 }
             }
             mAlertDialog.show()
        }
    }


}