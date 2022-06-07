package com.example.sample.view.activities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sample.R
import com.example.sample.databinding.ActivityMainBinding
import com.example.sample.interfaces.NavigationListener
import com.example.sample.localDb.AppSessionManager
import com.example.sample.view.fragments.HomeFragment
import com.example.sample.view.fragments.LoginFragment
import com.example.sample.view.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,NavigationListener {
    lateinit var binding: ActivityMainBinding
    var fragmentName = ""
    private lateinit var appSessionManager:AppSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        appSessionManager = AppSessionManager(this)
        showFragment(LoginFragment.newInstance())
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        val view: View = binding.root
        setContentView(view)
    }

    fun showFragment(fragment: Fragment){
        val fram = supportFragmentManager.beginTransaction()
        fram.setCustomAnimations(
            R.anim.slide_in,  // enter
            R.anim.fade_out,  // exit
            R.anim.fade_in,  // popEnter
            R.anim.slide_out // popExit
        )
        fram.replace(R.id.fragment_container,fragment)
        fram.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.history-> {
                if(fragmentName == "HomeFragment"){
                    showFragment(ProfileFragment.newInstance())
                    appSessionManager.setItemlistFragmentVisibilityStatus(true)

                }
            }
            R.id.home->
            {
                if(fragmentName=="ProfileFragment")
                showFragment(HomeFragment.newInstance())
                appSessionManager.setItemlistFragmentVisibilityStatus(true)


            }

        }
        return true
    }
    override fun onNavigationItemClick(nextFragment: String, nPosition: Int) {
        Log.d("onNavigationItemClick", ""+nPosition)
        fragmentName=nextFragment
        val menuView: BottomNavigationMenuView =  binding.bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        if(nPosition==1){
            binding.bottomNavigationView.visibility= View.VISIBLE
            for (i in 0 until menuView.childCount) {
                val itemView = menuView.getChildAt(i) as BottomNavigationItemView
                if (!appSessionManager.getItemlistFragmentVisibilityStatus&&(itemView.id == R.id.home)) {
                    itemView.visibility = View.GONE
                }
            }
        } else if(nPosition==2){
            binding.bottomNavigationView.selectedItemId = R.id.history

        }


    }

    override fun onBackPressed() {
        //super.onBackPressed()
        loadExitDialog()
    }


    private var doubleBackToExitPressedOnce = false

    fun loadExitDialog() {
        try {
            if (doubleBackToExitPressedOnce) {
                finishAffinity()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, getString(R.string.exitDoublepress), Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}