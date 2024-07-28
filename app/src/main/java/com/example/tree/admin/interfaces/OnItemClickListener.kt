package com.example.tree.admin.interfaces

import android.view.View

interface OnItemClickListener {
    fun onItemClicked(view: View?, position: Int)
    fun onTipItemClicked(view: View?, position: Int)
    fun searchItem(query: String) {}
}