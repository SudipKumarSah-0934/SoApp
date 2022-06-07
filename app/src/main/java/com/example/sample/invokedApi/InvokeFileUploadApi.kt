package com.example.sample.invokedApi

import android.util.Log
import com.example.sample.interfaces.OnRequestComplete
import com.example.sample.model.UploadImageModel
import com.example.sample.remote.APIService
import com.example.sample.remote.RetroClient
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class InvokeFileUploadApi(
    imageParts: MultipartBody.Part,
    private val requestComplete: OnRequestComplete
) {
    init {
        val apiService = RetroClient.getInstance()?.create(APIService::class.java)
        apiService?.addImage(imageParts)?.enqueue(object : Callback<UploadImageModel?> {
            override fun onResponse(call: Call<UploadImageModel?>, response: Response<UploadImageModel?>) {
                if (response.isSuccessful) {
                    requestComplete.onRequestSuccess(response.body())
                    Log.d("Login", response.message())
                } else {
                    try {
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        Log.d("LoginErr", "jobj " + jObjError.getString("message"))
                        requestComplete.onRequestError(jObjError.getString("message"));
                    } catch (e: Exception) {
                        Log.d("LoginErr", e.message!!)
                        requestComplete.onRequestError("Something went wrong!")
                    }
                }
            }

            override fun onFailure(call: Call<UploadImageModel?>, t: Throwable) {
                Log.d("LoginErr", "server err" + t.message)
                requestComplete.onRequestError("Something went wrong!")
            }
        })
    }
}