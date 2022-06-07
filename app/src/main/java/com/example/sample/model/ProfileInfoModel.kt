package com.example.sample.model

data class ProfileInfoModel(
    val id: Long,
    val email: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val date_of_birth: String,
    val gender: String,
    val profile_picture: String,
    val createdAt: String,
    val updatedAt: String,
    val total_following_users: String,
    val total_followers: String,
    val total_post: String
)
