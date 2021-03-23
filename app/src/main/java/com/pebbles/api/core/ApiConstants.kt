package com.pebbles.api.core

import java.util.*


object ApiConstants {


    /**
     * get complete url string including the base url
     */
    fun getRequestUrlFor(url: String) = APIGlobal.getBaseURL() + url

    fun String?.formatUrl(vararg args: Any?): String {
        if (!this.isNullOrEmpty()) {
            return this.format(*args)
        }
        return ""
    }


    const val LOGIN = "authenticate.json?username=%s&password=%s"
    const val AUTO_LOGIN = "auto_login.json"
    const val SIGN_UP = "sign_up.json?username=%s&password=%s&email=%s"

    const val PEBBLES = "get_pebbles.json?"
    const val CREATE_PEBBLE = "create_pebble.json?pebble_name=%s&pebble_key=%s"
    const val SHARE_PEBBLE = "share_pebble.json?id=%d&username=%s"

    const val TOKEN_REG = "save_token.json?token=%s"
    const val TOKEN_DE_REG = "delete_token.json?token=%s"

}

object ApiParams {
    //region session request
    const val KEY_EMAIL = "email"
    const val KEY_USER = "user"
    const val KEY_PASSWORD = "password"
    //endregion

}


