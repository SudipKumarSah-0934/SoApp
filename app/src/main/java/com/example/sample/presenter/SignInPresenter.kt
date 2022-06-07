package com.example.sample.presenter

import com.example.sample.interfaces.OnRequestComplete
import com.example.sample.interfaces.OnSignInView
import com.example.sample.invokedApi.InvokeSignInApi
import com.example.sample.model.SignInModel
import java.util.HashMap

class SignInPresenter(private val onSignInView: OnSignInView) {
     fun signInDataRespose(signInMap: HashMap<String, Any>?) {
        onSignInView.onSignInStartLoading()
        InvokeSignInApi(signInMap, object : OnRequestComplete {
            override fun onRequestSuccess(obj: Any?) {
                onSignInView.onSignInData(obj as SignInModel?)
                onSignInView.onSignInStopLoading()
            }

            override fun onRequestError(errMsg: String?) {
//                if (errMsg!!.length > 3) { //just checks that there is something. You may want to check that length is greater than or equal to 3
//                    val bar = errMsg.substring(0, 3)
//                    if (bar == "666") {
//                        onSignInView.onSignInTime(errMsg)
//                    } else {
//                        onSignInView.onSignInShowMessage(errMsg)
//                    }
//                    //do what you need with it
//                }
                onSignInView.onSignInShowMessage(errMsg)
                onSignInView.onSignInStopLoading()
            }
        })
    }
}