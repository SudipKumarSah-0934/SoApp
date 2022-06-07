package com.example.sample.interfaces

import com.example.sample.model.LikeUnlikeModel

interface OnLikeCommentView {
    fun onLikeCommentData(likeUnlikeModel: LikeUnlikeModel?)
    fun onLikeCommentStartLoading()
    fun onLikeCommentStopLoading()
    fun onLikeCommentShowMessage(errMsg: String?)
}