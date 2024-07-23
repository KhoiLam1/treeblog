package com.example.votree.admin.interfaces

import android.view.View

interface OnItemClickListener {
    fun onItemClicked(view: View?, position: Int)
    fun onTipItemClicked(view: View?, position: Int)
    fun searchItem(query: String) {}
}