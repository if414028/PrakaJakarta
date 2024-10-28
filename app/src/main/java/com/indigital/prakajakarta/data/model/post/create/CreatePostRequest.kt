package com.indigital.prakajakarta.data.model.post.create

data class CreatePostRequest(
    val name: String,
    val address1: String,
    val pekerjaan: String,
    val usia: String,
    val jenisKelamin: String,
    val lat: String,
    val lng: String,
    val images: String,
    val answer1: String,
    val answer2: String
)