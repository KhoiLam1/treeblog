package com.example.tree.admin.screens

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.compose.errorLight
import com.example.compose.onPrimaryContainerLight
import com.example.compose.onSecondaryContainerLight
import com.example.compose.primaryLight
import com.example.compose.scrimLightMediumContrast
import com.example.compose.secondaryLight
import com.example.compose.theme_gray_addition
import com.example.compose.theme_pending
import com.example.compose.theme_tertiaryFixedDim_mediumContrast
import com.example.tree.R
import com.example.tree.admin.activities.MyTopAppBar
import com.example.tree.models.CheckContent
import com.example.tree.models.Tip
import com.example.tree.models.User
import com.example.tree.utils.CustomToast
import com.example.tree.utils.ToastType
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Date

@Composable
fun TipDetailScreen(tipId: String, navController: NavController) {
    val tip = loadTip(tipId).value
    val checkContent = loadCheckContent(tipId).value
    val state = when(tip?.approvalStatus){
        -1 -> "Rejected"
        0 -> "Pending"
        1 -> "Approved"
        else -> "Unknown"
    }
    val vote_counts = countUpvotes(tipId = tipId).value
    val userState = remember { mutableStateOf(User()) }
    val user = userState.value
    loadUser(userId = tip?.userId ?: "", userState)

    val context = LocalContext.current

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        MyTopAppBar(navigationIcon = {
            IconButton(onClick = {navController.popBackStack()}) {
                Icon(
                    painterResource(id = R.drawable.ic_arrow_forward_24px),
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.rotate(180f)
                )
            }
        }, title = "Tip: " + tip?.title
        )
        val tag = "ImageDebug"
        AsyncImage(
            model = tip?.imageList?.firstOrNull()?.also { imageUrl ->
                // Log the URL or path of the image
                Log.d(tag, "Loading image: $imageUrl")
            },
            placeholder = painterResource(id = R.drawable.sample_tip_pic),
            contentDescription = "First Image of Tip",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(10.dp))

        // AI Frame
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(vertical = 6.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_app),
                    contentDescription ="This is icon for illustration" ,
                    modifier = Modifier
                        .size(58.dp)
                        .padding(end = 10.dp)
                )
                Column {
                    Text(
                        text = checkContent?.response ?: "No response from AI",
                        fontSize = 14.sp,
                        color = theme_tertiaryFixedDim_mediumContrast
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = tip?.title ?: "Title",
                    color = scrimLightMediumContrast,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 7.dp)
                )

                Text(
                    text = tip?.shortDescription ?: "",
                    color = onPrimaryContainerLight,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp),
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_dot),
                            contentDescription = "This is icon for illustration",
                            modifier = Modifier
                                .size(7.dp)
                                .padding(end = 5.dp)
                        )
                        Text(
                            text = state,
                            color = theme_pending,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = DateFormat.format("dd MMM yyyy", tip?.createdAt ?: Date()).toString(),
                            color = theme_gray_addition,
                            fontSize = 12.sp
                        )
                        Image(
                            painter = painterResource(id = R.drawable.icon_dot),
                            contentDescription = "This is icon for illustration",
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .size(4.dp)
                        )
                        Text(
                            text = vote_counts.toString() + " upvotes",
                            color = theme_gray_addition,
                            fontSize = 12.sp
                        )
                    }
                }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = user.avatar ?: "",
                    placeholder = painterResource(id = R.drawable.avatar_default_2),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Text(
                        text = user.fullName ?: "",
                        color = secondaryLight,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Plant writer",
                        color = secondaryLight,
                        fontSize = 12.sp
                    )
                }
            }

            Text(
                text = tip?.content ?: "",
                color = onSecondaryContainerLight,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp),
            )

            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .fillMaxWidth()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { rejectTip(navController, tipId, context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = errorLight
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.white_red_tick),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "Reject",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = { approveTip(navController, tipId, context) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryLight
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.white_green_tick),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "Approve",
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun loadCheckContent(tipId: String): State<CheckContent?> {
    return produceState<CheckContent?>(initialValue = null) {
        val db = Firebase.firestore.collection("checkContent")
        try {
            val document = db.whereEqualTo("tipId", tipId).get().await()
            if (!document.isEmpty) {
                value = document.documents[0].toObject(CheckContent::class.java)
            }
        } catch (e: Exception) {
            Log.d("TipDetailScreen", e.toString())
        }
    }
}



@Composable
fun loadTip(tipId: String): State<Tip?> {
    return produceState<Tip?>(initialValue = null) {
        val db = Firebase.firestore.collection("Tip")
        try {
            val document = db.document(tipId).get().await()
            value = document.toObject(Tip::class.java)
        } catch (e: Exception) {
            Log.d("TipDetailScreen", e.toString())
        }
    }
}

fun loadUser(userId: String, userState : MutableState<User>){
    val db = Firebase.firestore.collection("users")
    Log.d("TipDetailScreen", "Loading user - " + userId)
    if (userId.isNotEmpty())
        db.document(userId).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) {
                    userState.value = user
                }
            }
            .addOnFailureListener {
                // Handle error
                Log.d("TipDetailScreen", it.toString())
            }
}

fun rejectTip(navController: NavController, tipId: String, context: Context){
    val db = Firebase.firestore.collection("Tip").document(tipId)
    db.update("approvalStatus", -1).addOnSuccessListener { navController.popBackStack() }.addOnFailureListener { CustomToast.show(
        context, "Failed to reject tip", ToastType.FAILURE) }
}

fun approveTip(navController: NavController, tipId: String, context: Context){
    val db = Firebase.firestore.collection("Tip").document(tipId)
    db.update("approvalStatus", 1).addOnSuccessListener { navController.popBackStack() }.addOnFailureListener { CustomToast.show(
        context, "Failed to approve tip", ToastType.FAILURE) }
}

@Composable
fun countUpvotes(tipId: String) : State<Int>{
    return produceState(initialValue = 0) {
        val db = Firebase.firestore.collection("Tip").document(tipId).collection("votes")
        db.count().query.get().addOnSuccessListener {
            Log.d("TipDetailScreen", "Upvotes count: " + it.size())
            value = it.size()
        }
            .addOnFailureListener {
                // Handle error
                Log.d("TipDetailScreen", it.toString())
            }

    }

}

@Composable
fun Spacer(modifier: Modifier = Modifier) {
    Box(modifier = modifier)
}