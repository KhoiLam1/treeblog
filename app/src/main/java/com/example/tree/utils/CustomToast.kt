@file:Suppress("DEPRECATION")

package com.example.tree.utils
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.errorContainerLightMediumContrast
import com.example.compose.primaryLight
import com.example.tree.R

object CustomToast {

    @Composable
    fun CustomToastView(message: String, type: ToastType) {
        // Define colors based on ToastType
        val backgroundColor = when (type) {
            ToastType.SUCCESS -> primaryLight
            ToastType.FAILURE -> errorContainerLightMediumContrast
            ToastType.INFO -> primaryLight
        }

        // Define the drawable resource for the icon
        val iconResId = when (type) {
            ToastType.SUCCESS -> R.drawable.done_24px
            ToastType.FAILURE -> R.drawable.ic_close_24dp
            ToastType.INFO -> R.drawable.ic_info
        }

        // Toast container
        Card(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(backgroundColor, shape = RoundedCornerShape(8.dp)),
            elevation = CardDefaults.cardElevation(5.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }

    fun show(context: Context, message: String, type: ToastType) {
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_LONG

        // Create a ComposeView and set it as the view of the Toast
        val composeView = ComposeView(context).apply {
            setContent {
                CustomToastView(message, type)
            }
        }
        toast.view = composeView
        toast.show()
    }
}