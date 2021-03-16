package com.pebbles.api.core


import android.util.Log
import com.google.gson.GsonBuilder
import com.pebbles.BuildConfig
import com.pebbles.core.sessionUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit



object APIGlobal {

    private fun getClient(): Retrofit {
        val gson = GsonBuilder().serializeNulls().setLenient().create()

        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(getBaseURL())
            .client(
                getOkHttpClient(
                    getHttpLoggingInterceptor()
                )
            )
            .build()
    }

    fun create(): ApiInterface {
        return getClient()
            .create(ApiInterface::class.java)
    }

    private fun getOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val addCookiesInterceptor = createAddCookiesInterceptor()
        val receivedCookiesInterceptor = createReceivedCookiesInterceptor()
        val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(addCookiesInterceptor)
            .addInterceptor(receivedCookiesInterceptor)

        //need to enable http logging for development environment
        if (BuildConfig.DEBUG) {
            client.addInterceptor(httpLoggingInterceptor)
        }

        return client.build()
    }

    /**
     * Add header cookie to request.
     */
    private fun createAddCookiesInterceptor(): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            Log.d("CookieLog","------------------- Setting Cookies -------------------- ")
            if (sessionUtils.hasCookies()) {
                Log.d("CookieLog","Setting the following cookies")
                for (cookie in sessionUtils.getCookies()) {
                    Log.d("CookieLog","Cookie: $cookie")
                    requestBuilder.addHeader("Cookie", cookie)
                }
            }
            Log.d("CookieLog","------------------- Setting Cookies End -----------------")
            it.proceed(requestBuilder.build())
        }
    }

    /**
     * Get cookies from response.
     */
    private fun createReceivedCookiesInterceptor(): Interceptor {
        return Interceptor {
            val originalRequest = it.proceed(it.request())
            Log.d("CookieLog","------------------- Received Cookies --------------------")
            if (originalRequest.headers("Set-Cookie").isNotEmpty()) {
                Log.d("CookieLog","Cookies received are: ")
                val cookies = HashSet<String>()
                for (header in originalRequest.headers("Set-Cookie")) {
                    Log.d("CookieLog", "Cookie: $header")
                    cookies.add(header)
                }
                sessionUtils.updateCookies(cookies)
            }
            Log.d("CookieLog","------------------- Received Cookies End ----------------")
            originalRequest
        }
    }

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    fun getBaseURL(): String {
        //return "https://pebblesapi.herokuapp.com/v1/"
        return "http://10.0.2.2:3000/v1/"
    }
}