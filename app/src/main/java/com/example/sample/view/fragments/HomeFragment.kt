package com.example.sample.view.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.sample.R
import com.example.sample.databinding.FragmentHomeBinding
import com.example.sample.interfaces.NavigationListener
import com.example.sample.interfaces.OnLikeCommentListener
import com.example.sample.interfaces.OnLikeCommentView
import com.example.sample.interfaces.OnPostView
import com.example.sample.localDb.AppSessionManager
import com.example.sample.model.*
import com.example.sample.presenter.LikeUnlikePresenter
import com.example.sample.presenter.PostsPresenter
import com.example.sample.remote.APIService
import com.example.sample.remote.RetroClient.getInstance
import com.example.sample.utilities.CheckInternetConnection
import com.example.sample.view.adapter.CommentsAdapter
import com.example.sample.view.adapter.PostsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class HomeFragment : Fragment(),OnPostView,OnLikeCommentView,OnLikeCommentListener {
    lateinit var binding:FragmentHomeBinding
    var products: List<PostListModel>?=null
    var comments: ViewCommentModel?=null
    lateinit  var adapter: PostsAdapter
    lateinit var appSessionManager: AppSessionManager
    private lateinit var postsPresenter: PostsPresenter
    lateinit var navigationListener: NavigationListener
    private lateinit var likeUnlikePresenter: LikeUnlikePresenter
    var dialog: Dialog? = null
    var likeUnlikeMap = HashMap<String, Int>()
    var updateMap = java.util.HashMap<String, String>()
    var createMap = java.util.HashMap<String, String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(context))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentHomeBinding>(
            inflater,
            R.layout.fragment_home, container, false)
        appSessionManager= AppSessionManager(requireContext())
        postsPresenter = PostsPresenter(this)
        if (CheckInternetConnection.isInternetAvailable(context)) {
            postsPresenter.getPostList("bearer "+ appSessionManager.userDetails[AppSessionManager.USER_TOKEN])
        }
        dialog = Dialog(requireContext(), R.style.TransparentProgressDialog)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.custom_progress_layout)
        binding.createPost.setOnClickListener(View.OnClickListener {
            onCreatePost()
        })
        if (appSessionManager!!.getProfileImg(AppSessionManager.PROFILE_IMG) != null) {
            Glide.with(binding.homeImg)
                .load(appSessionManager!!.getProfileImg(AppSessionManager.PROFILE_IMG))
                .fitCenter()
                .placeholder(requireContext().resources.getDrawable(R.drawable.placeholder_img))
                .into(binding.homeImg)
        } else {
            binding.homeImg.visibility = View.VISIBLE
        }
        navigationListener.onNavigationItemClick("HomeFragment",1)
        return binding.root
    }

    private fun onCreatePost() {
        val builder = AlertDialog.Builder(context)
        val dataSave = builder.create()
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.create_post_popup, null)
        val createPostContent = customView.findViewById<TextView>(R.id.edtCreatePost)
        val cancelBtn: Button = customView.findViewById(R.id.btnCreateDismiss)
        val createBtn: Button = customView.findViewById(R.id.btnCreate)
        cancelBtn.setOnClickListener { dataSave.dismiss() }
        createBtn.setOnClickListener {
            createMap.put("content", createPostContent.text.toString().trim { it <= ' ' })
            val apiService = getInstance()!!.create(APIService::class.java)
            apiService.createPost("bearer " + appSessionManager.userDetails[AppSessionManager.USER_TOKEN], createMap)!!.enqueue(object : Callback<CreatePostModel?> {
                override fun onResponse(
                    call: Call<CreatePostModel?>,
                    response: Response<CreatePostModel?>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        if (CheckInternetConnection.isInternetAvailable(context)) {
                            postsPresenter.getPostList("bearer "+ appSessionManager.userDetails[AppSessionManager.USER_TOKEN])
                        }
                    } else {
                        Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                    dialog!!.dismiss()
                }

                override fun onFailure(
                    call: Call<CreatePostModel?>,
                    t: Throwable
                ) {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            })
            Toast.makeText(context, "Posted", Toast.LENGTH_SHORT).show()
            dataSave.dismiss()
        }
        dataSave.setCancelable(false)
        dataSave.setView(customView)
        dataSave.show()
        dataSave.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            HomeFragment().apply {

            }
    }

    override fun onDataGet(postsModel: PostsModel) {
        requireContext().let {
            Toast.makeText(
                it,
                postsModel.message,
                Toast.LENGTH_LONG
            ).show()

        }
        if (postsModel.message=="Post is not found!"){
            binding.noPostTv.text = "No posts yet!!!"
            binding.noPostTv.setVisibility(View.VISIBLE)
            Log.d("TAG", "onDataGet: "+postsModel.data)
        }else {
            products = postsModel.data
            val layoutManager = LinearLayoutManager(activity)
            binding.postRecycler.layoutManager = layoutManager
            adapter = PostsAdapter(products, context, this, likeUnlikeMap)
            binding.postRecycler.adapter = adapter
        }
    }

    override fun onLoading() {
        dialog?.show()
    }

    override fun onStopLoading() {
        dialog?.hide()
    }

    override fun onErrShowMessage(errMsg: String?) {
        requireContext().let {

            Toast.makeText(
                it,
                "! $errMsg",
                Toast.LENGTH_LONG
            ).show()

        }
    }

    override fun onLikeCommentData(likeUnlikeModel: LikeUnlikeModel?) {
        if (CheckInternetConnection.isInternetAvailable(context)) {
        postsPresenter.getPostList("bearer "+ appSessionManager.userDetails[AppSessionManager.USER_TOKEN])
    }
     /*   requireContext().let {
            Toast.makeText(
                it,
                "!!!! $likeUnlikeModel.message",
                Toast.LENGTH_LONG
            ).show()
        }*/
    }

    override fun onLikeCommentStartLoading() {
        dialog?.show()
    }

    override fun onLikeCommentStopLoading() {
        dialog?.hide()
    }

    override fun onLikeCommentShowMessage(errMsg: String?) {
       /* requireContext().let {
            Toast.makeText(
                it,
                "! $errMsg",
                Toast.LENGTH_LONG
            ).show()

        }*/
    }

    override fun onCardClick(Id: Int) {
        likeUnlikePresenter = LikeUnlikePresenter(this)
        if (CheckInternetConnection.isInternetAvailable(context)) {
            likeUnlikeMap["post_id"] = Id
            likeUnlikePresenter.getLike(likeUnlikeMap,"bearer "+appSessionManager.userDetails.get(
                AppSessionManager.USER_TOKEN))
        }
    }

    override fun onDeletePost(Id: Int) {
        val apiService = getInstance()!!.create(APIService::class.java)
        apiService.deletePost(
            "bearer " +
                    appSessionManager.userDetails[AppSessionManager.USER_TOKEN],
           Id
        )!!.enqueue(object : Callback<DelPostModel?> {
            override fun onResponse(call: Call<DelPostModel?>, response: Response<DelPostModel?>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    if (CheckInternetConnection.isInternetAvailable(context)) {
                        postsPresenter.getPostList("bearer "+ appSessionManager.userDetails[AppSessionManager.USER_TOKEN])
                    }
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
                dialog!!.dismiss()
            }

            override fun onFailure(call: Call<DelPostModel?>, t: Throwable) {
                Toast.makeText(context, "Failed to load data2", Toast.LENGTH_SHORT).show()
            }
        })
        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onUpdatePost(Id: Int) {
        val builder = AlertDialog.Builder(context)
        val dataSave = builder.create()
        val inflater = requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.layout_text_input, null)
        val updatePostContent = customView.findViewById<TextView>(R.id.edtUpdatePost)
        val cancelBtn: Button = customView.findViewById(R.id.btnUpdateDismiss)
        val updateBtn: Button = customView.findViewById(R.id.btnUpdate)
        cancelBtn.setOnClickListener { dataSave.dismiss() }
        updateBtn.setOnClickListener {
            Log.d("TAG", "onClick: " + likeUnlikeMap["post_id"])
            Log.d("TAG", "onClick: " + appSessionManager.userDetails[AppSessionManager.USER_TOKEN])
            Log.d("TAG", "onClick: " + updatePostContent.text.toString().trim { it <= ' ' })

            updateMap.put("content", updatePostContent.text.toString().trim { it <= ' ' })
            val apiService = getInstance()!!.create(APIService::class.java)
            apiService.updatePost("bearer " + appSessionManager.userDetails[AppSessionManager.USER_TOKEN], Id, updateMap)!!.enqueue(object : Callback<UpdatePostModel?> {
                override fun onResponse(
                    call: Call<UpdatePostModel?>,
                    response: Response<UpdatePostModel?>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, response.body().toString(), Toast.LENGTH_SHORT).show()
                        if (CheckInternetConnection.isInternetAvailable(context)) {
                            postsPresenter.getPostList("bearer "+ appSessionManager.userDetails[AppSessionManager.USER_TOKEN])
                        }
                    } else {
                        Toast.makeText(context, "Failed to load data22", Toast.LENGTH_SHORT).show()
                    }
                    dialog!!.dismiss()
                }

                override fun onFailure(
                    call: Call<UpdatePostModel?>,
                    t: Throwable
                ) {
                    Toast.makeText(context, "Failed to load data33", Toast.LENGTH_SHORT).show()
                }
            })
            Toast.makeText(context, "Edited", Toast.LENGTH_SHORT).show()
            dataSave.dismiss()
        }
        dataSave.setCancelable(false)
        dataSave.setView(customView)
        dataSave.show()
        dataSave.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onComments(Id: Int) {
        Log.d("TAG", "onComments: $Id")

    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        navigationListener = activity as NavigationListener
    }

}