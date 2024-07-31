package com.example.tree.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

@Composable
fun MyAlertDialog(shouldShowDialog: MutableState<Boolean>, onConfirm : (() -> Unit)?, onError : (() -> Unit)?) {
    if (shouldShowDialog.value) { // 2
        AlertDialog( // 3
            onDismissRequest = { // 4
                shouldShowDialog.value = false
            },
            // 5
            title = { Text(text = "Alert Dialog") },
            text = { Text(text = "Jetpack Compose Alert Dialog") },
            confirmButton = { // 6
                Button(
                    onClick = {
                        shouldShowDialog.value = false
                        onConfirm?.invoke()
                    }
                ) {
                    Text(
                        text = "Xác nhận",
                        color = Color.White
                    )
                }
            },
            dismissButton = {
                Button(onClick = {
                    shouldShowDialog.value = false
                    onError?.invoke()
                }) {
                    Text(text = "Hủy", color=Color.Red)
                }
            }
        )
    }
}