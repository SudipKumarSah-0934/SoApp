package com.example.sample.remote


import com.google.gson.GsonBuilder
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Modifier.TRANSIENT
import java.util.concurrent.TimeUnit

object RetroClient {
    var retrofit: Retrofit? = null
    const val BASE_URL = "https://soapp-nodejs.herokuapp.com"
    private var okHttpClient: OkHttpClient? = null
        private get() {
            if (field == null) field = loggingInterceptor?.let {
                OkHttpClient().newBuilder()
                    .addInterceptor(it)
                    .connectTimeout(40, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor { chain: Interceptor.Chain ->
                        val request = chain.request().newBuilder().addHeader("lang", "en").build()
                        chain.proceed(request)
                    }
                    .build()
            }
            return field
        }
    private var interceptor: LoggingInterceptor? = null

    //.client(okHttpClient)

    ////////////////////////////////////////////////////////////////////////////
    fun getInstance(): Retrofit? {


        val gson = GsonBuilder()
            .excludeFieldsWithModifiers(TRANSIENT) .create()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL) //.client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build()
            }
            return retrofit
        }
    private val loggingInterceptor: LoggingInterceptor?
        private get() {
            if (interceptor == null) interceptor = LoggingInterceptor.Builder()
                .log(Platform.INFO)
                .request("API_REQ")
                .response("API_RES")
                .setLevel(com.ihsanbal.logging.Level.BASIC)
                .build()
            return interceptor
        }
}