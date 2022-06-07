package com.example.sample.model

data class PostListModel(
    val id: Int,
    val content:String,
    val picture: String,
    val tag_id: List<Integer>,
    val first_name:String,
    val last_name:String,
    val user_id:Int,
    val total_like:String,
    val total_comment:String,
    var islike:Boolean,
    var comment:List<CommentDetailsModel>

)
