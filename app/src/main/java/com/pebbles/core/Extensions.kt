package com.pebbles.core

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Handler
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


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