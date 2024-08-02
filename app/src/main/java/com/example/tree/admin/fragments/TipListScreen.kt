package com.example.tree.admin.fragments

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.compose.errorContainerLight
import com.example.compose.primaryContainerLight
import com.example.tree.R
import com.example.tree.admin.activities.MyTopAppBar
import com.example.tree.models.Tip
import com.example.tree.ui.MyAlertDialog
import com.example.tree.users.activities.SignInActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun TipListScreen(navController: NavController = rememberNavController(), context: Context) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            DrawerContent(context)
        },
        drawerState = drawerState,
        content = {
            TipList(navController, drawerState, scope)
        }
    )
}

@Composable
fun TipList(navController: NavController, drawerState: DrawerState, scope: CoroutineScope) {
    val itemList = loadTips().value
    val filteredList = remember(itemList) {
        mutableStateListOf<Tip>().apply {
            addAll(itemList)
        }
    }
    var tabState by remember { mutableStateOf(0) }
    Column {
        MyTopAppBar(navigationIcon = {
            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(
                    painterResource(id = R.drawable.icon_menu),
                    contentDescription = "Menu",
                    tint = Color.Black
                )
            }
        }, title = "Tips Management"
        )
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
            items(filteredList.toList(), key = { it.id }) { TipItem(it) {
                navController.navigate("admin_tip_detail/${it.id}")
            } }
        }
    }
}

@Composable
fun loadTips() : State<List<Tip>> {
    return produceState(initialValue = listOf()) {
        Log.d("TipListScreen", "Loading tips")
        val db = Firebase.firestore.collection("Tip")
        db.get().addOnSuccessListener {
            val tips = it.toObjects(Tip::class.java)
            Log.d("TipListScreen", "Successfully loaded ${tips.size} tips")
                value = tips
        }
            .addOnFailureListener { Log.d("TipListScreen", it.toString()) }
    }
}

@Composable
fun TipItem(data: Tip, modifier: Modifier = Modifier, onTipClick: () -> Unit) {
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
            .clickable { onTipClick() }
        , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row {
            AsyncImage(
                model = data.imageList[0],
                contentDescription = "tip image",
                modifier = Modifier
                    .height(64.dp)
                    .width(64.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = data.title)
                Text(text = data.shortDescription)
            }
        }

        Icon(painter = painterResource(icon), contentDescription = "list icon", modifier = modifier
            .width(48.dp)
            .height(48.dp))
    }
}

@Composable
fun DrawerContent(context: Context) {
    val logoutDialogState = remember { mutableStateOf(false) }

    if (logoutDialogState.value) {
        MyAlertDialog(shouldShowDialog = logoutDialogState, onConfirm = {
            SignInActivity().signOut()
            val intent = Intent(context, SignInActivity::class.java)
            context.startActivity(intent)
        }) {}
    }

    ModalDrawerSheet {
        Spacer(Modifier.height(12.dp))
        NavigationDrawerItem(
            icon = { Icon(painterResource(R.drawable.baseline_logout_24), contentDescription = null, tint = Color.Black) },
            label = { Text("Logout") },
            onClick = { logoutDialogState.value = true },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            selected = false,
        )
    }
}

//@Preview
//@Composable
//fun PreviewTipList(){
//    TreeTheme {
//        TipListScreen()
//    }
//}