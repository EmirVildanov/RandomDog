package com.vildanov.randomdog


import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.vildanov.randomdog.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var mediaPlayer: MediaPlayer

    // TODO: Menu labels change label if destroy activity and than return to it

    override fun onCreate(savedInstanceState: Bundle?) {
//        setTheme(R.style.Theme_RandomDog_NoActionBar)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = customFindNavController()
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView = binding.bottomNavView
        bottomNavigationView.setupWithNavController(navController)
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)

        (application as RandomDogApplication).setLanguage(this)

        mediaPlayer = MediaPlayer.create(this, R.raw.bark1)

        if (!(application as RandomDogApplication).isInternetAvailable(this)) {
            (application as RandomDogApplication).showToast(
                this,
                getString(R.string.internet_is_not_available)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, customFindNavController())
                || super.onOptionsItemSelected(item)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onMenuOpened(featureId, menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun customFindNavController(): NavController {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        return navHostFragment.navController
    }

    fun restartFragment(fragmentId: Int) {
        val currentFragment = this.supportFragmentManager.findFragmentById(fragmentId)!!

        this.supportFragmentManager.beginTransaction()
            .detach(currentFragment)
            .commit()
        this.supportFragmentManager.beginTransaction()
            .attach(currentFragment)
            .commit()
    }
}