package com.example.tree.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tree.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.MainTip,
        Screen.Profile
    )

    NavigationBar() {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
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
                    Box() {
                        Icon(
                            painterResource(id = item.icon ?: R.drawable.person_24px),
                            contentDescription = stringResource(item.titleResId))
                    }
                },
                label = {
                    Text(
                        stringResource(item.titleResId)
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}
