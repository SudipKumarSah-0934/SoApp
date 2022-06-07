package com.example.sample.interfaces

import com.example.sample.model.UploadImageModel

interface OnFileUploadView {
    fun onFileUploadData(uploadImageModel: UploadImageModel)
    fun onFileUploadStartLoading()
    fun onFileUploadStopLoading()
    fun onFileUploadShowMessage(errMsg: String?)
}