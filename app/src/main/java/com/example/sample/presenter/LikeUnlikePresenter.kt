package com.example.sample.presenter

import com.example.sample.interfaces.OnLikeCommentView
import com.example.sample.interfaces.OnRequestComplete
import com.example.sample.invokedApi.InvokeLikeUnlikeApi
import com.example.sample.model.LikeUnlikeModel

class LikeUnlikePresenter(private var onLikeCommentView: OnLikeCommentView) {

    fun getLike(likeUnlikeUpMap: HashMap<String, Int>, token: String?) {
        onLikeCommentView.onLikeCommentStartLoading()
        InvokeLikeUnlikeApi(token,likeUnlikeUpMap,object : OnRequestComplete {
            override fun onRequestSuccess(obj: Any?) {
                onLikeCommentView.onLikeCommentStopLoading()
                onLikeCommentView.onLikeCommentData(obj as LikeUnlikeModel?)
            }

            override fun onRequestError(errMsg: String?) {
                onLikeCommentView.onLikeCommentStopLoading()
                onLikeCommentView.onLikeCommentShowMessage(errMsg)
            }
        })
    }
}