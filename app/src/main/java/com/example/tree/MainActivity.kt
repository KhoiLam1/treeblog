package com.example.tree

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.tree.admin.activities.AdminMainActivity
import com.example.tree.databinding.ActivityMainBinding
import com.example.tree.ui.theme.TreeTheme
import com.example.tree.utils.AuthHandler
import com.example.tree.utils.PermissionManager
import com.example.tree.utils.RoleManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tree.users.activities.UserProfileActivity



class MainActivity : AppCompatActivity() {

    private lateinit var permissionManager: PermissionManager
    private lateinit var binding: ActivityMainBinding
    private val bottomNavigation by lazy { findViewById<BottomNavigationView>(R.id.bottom_navigation_view) }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check user authentication
        checkUserAuthentication()

        // Handle system bars
        handleSystemBars()

        // Check permissions
        setupPermissions()

        // Check user role and setup navigation
        setupUserRole()

        // Setup bottom navigation view
        setupBottomNavigation()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_navigation_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun checkUserAuthentication() {
        if (!AuthHandler.isUserAuthenticated) {
            AuthHandler.redirectToSignIn(this)
            finish()
        } else {
            AuthHandler.storeUserIdInSharedPreferences(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupPermissions() {
        permissionManager = PermissionManager(this)
        permissionManager.checkPermissions()
    }

    private fun handleSystemBars() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_activity_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    private fun setupUserRole() {
        RoleManagement.checkUserRole(AuthHandler.firebaseAuth) { role ->
            when (role) {
                "admin" -> {
                    val intent = Intent(this, AdminMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Welcome ${AuthHandler.firebaseAuth.currentUser?.displayName}", Toast.LENGTH_SHORT).show()
                    setupNavigation()
                }
            }
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_navigation_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_seller_graph)
        bottomNavigation.inflateMenu(R.menu.nav_seller)

        val showDestinations = setOf(
            R.id.main_tip_fragment
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavigation.visibility = if (destination.id in showDestinations) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        bottomNavigation.setupWithNavController(navController)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.user_profile_fragment -> {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> {
                    val navController = findNavController(R.id.main_navigation_fragment)
                    navController.navigate(item.itemId)
                    true
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainActivityContent(startDestination: String = "main_tip") {
        TreeTheme {
            val navController = rememberNavController()
            Scaffold(
                topBar = { TopAppBar(title = { Text("Main Activity") }) },
                content = { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        Modifier.padding(paddingValues)
                    ) {
                        composable("main_tip") { MainTipScreen(navController) }
                        composable("user_profile") { UserProfileScreen() }
                    }
                }
            )
        }
    }

    @Composable
    fun MainTipScreen(navController: NavController) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "Main Tip Screen")
            Button(onClick = { navController.navigate("user_profile") }) {
                Text("Go to User Profile")
            }
        }
    }

    @Composable
    fun UserProfileScreen() {
        val context = LocalContext.current
        Column(modifier = Modifier.fillMaxSize()) {
            Text(text = "User Profile Screen")
            Button(onClick = {
                val intent = Intent(context, UserProfileActivity::class.java)
                context.startActivity(intent)
            }) {
                Text("Open User Profile Activity")
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainActivityContentPreview() {
        MainActivityContent()
    }
}