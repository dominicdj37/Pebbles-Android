package com.pebbles.Utils

import android.util.Patterns

object StringUtils {


    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}