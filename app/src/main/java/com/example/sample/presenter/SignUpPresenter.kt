package com.example.sample.presenter

import com.example.sample.interfaces.OnRegisterView
import com.example.sample.interfaces.OnRequestComplete
import com.example.sample.interfaces.OnSignInView
import com.example.sample.invokedApi.InvokeSignInApi
import com.example.sample.invokedApi.InvokeSignUpApi
import com.example.sample.model.SignInModel
import com.example.sample.model.SignUpModel
import java.util.HashMap

class SignUpPresenter(private val onRegisterView: OnRegisterView) {
     fun signUpDataResponse(registerMap: HashMap<String, Any>?) {
         onRegisterView.onRegisterStartLoading()
        InvokeSignUpApi(registerMap, object : OnRequestComplete {
            override fun onRequestSuccess(obj: Any?) {
                onRegisterView.onRegisterData(obj as SignUpModel)
                onRegisterView.onRegisterStopLoading()
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
                onRegisterView.onRegisterShowMessage(errMsg)
                onRegisterView.onRegisterStopLoading()
            }
        })
    }
}