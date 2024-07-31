package com.example.tree.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.tree.R

sealed class Screen(val route: String, @StringRes val titleResId: Int, @DrawableRes val icon: Int?) {
    object MainTip : Screen("main_tip_screen", R.string.main_tip, R.drawable.plant_tip)
    object TipDetail : Screen("tip_detail_screen", R.string.tip_detail, null)
    object Profile : Screen("user_profile_screen", R.string.user_profile, R.drawable.person_24px)
}