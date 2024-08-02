package com.example.tree.admin.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compose.TreeTheme
import com.example.tree.admin.screens.TipDetailScreen
import com.example.tree.admin.screens.TipListScreen

class AdminMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TreeTheme {
                AdminMainScreen(this)
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
