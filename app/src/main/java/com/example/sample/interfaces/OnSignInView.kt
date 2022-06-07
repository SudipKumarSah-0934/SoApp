package com.example.sample.interfaces

import com.example.sample.model.SignInModel

interface OnSignInView {
    fun onSignInData(signInModel: SignInModel?)
    fun onSignInStartLoading()
    fun onSignInStopLoading()
    fun onSignInShowMessage(errMsg: String?)
    fun onSignInTime(secMsg: String?)
}