package com.example.sample.interfaces


import com.example.sample.model.SignUpModel

interface OnRegisterView {
    fun onRegisterData(signUpModel: SignUpModel)
    fun onRegisterStartLoading()
    fun onRegisterStopLoading()
    fun onRegisterShowMessage(errMsg: String?)
    fun onRegisterTime(secMsg: String?)
}