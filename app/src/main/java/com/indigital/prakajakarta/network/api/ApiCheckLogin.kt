package com.indigital.prakajakarta.network.api

import com.indigital.prakajakarta.data.model.user.UserCheckLogin
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiCheckLogin {
    @FormUrlEncoded
    @POST("api/appUsers/cekStatus")
    fun checkLogin(@Field("oldToken") token: String): Call<UserCheckLogin>
}