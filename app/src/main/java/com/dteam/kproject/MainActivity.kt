package com.dteam.kproject

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.dteam.kproject.activityViewModel.ActivityViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration:AppBarConfiguration
    private val viewModel: ActivityViewModel by viewModels()

    companion object{
        const val preferenceKey = "K_Project"
        const val userIdKey = "userId"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.calendarFragment,
            R.id.setPhoneFragment
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        val preference = getSharedPreferences(preferenceKey, Context.MODE_PRIVATE)
        val userId = (preference.getString(userIdKey, ""))
        if (userId.isNullOrEmpty()) checkAuth(false, navController)
        else viewModel.check()

        viewModel.getCheckLD().observe(this) {
            it.getContentIfNotHandler()?.let {check ->
                checkAuth(check, navController)
            }
        }
    }

    private fun checkAuth(check: Boolean, navController: NavController) {
        if(!check) {
            val options = NavOptions.Builder()
                .setPopUpTo(R.id.calendarFragment, true).build()
            navController.navigate(R.id.setPhoneFragment, null, options)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_container)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}