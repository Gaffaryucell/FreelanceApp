package com.androiddevelopers.freelanceapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.ActivityBottomNavigationBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomNavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBottomNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val navView: BottomNavigationView = binding.navView
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_navigation)
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.navigation_home, R.id.navigation_discover, R.id.navigation_create_post,
//            R.id.navigation_job_postings, R.id.navigation_profile
//        ))
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

        //host navigation fragmente eriştik
        val navHostFragment =
            supportFragmentManager
                .findFragmentById(binding.navHostFragmentActivityBottomNavigation.id) as NavHostFragment?
        val navControl = navHostFragment?.navController

        navControl?.let {
            //bottom navigatiton ile host navigation fragment bağlantısı yapıldı
            //çalışması için bottom menü item idleri ile fragment idleri aynı olması lazım
            NavigationUI.setupWithNavController(binding.navView, navControl)

            //Bottom Navigation item'leri tekrar seçildiğinde sayfayı yenilemesi için eklendi
            binding.navView.setOnItemReselectedListener {
                when (it.itemId) {
                    R.id.navigation_home -> navControl.navigate(R.id.navigation_home)
                    R.id.navigation_discover -> navControl.navigate(R.id.navigation_discover)
                    R.id.navigation_create_post -> navControl.navigate(R.id.navigation_create_post)
                    R.id.navigation_job_postings -> navControl.navigate(R.id.navigation_job_postings)
                    R.id.navigation_profile -> navControl.navigate(R.id.navigation_profile)
                }
            }
        }

    }
}