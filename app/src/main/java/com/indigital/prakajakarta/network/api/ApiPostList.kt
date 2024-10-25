package com.indigital.prakajakarta.network.api

import com.indigital.prakajakarta.data.model.post.list.ReportDataResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiPostList {
    @GET("api/reports/mobile")
    fun getPostList(
        @Header("authorization") token: String,
        @Query("limit") limit: Int,
        @Query("sortby") sortby: String,
        @Query("order") order: String
    ): Call<ReportDataResult>

    @GET("api/reports/mobile")
    fun getPostFiltered(
        @Header("authorization") token: String,
        @Query("limit") limit: Int,
        @Query("sortby") sortby: String,
        @Query("order") order: String,
        @Query("filterbydate") filter: String
    ): Call<ReportDataResult>
}