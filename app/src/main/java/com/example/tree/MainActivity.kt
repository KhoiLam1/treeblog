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
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
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
import com.example.tree.admin.activities.AdminMainActivity
import com.example.tree.ui.theme.TreeTheme
import com.example.tree.users.activities.UserProfileScreen
import com.example.tree.utils.AuthHandler
import com.example.tree.utils.PermissionManager
import com.example.tree.utils.RoleManagement
import com.example.tree.tips.TipMainScreenFragment
import com.example.tree.users.activities.UserProfileActivity
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.systemBarsPadding

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
            startDestination = "main_tip_fragment",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("main_tip_fragment") { TipMainScreenFragmentContainer() }
            composable("user_profile_fragment") { UserProfileFragment() }
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
fun UserProfileFragment() {
    val context = LocalContext.current
    val intent = Intent(context, UserProfileActivity::class.java)
    context.startActivity(intent)
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem("main_tip_fragment", "Tips", R.drawable.plant_tip),
        NavigationItem("user_profile_fragment", "Your Profile", R.drawable.person_24px)
    )

    BottomNavigation(
        backgroundColor = Color(0xFFE8EDE5)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route
            val tintColor = if (selected) Color.White else Color.Black
            val backgroundColor = if (selected) Color.Green else Color.Transparent

            BottomNavigationItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(color = backgroundColor, shape = CircleShape)
                    ) {
                        Icon(
                            painterResource(id = item.icon),
                            contentDescription = item.label,
                            tint = tintColor,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(16.dp)
                        )
                    }
                },
                label = {
                    Text(
                        item.label,
                        color = tintColor
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    TreeTheme {
        MainScreen()
    }
}

data class NavigationItem(val route: String, val label: String, @DrawableRes val icon: Int)
