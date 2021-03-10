package com.pebbles.api.core


import com.google.gson.GsonBuilder
import com.pebbles.BuildConfig
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
        //val addCookiesInterceptor = createAddCookiesInterceptor()
        //val receivedCookiesInterceptor = createReceivedCookiesInterceptor()
        val client = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            //.addInterceptor(addCookiesInterceptor)
            //.addInterceptor(receivedCookiesInterceptor)

        //need to enable http logging for development environment
        if (BuildConfig.DEBUG) {
            client.addInterceptor(httpLoggingInterceptor)
        }

        return client.build()
    }

//    /**
//     * Add header cookie to request.
//     */
//    private fun createAddCookiesInterceptor(): Interceptor {
//        return Interceptor {
//            val requestBuilder = it.request().newBuilder()
//            if (sessionUtils.hasCookies()) {
//                for (cookie in sessionUtils.getCookies()) {
//                    requestBuilder.addHeader("Cookie", cookie)
//                }
//            }
//            it.proceed(requestBuilder.build())
//        }
//    }
//
//    /**
//     * Get cookies from response.
//     */
//    private fun createReceivedCookiesInterceptor(): Interceptor {
//        return Interceptor {
//            val originalRequest = it.proceed(it.request())
//            if (originalRequest.headers("Set-Cookie").isNotEmpty()) {
//                val cookies = HashSet<String>()
//                for (header in originalRequest.headers("Set-Cookie")) {
//                    cookies.add(header)
//                }
//                sessionUtils.updateCookies(cookies)
//            }
//            originalRequest
//        }
//    }

    private fun getHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    fun getBaseURL(): String {
        return "https://pebblesapi.herokuapp.com/v1/"
        //return "http://192.168.1.9:3000/v1/"
    }
}