package com.pebbles.api.core

import java.util.*


object ApiConstants {

    object MethodName{
        val GET ="GET"
        val POST ="POST"
        val DELETE ="DELETE"
        val PUT ="PUT"
    }

    /**
     * get complete url string including the base url
     */
    fun getRequestUrlFor(url: String) = APIGlobal.getBaseURL() + url

    fun String?.formatUrl(vararg args: Any?): String {
        if (!this.isNullOrEmpty()) {
            return this.format(Locale.ENGLISH, *args)
        }
        return ""
    }


    const val sdfsdgsg = "%s/posts.json?report_id=%s&page_size=%s&between_id=%s"

}

object ApiParams {
    //region session request
    const val KEY_EMAIL = "email"
    const val KEY_USER = "user"
    const val KEY_PASSWORD = "password"
    //endregion

}


