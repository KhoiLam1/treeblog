package com.example.tree.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

@Composable
fun MyAlertDialog(shouldShowDialog: MutableState<Boolean>, onConfirm: (() -> Unit)?, onError: (() -> Unit)?) {
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
            },
            title = { Text(text = "Log Out") },
            text = { Text(text = "Do you want to log out ?") },
            confirmButton = {
                Button(
                    onClick = {
                        shouldShowDialog.value = false
                        onConfirm?.invoke()
                    }
                ) {
                    Text(
                        text = "Yes",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.Start) {
                    Button(onClick = {
                        shouldShowDialog.value = false
                        onError?.invoke()
                    }) {
                        Text(text = "No", color = Color.Black)
                    }
                }
            }
        )
    }
}
