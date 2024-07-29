package com.example.tree.users.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.tree.R
import com.example.tree.users.factories.UserProfileViewModelFactory
import com.example.tree.users.repositories.UserRepository
import com.example.tree.users.view_models.UserProfileViewModel
import com.example.tree.users.models.User
import com.example.tree.utils.ProgressDialogUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserProfileActivity : ComponentActivity() {

    private val userProfileViewModel: UserProfileViewModel by viewModels {
        UserProfileViewModelFactory(UserRepository(FirebaseFirestore.getInstance()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load user profile
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        userProfileViewModel.loadUserProfile(userId)

        setContent {
            UserProfileScreen(
                viewModel = userProfileViewModel,
                onLogout = { navigateLogout() },
                onBecomeSeller = { navigateToBecomeWriter() }
            )
        }
    }

    private fun navigateToBecomeWriter() {
        val intent = Intent(this, RegisterToWriterActivity::class.java) // Updated class name
        startActivity(intent)
    }

    private fun navigateLogout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel,
    onLogout: () -> Unit,
    onBecomeSeller: () -> Unit
) {
    val user by viewModel.user.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val context = LocalContext.current

    if (isLoading) {
        ProgressDialogUtils.showLoadingDialog(context)
    } else {
        ProgressDialogUtils.hideLoadingDialog()
    }

    user?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier.size(100.dp)
                ) {
                    GlideImage(
                        model = it.avatar,
                        contentDescription = "User Avatar",
                        modifier = Modifier.size(100.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = it.fullName,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = if (it.role == "writer") "Writer" else "User",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            UserProfileDetails(user = it)

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onBecomeSeller() },
                colors = ButtonDefaults.buttonColors(containerColor = CustomGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Become a Seller", color = Color.White)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onLogout() },
                colors = ButtonDefaults.buttonColors(containerColor = CustomGreen),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Logout", color = Color.White)
            }
        }
    }
}

@Composable
fun UserProfileDetails(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFf2f2f2))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ProfileDetailRow(iconRes = R.drawable.ic_username, label = "User Name", value = user.username)
            ProfileDetailRow(iconRes = R.drawable.ic_email, label = "Email", value = user.email)
            ProfileDetailRow(iconRes = R.drawable.ic_phone, label = "Phone", value = user.phoneNumber)
            ProfileDetailRow(iconRes = R.drawable.ic_update, label = "Created Date", value = user.createdAt.toString())
        }
    }
}

@Composable
fun ProfileDetailRow(iconRes: Int, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Black
        )
    }
}