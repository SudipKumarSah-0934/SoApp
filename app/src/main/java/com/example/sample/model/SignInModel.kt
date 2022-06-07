package com.example.sample.model

import com.google.gson.annotations.SerializedName


data class SignInModel (


    @SerializedName("message"  ) var message  : String? = null,

    @SerializedName("token" ) var token : String? = null,

    @SerializedName("userId" ) var userId : Int? = null

)