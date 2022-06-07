package com.example.sample.interfaces

interface OnRequestComplete {
    fun onRequestSuccess(obj: Any?)
    fun onRequestError(errMsg: String?)
}