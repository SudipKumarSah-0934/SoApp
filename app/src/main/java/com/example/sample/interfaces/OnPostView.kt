package com.example.sample.interfaces

import com.example.sample.model.PostsModel

interface OnPostView {
        fun onDataGet(postsModel: PostsModel)
        fun onLoading()
        fun onStopLoading()
        fun onErrShowMessage(errMsg: String?)

}