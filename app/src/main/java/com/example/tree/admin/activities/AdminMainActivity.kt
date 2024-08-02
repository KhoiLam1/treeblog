package com.example.tree.admin.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.TreeTheme
import com.example.tree.admin.fragments.TipDetailScreen
import com.example.tree.admin.fragments.TipListScreen

class AdminMainActivity : AppCompatActivity() {

    private val CALL_PHONE_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        return setContent {
            TreeTheme {
                AdminMainScreen(this)
            }
        }
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
}
@Composable
fun AdminMainScreen(context: Context) {
    val navController = rememberNavController()
    NavHost(navController, "admin_tip_list"){
        composable("admin_tip_list"){
            Log.d("AdminMainActivity", "Navigating Admin Tip List")
            TipListScreen(navController, context)
        }
        composable("admin_tip_detail/{tipId}"){ backStackEntry ->
            Log.d("AdminMainActivity", "Navigating Admin Tip Detail")
            val tipId = backStackEntry.arguments?.getString("tipId")
            if (tipId != null) {
                TipDetailScreen(tipId, navController)
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Error: Tip ID not found")
                }
            }
        }
    }
}
//@PreviewParameter(getContextForLanguage())
//@Composable
//fun PreviewMyApp(context: Context) {
//    AdminMainScreen(this)
//}
