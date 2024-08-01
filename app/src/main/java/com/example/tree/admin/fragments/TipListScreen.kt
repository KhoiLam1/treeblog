package com.example.tree.admin.fragments

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.compose.TreeTheme
import com.example.compose.errorContainerLight
import com.example.compose.primaryContainerLight
import com.example.tree.R
import com.example.tree.models.Tip
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.jetbrains.annotations.Async


@Composable
fun TipListScreen() {
    val itemList = remember {
        mutableStateListOf<Tip>()
    }
    val filteredList = remember {
        mutableStateListOf<Tip>()
    }
    var tabState by remember { mutableStateOf(0) }

    Column {
        TabRow(selectedTabIndex = tabState) {
            Tab(selected = tabState == 0, text = { Text("All") }, onClick = {
                tabState = 0
                filteredList.clear()
                filteredList.addAll(itemList)
            })
            Tab(selected = tabState == 1, text = { Text("Pending") }, onClick = {
                tabState = 1
                filteredList.clear()
                filteredList.addAll(itemList.filter { it.approvalStatus == 0 })
            })
            Tab(selected = tabState == 2, text = { Text("Approved") }, onClick = {
                tabState = 2
                filteredList.clear()
                filteredList.addAll(itemList.filter { it.approvalStatus == 1 })
            })
            Tab(selected = tabState == 3, text = { Text("Rejected") }, onClick = {
                tabState = 3
                filteredList.clear()
                filteredList.addAll(itemList.filter { it.approvalStatus == -1 })
            })
        }
        LazyColumn {
            items(itemList.toList(), key = { it.id }) { ListItem(it) }
        }
    }

    val db = Firebase.firestore.collection("Tip")
    SideEffect {
        db.get().addOnSuccessListener {
            val tips = it.toObjects(Tip::class.java)
            Log.d("TipListScreen", "Successfully loaded ${tips.size} tips")
            itemList.addAll(tips)
        }
            .addOnFailureListener { Log.d("TipListScreen", it.toString()) }
    }
}

@Composable
fun ListItem(data: Tip, modifier: Modifier = Modifier) {
    val bgColor = when (data.approvalStatus) {
        -1 -> errorContainerLight
        0 -> Color.Transparent
        1 -> primaryContainerLight
        else -> Color.Transparent
    }
    val icon = when (data.approvalStatus) {
        -1 -> R.drawable.red_tick
        0 -> R.drawable.baseline_arrow_right_24
        1 -> R.drawable.green_tick
        else -> R.drawable.red_tick
    }
    Row(
        modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(24.dp, 12.dp)
        , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row {
            AsyncImage(
                model = data.imageList[0],
                contentDescription = "tip image",
                modifier = Modifier.height(64.dp).width(64.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = data.title)
                Text(text = data.shortDescription)
            }
        }

        Icon(painter = painterResource(icon), contentDescription = "list icon", modifier = modifier.width(48.dp).height(48.dp))
    }
}

@Preview
@Composable
fun PreviewTipList(){
    TreeTheme {
        TipListScreen()
    }
}