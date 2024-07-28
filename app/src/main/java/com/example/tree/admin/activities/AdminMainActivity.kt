package com.example.tree.admin.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import com.example.tree.R
import com.example.tree.admin.fragments.TipListFragment
import com.example.tree.admin.interfaces.OnItemClickListener
import com.example.tree.users.activities.SignInActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Suppress("DEPRECATION")
class AdminMainActivity : AppCompatActivity(), OnItemClickListener, SearchView.OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener {

    private val CALL_PHONE_PERMISSION_REQUEST_CODE = 1
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var topAppBar: MaterialToolbar
    private val db = Firebase.firestore

    private val backStackListener = FragmentManager.OnBackStackChangedListener {
        val currentBackStackEntryCount = supportFragmentManager.backStackEntryCount
        if (currentBackStackEntryCount == 1) {
            setupNormalActionBar()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        // Check for CALL_PHONE permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PHONE_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission has already been granted
            // You can now use CALL_PHONE
        }

        drawerLayout = findViewById(R.id.mainLayout)
        topAppBar = findViewById(R.id.topAppBar)
        setSupportActionBar(topAppBar)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.more -> {
                    Log.d("ListActivity", "More clicked")
                    true
                }

                else -> false
            }
        }

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            topAppBar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, TipListFragment())
            .addToBackStack(null)
            .commit()

        supportFragmentManager.addOnBackStackChangedListener(backStackListener)
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_PHONE_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    // You can now use CALL_PHONE
                } else {
                    // Permission denied
                    // You may inform the user or handle this case as needed
                }
                return
            }
        }
    }

    override fun onDestroy() {
        // Remove the listener when the activity is destroyed to avoid memory leaks
        supportFragmentManager.removeOnBackStackChangedListener(backStackListener)
        super.onDestroy()
    }

    override fun onItemClicked(view: View?, position: Int) {}

    override fun onTipItemClicked(view: View?, position: Int) {
        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setNavigationIcon(R.drawable.icon_back)
        topAppBar.menu.findItem(R.id.search).isVisible = false
        topAppBar.setNavigationOnClickListener {
            supportFragmentManager.popBackStack()
        }
    }

    override fun searchItem(query: String) {}

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.top_app_bar, menu)
        val search = menu?.findItem(R.id.search)
        val searchView = search?.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)

        return true
    }
    // Search item
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            TipListFragment().searchItem(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            TipListFragment().searchItem(query)
        }
        return true
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.nav_logout -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Log Out")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("Yes") { _, _ ->
                        SignInActivity().signOut()
                        val intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                return true
            }
            else -> return false
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }

        super.onBackPressed()
    }

    private fun setupNormalActionBar() {
        topAppBar.setTitleTextColor(resources.getColor(R.color.md_theme_primary))
        setSupportActionBar(topAppBar)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            topAppBar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        topAppBar.title = "Tips"
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.more -> {
                    Log.d("ListActivity", "More clicked")
                    true
                }

                else -> false
            }
        }
    }

}
