package com.example.sample.view.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.example.sample.interfaces.OnSignInView
import com.example.sample.model.SignInModel
import com.example.sample.presenter.SignInPresenter
import com.example.sample.R
import com.example.sample.utilities.CheckInternetConnection
import com.example.sample.databinding.FragmentLoginBinding
import com.example.sample.localDb.AppSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment(),OnSignInView {
    lateinit var binding: FragmentLoginBinding
    private lateinit var signInPresenter: SignInPresenter
    var signInMap = HashMap<String, Any>()
    var dialog: Dialog? = null
    lateinit  var appSessionManager: AppSessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.fragment_login, container, false
        )
        appSessionManager = AppSessionManager(requireContext())


        signInPresenter = SignInPresenter(this)
        binding.btnLoginSubmit.setOnClickListener {
            login()
        }
        dialog = Dialog(requireContext(), R.style.TransparentProgressDialog)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.custom_progress_layout)
        binding.createNewAc.setOnClickListener(View.OnClickListener {
            showFragment(SignUpFragment.newInstance())
        })

return binding.root

    }

    private fun login() {
        val email: String = binding.txtEmail.text.toString()
        val password: String = binding.txtPassword.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.txtEmail.error = "Field is empty!"
            return
        } else if (TextUtils.isEmpty(password)) {
            binding.txtPassword.error = "Password field is empty!"
            return
        }
        try {
            val handler = Handler()
            handler.postDelayed(Runnable {
                try {
                    if (CheckInternetConnection.isInternetAvailable(requireContext())) {

                        signInMap.put("email", email)
                        signInMap.put("password", password)
                        MainScope().launch(Dispatchers.Main) {
                            signInPresenter.signInDataRespose(signInMap)
                        }
                    } else {
                        requireContext()?.let {
                            Toast.makeText(
                                it,
                                "Internet is not available!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 100) // Show the ad after 10 sc
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onSignInData(signInModel: SignInModel?) {
        if (signInModel != null) {
            Toast.makeText(requireContext(),signInModel.message, Toast.LENGTH_LONG)
        }
        if (signInModel != null) {
            appSessionManager.createMerchantLoginSession(signInModel.userId.toString(),signInModel.token)
        }

        Log.d("SignInModel", "onSignInData: $signInModel")
        showFragment(HomeFragment.newInstance())
    }

    override fun onSignInStartLoading() {
        dialog?.show()
    }

    override fun onSignInStopLoading() {
        dialog?.hide()
    }

    override fun onSignInShowMessage(errMsg: String?) {
        requireContext()?.let {

            Toast.makeText(
                it,
                "! $errMsg",
                Toast.LENGTH_LONG
            ).show()

        }
    }

    override fun onSignInTime(secMsg: String?) {

    }
    private fun showFragment(fragment: Fragment?) {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(
            R.anim.slide_in,  // enter
            R.anim.fade_out,  // exit
            R.anim.fade_in,  // popEnter
            R.anim.slide_out // popExit
        )
        fragmentTransaction.replace(R.id.fragment_container, fragment!!)
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        fragmentTransaction.commit()
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            LoginFragment().apply {
            }
    }
}