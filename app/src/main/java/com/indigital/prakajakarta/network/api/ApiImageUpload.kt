package com.indigital.prakajakarta.network.api

import com.indigital.prakajakarta.data.model.post.create.ImageUpload
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiImageUpload {
    @Multipart
    @POST("api/uploads/single")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<ImageUpload>
}