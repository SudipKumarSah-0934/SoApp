package com.example.sample.invokedApi

import com.example.sample.interfaces.OnRequestComplete
import com.example.sample.model.LikeUnlikeModel

import com.example.sample.remote.APIService
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.sample.remote.RetroClient
import java.lang.Exception
import kotlin.collections.HashMap

class InvokeLikeUnlikeApi(
    token: String?,
    likeUnlikeUpMap: HashMap<String, Int>,
    private val requestComplete: OnRequestComplete
) {
    init {
        val apiService = RetroClient.getInstance()?.create(APIService::class.java)
        apiService?.getLikeUnlike(likeUnlikeUpMap,token)?.enqueue(object : Callback<LikeUnlikeModel?> {
            override fun onResponse(call: Call<LikeUnlikeModel?>, response: Response<LikeUnlikeModel?>) {
                if (response.isSuccessful) {
                    requestComplete.onRequestSuccess(response.body())
                    //  Log.d("Login", response.message())
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        // Log.d("LoginErr", "jobj " + jObjError.getString("message"))
                        requestComplete.onRequestError(jObjError.getString("message"));

                    } catch (e: Exception) {
                        //  Log.d("LoginErr", e.message!!)
                        //   requestComplete.onRequestError("Something went wrong!")
                    }
                }
            }

            override fun onFailure(call: Call<LikeUnlikeModel?>, t: Throwable) {
                // Log.d("LoginErr", "server err" + t.message)
                requestComplete.onRequestError("Something went wrong!")
            }
        })
    }
}