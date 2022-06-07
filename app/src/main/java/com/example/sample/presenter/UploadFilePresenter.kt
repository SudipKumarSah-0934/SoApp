package com.example.sample.presenter


import okhttp3.MultipartBody
import com.example.sample.interfaces.OnFileUploadView
import com.example.sample.interfaces.OnRequestComplete
import com.example.sample.invokedApi.InvokeFileUploadApi
import com.example.sample.model.UploadImageModel

class UploadFilePresenter(private val onFileUploadView: OnFileUploadView) {
   suspend fun uploadFileDataResponse(token: String, imageParts: MultipartBody.Part) {
       onFileUploadView.onFileUploadStartLoading()
        InvokeFileUploadApi(imageParts, object : OnRequestComplete {
            override fun onRequestSuccess(obj: Any?) {
                onFileUploadView.onFileUploadData(obj as UploadImageModel)
                onFileUploadView.onFileUploadStopLoading()
            }

            override fun onRequestError(errMsg: String?) {
                onFileUploadView.onFileUploadShowMessage(errMsg)
                onFileUploadView.onFileUploadStopLoading()
            }
        })
    }
}