package com.example.tree.ui

import androidx.annotation.DrawableRes
import com.example.tree.R

sealed class Screen(val route: String, val titleResId: String, @DrawableRes val icon: Int?) {
    object MainTip : Screen("main_tip_screen", "Home", R.drawable.plant_tip)
    object Profile : Screen("user_profile_screen", "User Profile", R.drawable.person_24px)
}