package com.example.sample.interfaces

interface OnLikeCommentListener {
        fun onCardClick(Id: Int)
        fun onDeletePost(Id: Int)
        fun onUpdatePost(Id: Int)
        fun onComments(Id: Int)
}