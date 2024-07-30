package com.example.tree.users.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
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
                        val avatarModel = it.avatar
                        if (avatarModel.isNotEmpty()) {
                            GlideImage(
                                model = avatarModel,
                                contentDescription = "User Avatar",
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.avatar_default_2),
                                contentDescription = "Default Avatar",
                                modifier = Modifier.size(100.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    Column {
                        Text(
                            text = it.fullName,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = if (it.role == "writer") "Writer" else "User",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // "Up to Writer" card above the user profile details
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { onBecomeSeller() },
                            modifier = Modifier
                                .background(Color(0xFF5A8659), CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_unfold),
                                contentDescription = "Up to Writer",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "Up to Writer",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                UserProfileDetails(user = it)

                // Placeholder for FragmentContainerView equivalent
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                ) {
                    Text(text = "")
                }
            }

            // Floating Action Button
            ExtendedFloatingActionButton(
                text = { Text("Log out") },
                icon = { Icon(painterResource(id = R.drawable.ic_logout), contentDescription = "Log out") },
                onClick = { onLogout() },
                containerColor = Color(0xFF5A8659),
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
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
        colors = CardDefaults.cardColors(containerColor = White)
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    // Mock data for preview
    val mockDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse("2024-01-01") ?: Date()

    val mockUser = User(
        username = "JohnDoe",
        email = "johndoe@example.com",
        phoneNumber = "123-456-7890",
        createdAt = mockDate,
        avatar = "",  // Set to null to simulate missing avatar
        fullName = "John Doe",
        role = "store"
    )

    UserProfileScreen(
        viewModel = PreviewUserProfileViewModel(mockUser),
        onLogout = {},
        onBecomeSeller = {}
    )
}

@Composable
fun PreviewUserProfileViewModel(user: User): UserProfileViewModel {
    val viewModel = UserProfileViewModel(UserRepository(FirebaseFirestore.getInstance()))
    viewModel.updateUser(user)
    return viewModel
}