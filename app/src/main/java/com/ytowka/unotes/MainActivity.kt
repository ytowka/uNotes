package com.ytowka.unotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.ytowka.unotes.databinding.DrawerHeadBinding


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = fragment.navController

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.noteListFragment,R.id.loginFragment),drawer)

        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)
    }
}