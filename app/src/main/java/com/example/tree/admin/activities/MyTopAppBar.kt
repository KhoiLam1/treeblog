package com.example.tree.admin.activities

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import com.example.tree.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(title:String = "Tips", navigationIcon :  @Composable (() -> Unit)? = null, actions: List<@Composable () -> Unit>? = null) {
    TopAppBar(
        title = {
            Text(
                title,
            )
        },
        navigationIcon = {
            navigationIcon?.invoke() ?: IconButton(onClick = { /* TODO: Handle menu click */ }) {
                Icon(
                    painterResource(id = R.drawable.icon_menu),
                    contentDescription = "Menu",
                    tint = Color.Black
                )
            }
        },
        actions = {
            actions?.forEach { action ->
                action()
            }
        }
    )
}