package com.example.museumexplore

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.museumexplore.databinding.MainActivityBinding
import com.example.museumexplore.ui.autentication.LoginFragment
import com.example.museumexplore.ui.home.HomeFragment
import com.example.museumexplore.ui.home.MuseumDetailsFragment
import com.google.android.material.navigation.NavigationView
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navigationView: NavigationView
    private lateinit var binding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navigationView = binding.navView


        /*val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()*/

        /*toolbar.setNavigationOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }*/

        // Happens an error when using this
        /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeFragment()).commit()
        }*/


        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.loginFragment,
               // R.id.navigation_settings // Add other destination IDs if needed
            ),
            drawerLayout,
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nd.id == R.id.homeFragment /*|| nd.id == R.id.museumDetailsFragment*/) {
                // Makes the menu Icon (left) disappear
                supportActionBar?.show()
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                supportActionBar?.setHomeButtonEnabled(false)
            } else if (nd.id == R.id.loginFragment || nd.id == R.id.recoverPasswordFragment || nd.id == R.id.registerFragment) {
                // Hide the actionBar
                supportActionBar?.hide()

            } else {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }
        }
        //NavigationUI.setupWithNavController(navigationView, navController)

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuToUse: Int = R.menu.drawer_icon_right

        val inflater = menuInflater
        inflater.inflate(menuToUse, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.drawerIcon) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
            return true
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.navHome -> {
                if (navController.currentDestination?.id == R.id.homeFragment) {
                    // If the current destination is HomeFragment, close the drawer
                    drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    navController.navigate(R.id.action_global_homeNavigation)
                }
            }
            R.id.navTicket -> {

            }
            R.id.navScan -> {

            }
            R.id.navSettings -> {

            }
            R.id.navLogout -> {
                if (navController.currentDestination?.id == R.id.loginFragment) {
                    // If the current destination is HomeFragment, close the drawer
                    drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    navController.navigate(R.id.action_global_autenticationNavigation)
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }

    /*  override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }*/
}