package com.pebbles.core

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Handler
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pebbles.ui.Custom.MultiListenerMotionLayout
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout


class Run {
    companion object {
        fun after(delay: Long, process: () -> Unit) {
            Handler().postDelayed({
                process()
            }, delay)
        }
    }
}



@SuppressLint("CheckResult")
fun ImageView?.assignImageFromUrl(
    fileUrl: String?, fitCenter: Boolean = false, centerCrop: Boolean = false, centerInside: Boolean = false,
    placeHolder: Int? = null, placeHolderDrawable: Drawable? = null, override: Pair<Int, Int>? = null, isCircleCrop: Boolean = false, bgColor: Int? = null
) {
    val imageView = this
    if (this != null) {
        val requestManager = Glide.with(this.context).load(fileUrl)

        when {
            placeHolder != null -> requestManager.placeholder(placeHolder)
            placeHolderDrawable != null -> requestManager.placeholder(placeHolderDrawable)
        }

        when {
            fitCenter -> requestManager.fitCenter()
            centerCrop -> requestManager.centerCrop()
            centerInside -> requestManager.centerInside()
        }

        if (override != null) {
            requestManager.override(override.first, override.second)
        }

        if (isCircleCrop) {
            requestManager.apply(RequestOptions.circleCropTransform())
        }

        requestManager.into(this)

    }
}


suspend fun MultiListenerMotionLayout.awaitTransitionComplete(transitionId: Int, timeout: Long = 5000L) {
    // If we're already at the specified state, return now
    if (currentState == transitionId) return

    var listener: MotionLayout.TransitionListener? = null

    try {
        withTimeout(timeout) {
            suspendCancellableCoroutine<Unit> { continuation ->
                val l = object : TransitionAdapter() {
                    override fun onTransitionCompleted(motionLayout: MotionLayout, currentId: Int) {
                        if (currentId == transitionId) {
                            removeTransitionListener(this)
                            continuation.resume(Unit) {
                                removeTransitionListener(this)
                            }
                        }
                    }
                }
                // If the coroutine is cancelled, remove the listener
                continuation.invokeOnCancellation {
                    removeTransitionListener(l)
                }
                // And finally add the listener
                addTransitionListener(l)
                listener = l
            }
        }
    } catch (tex: TimeoutCancellationException) {
        // Transition didn't happen in time. Remove our listener and throw a cancellation
        // exception to let the coroutine know
        listener?.let(::removeTransitionListener)
        throw CancellationException("Transition to state with id: $transitionId did not" +
                " complete in timeout.", tex)
    }
}