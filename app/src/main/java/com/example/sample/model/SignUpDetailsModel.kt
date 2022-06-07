package com.example.sample.model

data class SignUpDetailsModel(
    val id:Int,
    val email:String,
    val password:String,
    val first_name:String,
    val last_name:String,
    val date_of_birth:String,
    val gender:String,
    val profile_picture:String,
    val updatedAt:String,
    val createdAt:String
)
