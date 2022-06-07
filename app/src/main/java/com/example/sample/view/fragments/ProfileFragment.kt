package com.example.sample.view.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.bumptech.glide.Glide
import com.example.sample.R
import com.example.sample.databinding.FragmentProfileBinding
import com.example.sample.databinding.PopupCameraGalleryBinding
import com.example.sample.interfaces.NavigationListener
import com.example.sample.interfaces.OnFileUploadView
import com.example.sample.localDb.AppSessionManager
import com.example.sample.model.ProfileModel
import com.example.sample.model.UploadImageModel
import com.example.sample.presenter.UploadFilePresenter
import com.example.sample.remote.APIService
import com.example.sample.remote.RetroClient
import com.example.sample.view.activities.MainActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment(),OnFileUploadView {
    lateinit var binding: FragmentProfileBinding
    private lateinit var navigationListener: NavigationListener
    lateinit var appSessionManager: AppSessionManager
    var dialog: Dialog? = null
    private val CAMERA_REQUEST_CODE: Int = 100
    private val GALLERY_REQUEST_CODE: Int = 101
    private lateinit var regImgUri1: Uri
    private lateinit var regImg: ImageView
    lateinit var uploadFilePresenter: UploadFilePresenter
    lateinit var profileDetails:ProfileModel
    private lateinit var image_uri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentProfileBinding>(
            inflater,
            R.layout.fragment_profile, container, false)
        appSessionManager= AppSessionManager(requireContext())
        Log.d("TAG###########", "onDataGet: "+"")
        uploadFilePresenter = UploadFilePresenter(this)
        dialog = Dialog(requireContext(), R.style.TransparentProgressDialog)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.custom_progress_layout)
        navigationListener.onNavigationItemClick("ProfileFragment",1)
        binding.btnLogout.setOnClickListener(View.OnClickListener {
            logout()
        })
        initData()
        binding.addPhoto.setOnClickListener(View.OnClickListener {
            popupChooseSource(binding.addPhoto)
        })

        //imageUpload()
        return binding.root
    }

/*    private fun imageUpload() {
        val imageParts: MultipartBody.Part = prepareFilePart("file", regImgUri1)
        if (CheckInternetConnection.isInternetAvailable(context)) {
            MainScope().launch(Dispatchers.Main) {
                uploadFilePresenter.uploadFileDataResponse("bearer " + appSessionManager.userDetails[AppSessionManager.USER_TOKEN], imageParts)
            }
        }else{
            Toast.makeText(requireContext(), "Internet is not available!", Toast.LENGTH_SHORT).show()
            return
        }
    }*/

    private fun logout() {
        activity?.finish()
        activity?.let{
            val intent = Intent (it, MainActivity::class.java)
            it.startActivity(intent)
        }
    }

    private fun initData() {
        val apiService = RetroClient.getInstance()!!.create(APIService::class.java)
        apiService.viewProfile("bearer " + appSessionManager.userDetails[AppSessionManager.USER_TOKEN])!!.enqueue(object :
            Callback<ProfileModel?> {
            override fun onResponse(
                call: Call<ProfileModel?>,
                response: Response<ProfileModel?>
            ) {
                if (response.isSuccessful) {
                    profileDetails= response.body()!!
                    Log.d("TAG", "onResponse: "+ profileDetails.data)
                    if (profileDetails.data.profile_picture != null) {
                        Glide.with(binding.profileImg)
                            .load(profileDetails.data.profile_picture)
                            .fitCenter()
                            .placeholder(context!!.resources.getDrawable(R.drawable.placeholder_img))
                            .into(binding.profileImg)
                        appSessionManager!!.setProfileImg(profileDetails.data.profile_picture)
                    } else {
                        binding.profileImg.visibility = View.VISIBLE
                    }
                    binding.profileName.text=profileDetails.data.first_name+" "+profileDetails.data.last_name
                    binding.postsCount.text = profileDetails.data.total_post
                    binding.followersCount.text = profileDetails.data.total_followers
                    binding.followingCount.text = profileDetails.data.total_following_users
                    binding.txtEmail.text = profileDetails.data.email
                    binding.txtGender.text = profileDetails.data.gender
                    binding.txtDob.text = profileDetails.data.date_of_birth
                } else {
                    Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                }
                dialog!!.dismiss()
            }
            override fun onFailure(
                call: Call<ProfileModel?>,
                t: Throwable
            ) {
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ProfileFragment().apply {
            }
    }
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        navigationListener = activity as NavigationListener
    }
    open fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
        //String path = getRealPathFromUri(this, fileUri);
        val path = fileUri.path
        Log.d("pathimg", path!!)
        val file = File(path)
        //        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
        val requestFile = RequestBody.create("image".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }
    private fun popupChooseSource(img: ImageView) { //popup
        regImg = img
        lateinit var binding: PopupCameraGalleryBinding
        val popup: AlertDialog
        val builder = AlertDialog.Builder(requireContext())
        popup = builder.create()
        val layoutInflater = layoutInflater
        val customView: View = layoutInflater.inflate(R.layout.popup_camera_gallery, null)
        val cameraCL = customView.findViewById<ConstraintLayout>(R.id.cameraCL)
        val galleryCL = customView.findViewById<ConstraintLayout>(R.id.galleryCL)
        val crossBtn = customView.findViewById<ImageView>(R.id.crossBtn)

        cameraCL.setOnClickListener(View.OnClickListener {

            var values: ContentValues = ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Pic");
            image_uri = requireActivity().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )!!

            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
            startActivityForResult(takePicture, CAMERA_REQUEST_CODE)
            popup.dismiss()
        })



        galleryCL.setOnClickListener(View.OnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
            popup.dismiss()
        })

        crossBtn.setOnClickListener(View.OnClickListener {
            popup.dismiss()
        })

        popup.setCancelable(true)
        popup.setView(customView)
        popup.show()
        popup.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            CAMERA_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    launchImageCrop(image_uri)
                } else {
                    Log.e("kkk", "Image selection error: Couldn't select that image from memory.")
                }
            }
            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                } else {
                    Log.e("kkk", "Image selection error: Couldn't select that image from memory.")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    setImage(result.uri, regImg)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e("kkk", "Crop error: ${result.error}")
                }
            }
        }
    }


    private fun setImage(uri: Uri, regImg: ImageView) {

        val mDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        Glide.with(this)
            .load(uri)
            .into(regImg)
        regImg.visibility = View.VISIBLE

        /*if (regImg == binding.addPhoto) {
            regImgUri1 = uri
//            Log.d("regImgUri1", "setImage: "+uri.path)
//           regImgUri1 = Uri.parse(cUtils.compressImage(uri.path!!, mDateFormat.format(Date())))
            ///  regImgUri1 = Uri.parse( Compressor.compress(requireContext(), File(uri.getPath())))
        }*/
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1920, 1080)
            .setOutputCompressQuality(25)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .start(requireActivity(), this)
    }

    override fun onFileUploadData(uploadImageModel: UploadImageModel) {
        TODO("Not yet implemented")
    }

    override fun onFileUploadStartLoading() {
        TODO("Not yet implemented")
    }

    override fun onFileUploadStopLoading() {
        TODO("Not yet implemented")
    }

    override fun onFileUploadShowMessage(errMsg: String?) {
        TODO("Not yet implemented")
    }
}