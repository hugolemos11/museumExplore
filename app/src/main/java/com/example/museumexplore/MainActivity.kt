package com.example.museumexplore

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.museumexplore.databinding.MainActivityBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navigationView: NavigationView
    private lateinit var binding: MainActivityBinding
    private val auth = FirebaseAuth.getInstance()
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

        navController.addOnDestinationChangedListener { _: NavController, nd: NavDestination, _: Bundle? ->
            when (nd.id) {
                R.id.homeFragment /*|| nd.id == R.id.museumDetailsFragment*/ -> {
                    // Causes the menu icon (on the left) to disappear and not be clickable
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setHomeButtonEnabled(false)
                    if (nd.id == R.id.homeFragment /*|| nd.id == R.id.editProfileFragment*/) {
                        // Updates the drawer every time the user changes their profile and logs into their account
                        updateDrawerContent()
                    }
                }
                R.id.loginFragment, R.id.recoverPasswordFragment, R.id.registerFragment -> {
                    // Hide the actionBar
                    supportActionBar?.hide()
                }
                else -> {
                    toolbar.setNavigationOnClickListener {
                        onBackPressed()
                    }
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
       // Method for placing the Menu Icon on the OptionsMenu, which is supposed to be a Three-Dot Icon
        val menuToUse: Int = R.menu.drawer_icon_right

        val inflater = menuInflater
        inflater.inflate(menuToUse, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Method to make the Right Menu Icon open and close the Drawer
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

    private fun updateDrawerContent() {
        // Method to Update the Drawer Data and the Type of Drawer
        val menuInflater: MenuInflater = menuInflater

        navigationView.menu.clear()

        val currentUser = auth.currentUser

        if (currentUser != null) {
            // User is signed in
            // You can get the user's information using currentUser
            val uid = currentUser.uid
            val email = currentUser.email
            // ... other user information

            // Inflate the menu and set it for the NavigationView
            menuInflater.inflate(R.menu.nav_menu_autenticated, navigationView.menu)

            // Verify if already exists an Header on the Navigation View
            if (navigationView.getHeaderView(0) == null) {
                navigationView.inflateHeaderView(R.layout.nav_header)
            }

            // Set navigation header with user data
            val headerView = navigationView.getHeaderView(0)
            val headerImage = headerView.findViewById<ImageView>(R.id.imageViewUserImage)
            val headerUserName = headerView.findViewById<TextView>(R.id.textViewUserName)
            val headerUserEmail = headerView.findViewById<TextView>(R.id.textViewUserEmail)

            // Set image and text in the header (customize as needed)
            headerImage.setImageResource(R.drawable.menu)
            headerUserName.text = uid
            headerUserEmail.text = email

            // Get the current layout parameters of the NavigationView
            val params: ViewGroup.LayoutParams = navigationView.layoutParams

            // Update the height in the layout parameters
            params.height = ViewGroup.LayoutParams.MATCH_PARENT

            // Set the background color to the NavigationView
            val backgroundColor = ContextCompat.getColor(this, R.color.nav)
            navigationView.setBackgroundColor(backgroundColor)

            // Set the updated layout parameters to the NavigationView
            navigationView.layoutParams = params
        } else {
            // Inflate the menu and set it for the NavigationView
            menuInflater.inflate(R.menu.nav_menu, navigationView.menu)

            // Get the current layout parameters of the NavigationView
            val params: ViewGroup.LayoutParams = navigationView.layoutParams

            // Update the height in the layout parameters
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT

            // Set the updated layout parameters to the NavigationView
            navigationView.layoutParams = params

            // Set the background color to the NavigationView
            val backgroundColor = ContextCompat.getColor(this, R.color.blueMuseum)
            navigationView.setBackgroundColor(backgroundColor)

            // Remove the header view
            navigationView.removeHeaderView(navigationView.getHeaderView(0))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks
        when (item.itemId) {
            R.id.navHome -> {
                when (navController.currentDestination?.id) {
                    R.id.homeFragment -> {
                        // If the current destination is HomeFragment, close the drawer
                        drawerLayout.closeDrawer(GravityCompat.END)
                    }
                    R.id.museumDetailsFragment, R.id.artWorksFragment, R.id.artWorkDetailsFragment -> {
                        // Pop the back stack to navigate to homeFragment
                        navController.popBackStack(R.id.homeFragment, false)
                    }
                    else -> {
                        navController.navigate(R.id.action_global_homeNavigation)
                    }
                }
            }
            R.id.navTicket -> {

            }
            R.id.navScan -> {

            }
            R.id.navSettings -> {

            }
            R.id.navLogout -> {
                // Method used to terminate the Firebase authentication session
                auth.signOut()

                // Update the Drawer to  the UnAuthenticated user drawer
                updateDrawerContent()
            }
            R.id.navLogin -> {
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
}