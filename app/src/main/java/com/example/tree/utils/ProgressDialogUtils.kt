package com.example.tree.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

object ProgressDialogUtils {
    private val _isDialogVisible: MutableState<Boolean> = mutableStateOf(false)

    val isDialogVisible: Boolean
        get() = _isDialogVisible.value

    fun showLoadingDialog() {
        _isDialogVisible.value = true
    }

    fun hideLoadingDialog() {
        _isDialogVisible.value = false
    }

    @Composable
    fun ProgressDialog() {
        if (isDialogVisible) {
            Dialog(onDismissRequest = { hideLoadingDialog() }) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .size(100.dp), // Adjust the size as needed
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}