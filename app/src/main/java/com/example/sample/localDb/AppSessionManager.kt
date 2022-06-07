package com.example.sample.localDb

import android.content.Context
import android.content.SharedPreferences
import android.text.Editable
import java.util.HashMap

class AppSessionManager(var _context: Context) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor
    var PRIVATE_MODE = 0

    companion object {
        //..............Merchant .........................
        const val PREF_NAME = "BAT_CAMPAIGN_PREFERENCES"
        const val USER_EMAIL = "USER_EMAIL"
        const val USER_ID = "USER_ID"
        const val USER_IS_LOGIN = "USERIsLoggedIn"
        const val USER_TOKEN = "token"
        const val USER_INFO = "USER_INFO"
        const val LOGIN_TIME = "loginTime"
        const val PROFILE_IMG = "profileImg"
        const val PRODUCT_MODEL="PRODUCT_MODEL"
        const val ITEM_LIST_FRAGMENT_VISIBILITY_STATUS="ITEM_LIST_FRAGMENT_VISIBILITY_STATUS"
        const val REG_INFO = "REG_INFO"


    }

    init {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = sharedPreferences.edit()
    }

    // Shared preference data store for Merchant login session
    fun createMerchantLoginSession(
        id:String?,
        token: String?
        ) {
        editor.putBoolean(USER_IS_LOGIN, true)
        editor.putString(USER_TOKEN, token)
        if (id != null) {
            editor.putString(USER_ID,id)
        }
        editor.commit()
    }
    val userDetails: HashMap<String, String?>
        get() {
            val userData = HashMap<String, String?>()
            userData[USER_TOKEN] = sharedPreferences.getString(USER_TOKEN, null)
            userData[USER_EMAIL] = sharedPreferences.getString(USER_EMAIL, null)
            userData[USER_ID] = sharedPreferences.getString(USER_ID, null)
            return userData
        }

    fun removeAlldata() {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }

    //Remove data from sharedPreferences when user is logout
    fun logoutUser() {
        sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(USER_IS_LOGIN, false).apply()
        sharedPreferences.edit().remove(USER_TOKEN).apply()
        sharedPreferences.edit().remove(USER_EMAIL).apply()
        sharedPreferences.edit().remove(USER_ID).apply()
    }

    fun updateExpTime(time: String?) {
        editor.putString(LOGIN_TIME, time)
        editor.apply()
    }

    //Check is user Login or not
    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(USER_IS_LOGIN, false)
        set(status) {
            editor.putBoolean(USER_IS_LOGIN, status)
            editor.apply()
        }
    fun setuserInfoAlldata(userdata: String?) {
        editor.putString(USER_INFO, userdata)
        editor.apply()
    }

    fun getuserInfoAlldata(): String? {
        return sharedPreferences.getString(USER_INFO, "")
    }

    fun setProductModel(sProductModel: String) {
        editor.putString(PRODUCT_MODEL, sProductModel)
        editor.apply()
    }
    fun getProductModel(): String? {
        return sharedPreferences.getString(PRODUCT_MODEL, "")
    }
    fun setItemlistFragmentVisibilityStatus(b: Boolean) {
        editor.putBoolean(ITEM_LIST_FRAGMENT_VISIBILITY_STATUS, b)
        editor.apply()
    }
    val getItemlistFragmentVisibilityStatus: Boolean
        get() = sharedPreferences.getBoolean(ITEM_LIST_FRAGMENT_VISIBILITY_STATUS, true)
    val getRegInfo: String?
        get() = sharedPreferences.getString(REG_INFO, "")

    fun storeRegInfo(info: String?) {
        editor.putString(REG_INFO, info)
        editor.apply()
    }
    fun setProfileImg(name: String) {
        editor.putString(PROFILE_IMG, name)
        editor.apply()
    }

    fun getProfileImg(ProfileImg: String): String? {
        return sharedPreferences.getString(PROFILE_IMG, "")
    }

}