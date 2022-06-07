package com.example.sample.model

data class CreateCommentDetailsModel(
    val id:Int,
    val user_id:Int,
    val post_id:Int,
    val content:String,
    val createdAt:String,
    val updatedAt:String
)
