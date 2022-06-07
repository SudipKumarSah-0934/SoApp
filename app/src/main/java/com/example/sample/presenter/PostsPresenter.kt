package com.example.sample.presenter

import com.example.sample.interfaces.OnPostView
import com.example.sample.interfaces.OnRequestComplete
import com.example.sample.invokedApi.InvokePostApi
import com.example.sample.model.PostsModel

class PostsPresenter(private val onPostView: OnPostView){
    fun getPostList(token: String?) {
        onPostView.onLoading()
        InvokePostApi(token, object : OnRequestComplete {
            override fun onRequestSuccess(obj: Any?) {
                onPostView.onStopLoading()
                (obj as PostsModel?)?.let { onPostView.onDataGet(it) }
            }

            override fun onRequestError(errMsg: String?) {
                onPostView.onStopLoading()
                onPostView.onErrShowMessage(errMsg)
            }
        })
    }

}