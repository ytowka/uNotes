package com.ytowka.unotes

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import com.ytowka.unotes.databinding.DrawerHeadBinding
import com.ytowka.unotes.model.CircularTransformation
import com.ytowka.unotes.screens.MainViewModel
import com.ytowka.unotes.screens.MainViewModelFactory


class MainActivity : AppCompatActivity() {
    companion object{
        const val ICON_RADIUS = 45
    }
    lateinit var mainViewModel: MainViewModel;
    lateinit var navController: NavController

    lateinit var navigationView: NavigationView
    lateinit var  navigationViewHeadBinding: DrawerHeadBinding

    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = ViewModelProvider(this, MainViewModelFactory(this.application)).get(
            MainViewModel::class.java
        )

        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = fragment.navController

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.menu_list), drawer)

        mainViewModel.authentication.firebaseUserLiveData.observe(this){
            //Log.i("debug", "observes in main activity");
            if(it != null){
                setupUser(it.photoUrl!!, it.displayName!!)
            }
        }

        toolbar =  findViewById(R.id.toolbar)
        toolbar.setupWithNavController(
            navController,
            appBarConfiguration
        )
        navigationView = findViewById(R.id.nav_view)
        navigationViewHeadBinding = DrawerHeadBinding.bind(navigationView.getHeaderView(0))


        //navigationView.setupWithNavController()

        navigationView.setNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.menu_logout -> {
                    mainViewModel.authentication.logout()
                    val action = NavGraphDirections.menuLogout()
                    navController.navigate(action)
                }
                R.id.menu_invites_fragment -> {
                    if(navController.currentDestination?.id != R.id.menu_invites_fragment){
                        val action = NavGraphDirections.actionToInvites()
                        navController.navigate(action)
                    }
                }
                R.id.menu_list -> {
                    if(navController.currentDestination?.id != R.id.menu_list){
                        val action = NavGraphDirections.actionToList()
                        navController.navigate(action)
                    }
                }
                R.id.menu_update_plan -> {
                    if(navController.currentDestination?.id != R.id.menu_update_plan){
                        val action = NavGraphDirections.actionToUpdatePlane()
                        navController.navigate(action)
                    }
                }
            }
            //val handled = NavigationUI.onNavDestinationSelected(item,navController)
            drawer.closeDrawers()
            return@setNavigationItemSelectedListener true
        }


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.i("debug","changed destination: ${destination.label}")
            if(destination.id == R.id.loginFragment || destination.id == R.id.splashFragment){
                toolbar.visibility = View.GONE
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
            else{
                /*if(destination.id == R.id.noteListFragment){
                    navController.popBackStack()
                }*/
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                toolbar.visibility = View.VISIBLE
            }
        }
    }
    private fun setupUser(photoUri: Uri, name: String){
        Log.i("debug", "setting up user in drawer")

        navigationViewHeadBinding.drawerUsername.text = name

        Picasso.get()
            .load(photoUri)
            .transform(CircularTransformation(navigationViewHeadBinding.drawerUserIcon.height / 2))
            .into(navigationViewHeadBinding.drawerUserIcon)
    }
}