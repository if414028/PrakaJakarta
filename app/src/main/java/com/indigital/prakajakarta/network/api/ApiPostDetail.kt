package com.indigital.prakajakarta.network.api

import com.indigital.prakajakarta.data.model.post.detail.ReportDataDetailResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiPostDetail {
    @GET("api/reports/detailMobile/{id}")
    fun getPostDetail(
        @Header("authorization") token: String,
        @Path("id") id: String
    ): Call<ReportDataDetailResult>
}