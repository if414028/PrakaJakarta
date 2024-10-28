package com.indigital.prakajakarta.network.api

import com.indigital.prakajakarta.data.model.post.create.CreatePostRequest
import com.indigital.prakajakarta.data.model.post.create.PostDataResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiCreatePost {
    @POST("api/reports/")
    fun createPost(
        @Header("authorization") token: String,
        @Body request: CreatePostRequest
    ): Call<PostDataResult>
}