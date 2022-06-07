package com.example.sample.view.fragments

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.sample.R
import com.example.sample.databinding.FragmentSignUpBinding
import com.example.sample.interfaces.OnRegisterView
import com.example.sample.localDb.AppSessionManager
import com.example.sample.model.SignUpModel
import com.example.sample.presenter.SignUpPresenter
import com.example.sample.utilities.CheckInternetConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import v2.ltd.nc_retailer.utilities.CommonDateUtilities
import v2.ltd.nc_retailer.utilities.CommonUtils
import java.text.SimpleDateFormat
import java.util.*


class SignUpFragment : Fragment(),OnRegisterView {
    private lateinit var binding:FragmentSignUpBinding
    private var date: DatePickerDialog.OnDateSetListener? = null
    private lateinit var myCalendar: Calendar
    private lateinit var commonDateUtilities: CommonDateUtilities
    private lateinit var dialog: Dialog
    var appSessionManager: AppSessionManager? = null
    lateinit var presenter: SignUpPresenter
    lateinit var cUtils: CommonUtils
    var radioGroup: RadioGroup? = null
    lateinit var radioButton: RadioButton
    private var isShowPass = false
    var registerMap = java.util.HashMap<String, Any>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        presenter= SignUpPresenter(this)
        appSessionManager = AppSessionManager(requireContext())
        cUtils = CommonUtils(requireContext())

        dialog = Dialog(requireContext(), R.style.TransparentProgressDialog)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_progress_layout)
        commonDateUtilities = CommonDateUtilities(requireContext())


        appSessionManager?.storeRegInfo("")
        initCalender()
        binding.dobLl.setOnClickListener(View.OnClickListener {
            dobStatus()
        })

        binding.createNewAc.setOnClickListener(View.OnClickListener {
            verifyData()
        })

        binding.backBtn.setOnClickListener {
            showFragment(LoginFragment.newInstance())
        }

        binding.imgVwshowPass.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!isShowPass) {
                    isShowPass = true
                    binding.imgVwshowPass.background = resources.getDrawable(R.drawable.ic_pass_show)
                    binding.etPassword.transformationMethod = null
                    binding.etPassword.setSelection(binding.etPassword.length())
                } else {
                    isShowPass = false
                    binding.imgVwshowPass.background = resources.getDrawable(R.drawable.ic_pass_hide)
                    binding.etPassword.transformationMethod = PasswordTransformationMethod()
                    binding.etPassword.setSelection(binding.etPassword.length())                }
            }
        })

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SignUpFragment().apply {
            }
    }
    private fun verifyData() {
        if (TextUtils.isEmpty(binding.firstName.text)) {
            binding.firstName.error = "Field is empty!"
            return
        }
        if (TextUtils.isEmpty(binding.lastName.text)) {
            binding.lastName.error = "Field is empty!"
            return
        }
        if (TextUtils.isEmpty(binding.etEmail.text)) {
            binding.etEmail.error = "Field is empty!"
            return
        }
        if (TextUtils.isEmpty(binding.etPassword.text)) {
            binding.etPassword.error = "Field is empty!"
            return
        }
        val dob: String = binding.dobTv.text.toString()
        if (TextUtils.isEmpty(dob) || dob == getString(R.string.dob)) {
            binding.dobTv.error = ("Date Of Birth Required!")
            return
        }
        binding.idTypeRG.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<View>(checkedId) as RadioButton
            val gender = radioButton.text.toString()
            Log.d("TAG", "onCheckedChanged: $gender")
            registerMap["gender"] = gender
        }

        if (commonDateUtilities.getYearDifference(dob) < 18) {
            binding.dobTv.error = ("Must be at least 18 years old")
            binding.dobTv.requestFocus()
            Toast.makeText(requireContext(), "Must be at least 18 years old", Toast.LENGTH_SHORT)
                .show()
            return
        }
        with(registerMap) {
            if (TextUtils.isEmpty(dob) || dob == getString(R.string.dob)) {
                binding.dobTv.error = ("Date Of Birth Required!")
                return
            }

            if (commonDateUtilities.getYearDifference(dob) < 18) {
                binding.dobTv.error = ("Must be at least 18 years old")
                binding.dobTv.requestFocus()
                Toast.makeText(requireContext(), "Must be at least 18 years old", Toast.LENGTH_SHORT)
                    .show()
                return
            }
            put("email",binding.etEmail.text.toString())
            put("password",binding.etPassword.text.toString())
            put("first_name",binding.firstName.text.toString())
            put("last_name",binding.lastName.text.toString())
            put("date_of_birth",binding.dobTv.text.toString())
            val selectedOption: Int = binding.idTypeRG.checkedRadioButtonId
            radioButton = requireActivity().findViewById(selectedOption)
            Log.d("TAG", "verifyData: "+radioButton.text)
            put("gender",radioButton.text)

        }

        if (CheckInternetConnection.isInternetAvailable(context)) {
                MainScope().launch(Dispatchers.Main) {
                    presenter.signUpDataResponse(registerMap)
                    Log.d("TAG", "verifyData: $registerMap")
                }
            }else{
                Toast.makeText(requireContext(), "Internet is not available!", Toast.LENGTH_SHORT).show()
                return
            }


        }
    private fun initCalender() {
        date =
            OnDateSetListener { _, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = monthOfYear
                myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                updateLabel(myCalendar)
            }
    }


    private fun updateLabel(myCalendar: Calendar) {
        val myFormat = "yyyy-MM-dd" //In which you need put here
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        binding.dobTv.text = sdf.format(myCalendar.time)
    }

    fun dobStatus() {
        myCalendar = Calendar.getInstance()
        if (TextUtils.isEmpty(binding.dobTv.text) || binding.dobTv.text == getString(R.string.dob)) {
            myCalendar.add(Calendar.YEAR, -18)
        } else {
            val date: String = binding.dobTv.text.toString()
            val year: Int = commonDateUtilities.getYearDifferenceforshow(date)
            myCalendar = Calendar.getInstance()
            myCalendar.add(Calendar.YEAR, -year)
        }

        DatePickerDialog(
            requireContext(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
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

    override fun onRegisterData(signUpModel: SignUpModel) {
        Log.d("TAG", "onRegisterData: $signUpModel")
    }

    override fun onRegisterStartLoading() {
      dialog.show()
    }

    override fun onRegisterStopLoading() {
        dialog.hide()
    }

    override fun onRegisterShowMessage(errMsg: String?) {
        Toast.makeText(context,errMsg,Toast.LENGTH_SHORT)
    }

    override fun onRegisterTime(secMsg: String?) {
        TODO("Not yet implemented")
    }
}