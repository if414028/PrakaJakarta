package com.indigital.prakajakarta.network.api

import com.indigital.prakajakarta.data.model.user.UserDataResult
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiLogin {
    @FormUrlEncoded
    @POST("api/appUsers/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<UserDataResult>
}