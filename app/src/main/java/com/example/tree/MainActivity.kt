package com.example.tree

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
//noinspection UsingMaterialAndMaterial3Libraries
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.compose.TreeTheme
import com.example.tree.admin.activities.AdminMainActivity
import com.example.tree.utils.AuthHandler
import com.example.tree.utils.PermissionManager
import com.example.tree.utils.RoleManagement
import com.example.tree.tips.TipMainScreenFragment
import com.example.tree.ui.BottomNavigationBar
import com.example.tree.ui.Screen
import com.example.tree.users.activities.UserProfileActivity
import com.google.accompanist.insets.ProvideWindowInsets

class MainActivity : FragmentActivity() {

    private lateinit var permissionManager: PermissionManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check user authentication
        checkUserAuthentication()

        // Check permissions
        setupPermissions()

        // Check user role and setup navigation
        setupUserRole()
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

    private fun setupUserRole() {
        RoleManagement.checkUserRole(AuthHandler.firebaseAuth) { role ->
            when (role) {
                "admin" -> {
                    val intent = Intent(this, AdminMainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    setContent {
                        TreeTheme {
                            ProvideWindowInsets {
                                MainScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.MainTip.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.MainTip.route) { TipMainScreenFragmentContainer() }
            composable(Screen.TipDetail.route + "/{tipId}") { TipMainScreenFragmentContainer() }
            composable(Screen.Profile.route) { UserProfileScreen() }
        }
    }
}

@Composable
fun TipMainScreenFragmentContainer() {
    val context = LocalContext.current
    val activity = context as FragmentActivity
    AndroidView(factory = { ctx ->
        androidx.fragment.app.FragmentContainerView(ctx).apply {
            id = ViewCompat.generateViewId()
            activity.supportFragmentManager.commit {
                replace(id, TipMainScreenFragment())
            }
        }
    })
}

@Composable
fun UserProfileScreen() {
    val context = LocalContext.current
    val intent = Intent(context, UserProfileActivity::class.java)
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TreeTheme {
        MainScreen()
    }
}

data class NavigationItem(val route: String, val label: String, @DrawableRes val icon: Int)
