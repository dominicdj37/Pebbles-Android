package com.pebbles.Utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.ContextCompat
import java.lang.ref.WeakReference
import java.util.*

object ResourceUtils {

    private var contextRef: WeakReference<Context>? = null
    //Separate context reference is maintained for getting String resource since it has to be updated frequently for localization
    private var localizedContext: WeakReference<Context>? = null

    //region initialize
    fun initialize(context: Context) {
        contextRef = WeakReference(context)
        localizedContext = WeakReference(context)
    }
    //endregion

    //region update locale
    fun updateContextReference(context: Context) {
        localizedContext = WeakReference(context)
    }
    //endregion

    //region Locale
    fun getConfig(): Configuration? {
        return localizedContext?.get()?.resources?.configuration
    }

    fun getCurrentLocale(): Locale? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getConfig()?.locales?.get(0)
        } else {
            getConfig()?.locale
        }
    }
    //endregion

    //region resource values
    fun getStringResource(id: Int): String {
        return localizedContext?.get()?.resources?.getString(id) ?: ""
    }

    fun getDimenResource(id: Int): Int {
        return contextRef?.get()?.resources?.getDimension(id)?.toInt() ?: 0
    }

    fun getDrawableResource(id: Int): Drawable? {
        return contextRef?.get()?.resources?.getDrawable(id, null)
    }

    fun getColorResource(id: Int): Int? {
        val context = contextRef?.get()
        if (context != null) {
            return ContextCompat.getColor(context, id)
        }
        return null
    }
    //endregion
}