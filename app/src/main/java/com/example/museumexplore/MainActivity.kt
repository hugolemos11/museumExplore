package com.example.museumexplore

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.museumexplore.databinding.MainActivityBinding
import com.example.museumexplore.modules.User
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navigationView: NavigationView
    private lateinit var binding: MainActivityBinding

    private val requestCodeCameraPermission = 1001

    private val auth = FirebaseAuth.getInstance()
    private var uid: String? = null
    private var user: User? = null

    private lateinit var headerImage: ImageView
    private lateinit var headerUserName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navigationView = binding.navView

        appBarConfiguration = AppBarConfiguration(
            // Set just the top level screens, makes the screen don't have the back arrow
            setOf(
                R.id.homeFragment,
            ),
            drawerLayout,
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navigationView.setupWithNavController(navController)

        if (navController.currentDestination?.id == 2131296370) {
            updateDrawerContent()
        }

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _: Bundle? ->
            when (nd.id) {
                R.id.homeFragment -> {
                    supportActionBar?.show()
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    supportActionBar?.setHomeButtonEnabled(false)
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                    updateDrawerContent()
                }

                R.id.museumDetailsFragment,
                R.id.artWorksFragment,
                R.id.artWorkDetailsFragment,
                R.id.eventDetailsFragment,
                R.id.paymentFragment,
                R.id.registerTicketFragment,
                R.id.ticketFragment,
                R.id.editProfileFragment,
                R.id.qrCodeReaderFragment -> {
                    supportActionBar?.show()
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }

                R.id.settingsFragment, -> {
                    supportActionBar?.show()
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                    updateDrawerContent()
                }

                R.id.loginFragment,
                R.id.recoverPasswordFragment,
                R.id.registerFragment,
                R.id.finishPaymentFragment -> {
                    // Hide the actionBar
                    supportActionBar?.hide()
                }

//                else -> {
//                    toolbar.setNavigationOnClickListener {
//                        onBackPressed()
//                    }
//                }
            }
        }
        NavigationUI.setupWithNavController(navigationView, navController)

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

        Log.e("teste", currentUser.toString())

        if (currentUser != null) {
            // User is signed in
            // You can get the user's information using currentUser
            uid = currentUser.uid
            val email = currentUser.email

            val appDatabase = AppDatabase.getInstance(this)
            lifecycleScope.launch {
                if (appDatabase != null) {
                    uid?.let { currentUid ->
                        Log.e("teste", currentUid)
                        val userData = User.fetchUserData(currentUid)
                        appDatabase.userDao().add(userData)
                        user = appDatabase.userDao().get(currentUid)
                    }
                }

                user?.let { currentUser ->
                    setImage(currentUser.pathToImage, headerImage, this@MainActivity)
                    headerUserName.text = currentUser.username
                }

            }

            // Inflate the menu and set it for the NavigationView
            menuInflater.inflate(R.menu.nav_menu_autenticated, navigationView.menu)

            // Verify if already exists an Header on the Navigation View
            if (navigationView.getHeaderView(0) == null) {
                navigationView.inflateHeaderView(R.layout.nav_header)
            }

            // Set navigation header with user data
            val headerView = navigationView.getHeaderView(0)
            headerImage = headerView.findViewById(R.id.imageViewUserImage)
            headerUserName = headerView.findViewById(R.id.textViewUserName)
            val headerUserEmail = headerView.findViewById<TextView>(R.id.textViewUserEmail)

            // Set text in the header (customize as needed)
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

                    R.id.museumDetailsFragment,
                    R.id.artWorksFragment,
                    R.id.artWorkDetailsFragment,
                    R.id.eventDetailsFragment,
                    R.id.ticketFragment -> {
                        // Pop the back stack to navigate to homeFragment
                        navController.popBackStack(R.id.homeFragment, false)
                    }

                    R.id.ticketsHistoryFragment -> {
                        navController.navigate(R.id.action_ticketsHistoryFragment_to_homeNavigation)
                    }

                    R.id.qrCodeReaderFragment -> {
                        navController.navigate(R.id.action_qrCodeReaderFragment_to_homeNavigation)
                    }

                    R.id.settingsFragment,
                    R.id.editProfileFragment -> {
                        navController.navigate(R.id.action_settingsNavigation_to_homeNavigation)
                    }
                }
            }

            R.id.navTicket -> {
                when (navController.currentDestination?.id) {
                    R.id.homeFragment,
                    R.id.museumDetailsFragment,
                    R.id.artWorksFragment,
                    R.id.artWorkDetailsFragment,
                    R.id.eventDetailsFragment,
                    R.id.ticketFragment -> {
                        val bundle = Bundle()
                        bundle.putString("uid", auth.uid)
                        navController.navigate(R.id.action_homeNavigation_to_ticketsHistoryFragment, bundle)
                    }

                    R.id.ticketsHistoryFragment -> {
                        // If the current destination is Tickets History, close the drawer
                        drawerLayout.closeDrawer(GravityCompat.END)
                    }

                    R.id.qrCodeReaderFragment -> {
                        val bundle = Bundle()
                        bundle.putString("uid", auth.uid)
                        navController.navigate(R.id.action_qrCodeReaderFragment_to_ticketsHistoryFragment, bundle)
                    }

                    R.id.settingsFragment, R.id.editProfileFragment -> {
                        val bundle = Bundle()
                        bundle.putString("uid", auth.uid)
                        navController.navigate(R.id.action_settingsNavigation_to_ticketsHistoryFragment, bundle)
                    }
                }
            }

            R.id.navScan -> {
                if (ContextCompat.checkSelfPermission(
                        this@MainActivity, android.Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    askForCameraPermission()
                } else {
                    when (navController.currentDestination?.id) {
                        R.id.homeFragment,
                        R.id.museumDetailsFragment,
                        R.id.artWorksFragment,
                        R.id.artWorkDetailsFragment,
                        R.id.eventDetailsFragment,
                        R.id.ticketFragment -> {
                            navController.navigate(R.id.action_homeNavigation_to_qrCodeReaderFragment)
                        }

                        R.id.ticketsHistoryFragment -> {
                            navController.navigate(R.id.action_ticketsHistoryFragment_to_qrCodeReaderFragment)
                        }

                        R.id.qrCodeReaderFragment -> {
                            // If the current destination is QrCodeReader, close the drawer
                            drawerLayout.closeDrawer(GravityCompat.END)
                        }

                        R.id.settingsFragment,
                        R.id.editProfileFragment -> {
                            navController.navigate(R.id.action_settingsNavigation_to_qrCodeReaderFragment)
                        }
                    }
                }
            }

            R.id.navSettings -> {
                val bundle = Bundle()
                bundle.putString("uid", uid)
                when (navController.currentDestination?.id) {
                    R.id.homeFragment,
                    R.id.museumDetailsFragment,
                    R.id.artWorksFragment,
                    R.id.artWorkDetailsFragment,
                    R.id.eventDetailsFragment,
                    R.id.ticketFragment -> {
                        navController.navigate(
                            R.id.action_homeNavigation_to_settingsNavigation,
                            bundle
                        )
                    }

                    R.id.ticketsHistoryFragment -> {
                        navController.navigate(R.id.action_ticketsHistoryFragment_to_settingsNavigation, bundle)
                    }

                    R.id.qrCodeReaderFragment -> {
                        navController.navigate(
                            R.id.action_qrCodeReaderFragment_to_settingsNavigation,
                            bundle
                        )
                    }

                    R.id.editProfileFragment -> {
                        // Pop the back stack to navigate to settings
                        navController.popBackStack(R.id.settingsFragment, false)
                    }

                    R.id.settingsFragment -> {
                        // If the current destination is Settings, close the drawer
                        drawerLayout.closeDrawer(GravityCompat.END)
                    }
                }
            }

            R.id.navLogout -> {
                // Method used to terminate the Firebase authentication session
                auth.signOut()

                // Update the Drawer to  the UnAuthenticated user drawer
                updateDrawerContent()

                // Go to the home page
                // Pop the back stack to navigate to homeFragment
                navController.popBackStack(R.id.homeFragment, false)
            }

            R.id.navLogin -> {
                if (navController.currentDestination?.id == R.id.loginFragment) {
                    // If the current destination is HomeFragment, close the drawer
                    drawerLayout.closeDrawer(GravityCompat.END)
                } else {
                    navController.navigate(R.id.action_homeNavigation_to_autenticationNavigation)
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showToast("Didn't give permission to access the camara.", applicationContext)
            }
        }
    }
}