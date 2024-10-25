package com.indigital.prakajakarta.network.api

import com.indigital.prakajakarta.data.model.post.create.PostDataResult
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiCreatePost {
    @FormUrlEncoded
    @POST("api/reports/")
    fun createPost(
        @Header("authorization") token: String,
        @Field("name") name: String,
        @Field("address1") address: String,
        @Field("pekerjaan") work: String,
        @Field("usia") age: String,
        @Field("jenisKelamin") sex: String,
        @Field("lat") lat: String,
        @Field("lng") lng: String,
        @Field("images") imageUrl: String,
        @Field("answer1") answer1: String,
        @Field("answer2") answer2: String
    ): Call<PostDataResult>
}