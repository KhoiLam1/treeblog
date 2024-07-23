package com.example.votree.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckContent(
    var id: String = "",
    val tipId: String = "",
    val tipContent: String = "",
    val response: String = "",
) : Parcelable