package com.example.sample.remote

import com.example.sample.model.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*


@JvmSuppressWildcards
interface APIService {
    //.......login........
    @POST("/auth/login")
    fun getSignInInfo(@Body signInMap: Map<String, Any>?): Call<SignInModel>
    @POST("/auth/register")
    fun getSignUpInfo(@Body signUpMap: Map<String, Any>?): Call<SignUpModel>
    //.........Posts.........
    @GET("/post/view-post")
    fun getPostsList( @Header("Authorization") token: String?): Call<PostsModel?>?
    //.......like/unlike........
    @POST("/post/like-unlike")
    fun getLikeUnlike(@Body postLikeUnlikeMap: HashMap<String, Int>?,
                      @Header("Authorization") token: String?
    ): Call<LikeUnlikeModel>

    @DELETE("/post/delete-post/{id}")
    fun deletePost( @Header("Authorization") token: String?,
                    @Path("id") id: Int?): Call<DelPostModel?>?

    @PATCH("/post/update-post/{id}")
    fun updatePost( @Header("Authorization") token: String?,
                    @Path("id") id: Int?,
                    @Body updateMap: HashMap<String, String>?): Call<UpdatePostModel?>?

    @POST("/post/create-post")
    fun createPost( @Header("Authorization") token: String?,
                    @Body createMap: HashMap<String, String>?): Call<CreatePostModel?>?

    @POST("/post/create-comment/{id}")
    fun createComment( @Header("Authorization") token: String?,
                    @Path("id") id: Int?,
                    @Body createCommentMap: HashMap<String, String>?): Call<CreateCommentModel?>?

    @POST("https://api.cloudinary.com/v1_1/v2-tech/image/upload")
    fun addImage(@Part images: MultipartBody.Part?): Call<UploadImageModel?>?

    @GET("/users/view-profile-second")
    fun viewProfile( @Header("Authorization") token: String?): Call<ProfileModel?>?
    @PATCH("/users/add-profile-picture")
    fun uploadProfileImg( @Header("Authorization") token: String?,
                    @Path("id") id: Int?,
                    @Body updateMap: HashMap<String, String>?): Call<UpdatePostModel?>?
}