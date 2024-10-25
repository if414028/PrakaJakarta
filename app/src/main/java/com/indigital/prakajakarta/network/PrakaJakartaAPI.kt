package com.indigital.prakajakarta.network

import com.indigital.prakajakarta.AppConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PrakaJakartaAPI {

    const val BASE_URL: String = AppConfig.BASE_URL + "/"

    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())

    private var retrofit: Retrofit = builder.build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()

    fun <S> createService(serviceClass: Class<S>): S {
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging)
            builder.client(httpClient.build())
            retrofit = builder.build()
        }

        return retrofit.create(serviceClass)
    }

}