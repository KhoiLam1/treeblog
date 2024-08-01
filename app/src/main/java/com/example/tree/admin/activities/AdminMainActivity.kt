//package com.example.tree.admin.activities
//
//import android.Manifest
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.util.Log
//import android.view.Menu
//import android.view.MenuItem
//import android.view.View
//import androidx.activity.compose.setContent
//import androidx.appcompat.app.ActionBarDrawerToggle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.SearchView
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.DrawerState
//import androidx.compose.material3.DrawerValue
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.ModalDrawerSheet
//import androidx.compose.material3.ModalNavigationDrawer
//import androidx.compose.material3.NavigationDrawerItem
//import androidx.compose.material3.NavigationDrawerItemDefaults
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.rememberDrawerState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.tooling.preview.PreviewParameter
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.constraintlayout.compose.ConstraintLayout
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.getContextForLanguage
//import androidx.core.content.ContextCompat.startActivity
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import androidx.fragment.app.FragmentManager
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.compose.TreeTheme
//import com.example.tree.R
//import com.example.tree.admin.fragments.TipListFragment
//import com.example.tree.admin.fragments.TipListScreen
//import com.example.tree.ui.MyAlertDialog
//import com.example.tree.users.activities.SignInActivity
//import com.google.android.material.appbar.MaterialToolbar
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//import com.google.android.material.navigation.NavigationView
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//
//@Suppress("DEPRECATION")
//class AdminMainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
//
//    private val CALL_PHONE_PERMISSION_REQUEST_CODE = 1
////    private lateinit var drawerLayout: DrawerLayout
////    private lateinit var topAppBar: MaterialToolbar
////    private val db = Firebase.firestore
//
////    private val backStackListener = FragmentManager.OnBackStackChangedListener {
////        val currentBackStackEntryCount = supportFragmentManager.backStackEntryCount
////        if (currentBackStackEntryCount == 1) {
////            setupNormalActionBar()
////        }
////    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main_admin)
//
//        // Check for CALL_PHONE permission
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            // Permission is not granted
//            // Request the permission
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.CALL_PHONE),
//                CALL_PHONE_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            // Permission has already been granted
//            // You can now use CALL_PHONE
//        }
//        return setContent {
//            TreeTheme {
//                AdminMainScreen(this)
//            }
//        }
//
////        drawerLayout = findViewById(R.id.mainLayout)
////        topAppBar = findViewById(R.id.topAppBar)
////        setSupportActionBar(topAppBar)
////
////        topAppBar.setOnMenuItemClickListener { menuItem ->
////            when (menuItem.itemId) {
////                R.id.more -> {
////                    Log.d("ListActivity", "More clicked")
////                    true
////                }
////
////                else -> false
////            }
////        }
////
////        val navigationView = findViewById<NavigationView>(R.id.nav_view)
////        navigationView.setNavigationItemSelectedListener(this)
////
////        val toggle = ActionBarDrawerToggle(
////            this,
////            drawerLayout,
////            topAppBar,
////            R.string.open_nav,
////            R.string.close_nav
////        )
////        drawerLayout.addDrawerListener(toggle)
////        toggle.syncState()
////
////        supportFragmentManager.beginTransaction()
////            .replace(R.id.fragment_container, TipListFragment())
////            .addToBackStack(null)
////            .commit()
////
////        supportFragmentManager.addOnBackStackChangedListener(backStackListener)
//    }
//
//    // Handle permission request result
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        when (requestCode) {
//            CALL_PHONE_PERMISSION_REQUEST_CODE -> {
//                // If request is cancelled, the result arrays are empty.
//                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    // Permission granted
//                    // You can now use CALL_PHONE
//                } else {
//                    // Permission denied
//                    // You may inform the user or handle this case as needed
//                }
//                return
//            }
//        }
//    }
//
////    override fun onDestroy() {
////        // Remove the listener when the activity is destroyed to avoid memory leaks
////        supportFragmentManager.removeOnBackStackChangedListener(backStackListener)
////        super.onDestroy()
////    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//
//        menuInflater.inflate(R.menu.top_app_bar, menu)
//        val search = menu?.findItem(R.id.search)
//        val searchView = search?.actionView as SearchView
//        searchView.isSubmitButtonEnabled = true
//        searchView.setOnQueryTextListener(this)
//
//        return true
//    }
//    // Search item
//    override fun onQueryTextSubmit(query: String?): Boolean {
//        if (query != null) {
//            TipListFragment().searchItem(query)
//        }
//        return true
//    }
//
//    override fun onQueryTextChange(query: String?): Boolean {
//        if (query != null) {
//            TipListFragment().searchItem(query)
//        }
//        return true
//    }
//
////    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
////        when (p0.itemId) {
////            R.id.nav_logout -> {
////                MaterialAlertDialogBuilder(this)
////                    .setTitle("Log Out")
////                    .setMessage("Are you sure you want to log out?")
////                    .setPositiveButton("Yes") { _, _ ->
////                        SignInActivity().signOut()
////                        val intent = Intent(this, SignInActivity::class.java)
////                        startActivity(intent)
////                    }
////                    .setNegativeButton("No") { dialog, _ ->
////                        dialog.dismiss()
////                    }
////                    .show()
////                return true
////            }
////            else -> return false
////        }
////    }
//
////    @Deprecated("Deprecated in Java")
////    override fun onBackPressed() {
////        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
////            drawerLayout.closeDrawer(GravityCompat.START)
////            return
////        }
////
////        super.onBackPressed()
////    }
////
////    private fun setupNormalActionBar() {
////        topAppBar.setTitleTextColor(resources.getColor(R.color.md_theme_primary))
////        setSupportActionBar(topAppBar)
////        val toggle = ActionBarDrawerToggle(
////            this,
////            drawerLayout,
////            topAppBar,
////            R.string.open_nav,
////            R.string.close_nav
////        )
////        drawerLayout.addDrawerListener(toggle)
////        toggle.syncState()
////        topAppBar.title = "Tips"
////        topAppBar.setOnMenuItemClickListener { menuItem ->
////            when (menuItem.itemId) {
////                R.id.more -> {
////                    Log.d("ListActivity", "More clicked")
////                    true
////                }
////
////                else -> false
////            }
////        }
////    }
//
//    fun handleLogout(){
//
//    }
//
//}
//@Composable
//fun AdminMainScreen(context: Context) {
//    val drawerState = rememberDrawerState(DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//
//    ModalNavigationDrawer(
//        drawerContent = {
//            DrawerContent(context)
//        },
//        drawerState = drawerState,
//        content = {
//            MainContent(drawerState, scope)
//        }
//    )
//}
//
//@Composable
//fun DrawerContent(context: Context) {
//    val logoutDialogState = remember { mutableStateOf(false) }
//
//    if (logoutDialogState.value) {
//        MyAlertDialog(shouldShowDialog = logoutDialogState, onConfirm = {
//            SignInActivity().signOut()
//            val intent = Intent(context, SignInActivity::class.java)
//            context.startActivity(intent)
//        }) {}
//    }
//
//    ModalDrawerSheet {
//            Spacer(Modifier.height(12.dp))
//            NavigationDrawerItem(
//                icon = { Icon(painterResource(R.drawable.baseline_logout_24), contentDescription = null, tint = Color.Black) },
//                label = { Text("Logout") },
//                onClick = { logoutDialogState.value = true },
//                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                selected = false,
//            )
//    }
//}
//
//@Composable
//fun MainContent(drawerState: DrawerState, scope : CoroutineScope) {
//    Scaffold(topBar = {
//        MyTopAppBar(navigationIcon = {
//            IconButton(onClick = { scope.launch { drawerState.open() } }) {
//                Icon(
//                    painterResource(id = R.drawable.icon_menu),
//                    contentDescription = "Menu",
//                    tint = Color.Black
//                )
//            }
//        }
//        )
//    }) { innerPadding ->
//        val navController = rememberNavController()
//
//        NavHost(navController, "admin_tip_list", modifier = Modifier.padding(innerPadding)){
//            composable("admin_tip_list"){
//                Log.d("AdminMainActivity", "Navigating Admin Tip List")
//                TipListScreen() }
//        }
//    }
//}
////
////@PreviewParameter(getContextForLanguage())
////@Composable
////fun PreviewMyApp(context: Context) {
////    AdminMainScreen(this)
////}
