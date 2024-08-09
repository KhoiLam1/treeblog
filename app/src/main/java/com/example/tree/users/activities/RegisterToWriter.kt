package com.example.tree.users.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.tree.R
import com.example.tree.users.models.Writer
import com.example.tree.users.repositories.WriterRepository
import com.example.tree.users.repositories.UserRepository
import com.example.tree.utils.CustomToast
import com.example.tree.utils.ProgressDialogUtils
import com.example.tree.utils.ToastType
import com.example.tree.utils.ValidationUtils
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class RegisterToWriterActivity : ComponentActivity() {
    private val writerRepository = WriterRepository()
    private val storageReference = FirebaseStorage.getInstance().getReference("images/storeAvatars")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var avatarUrl by remember { mutableStateOf<String?>(null) }
            var showToast by remember { mutableStateOf<Pair<String, ToastType>?>(null) }

            // Handle toast messages in Compose
            LaunchedEffect(showToast) {
                showToast?.let { (message, type) ->
                    Toast.makeText(this@RegisterToWriterActivity, message, Toast.LENGTH_SHORT).show()
                    showToast = null // Reset after showing
                }
            }

            RegisterToWriterScreen(
                onImageSelected = { uri ->
                    uploadImageToFirebase(uri) { url ->
                        avatarUrl = url
                    }
                },
                onRegisterClicked = { pseudonym, email ->
                    avatarUrl?.let { url ->
                        createNewWriter(url, pseudonym, email)
                    } ?: run {
                        showToast = "Image not uploaded yet" to ToastType.FAILURE
                    }
                }
            )
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri, onUploadSuccess: (String) -> Unit) {
        ProgressDialogUtils.showLoadingDialog()

        val fileName = "${UUID.randomUUID()}.jpg"
        val fileRef = storageReference.child(fileName)
        val uploadTask = fileRef.putFile(fileUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            ProgressDialogUtils.hideLoadingDialog()
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                onUploadSuccess(downloadUri)
                Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNewWriter(avatarUrl: String, pseudonym: String, email: String) {
        val writer = Writer(
            id = "",
            writerName = pseudonym,
            writerEmail = email,
            writerAvatar = avatarUrl
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userId = Firebase.auth.currentUser?.uid ?: ""
                writerRepository.createNewWriter(writer, userId)

                val userRepository = UserRepository(Firebase.firestore)
                userRepository.updateToStore(userId, writer.id)
                userRepository.updateAvatar(userId, avatarUrl)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterToWriterActivity, "Writer created successfully", Toast.LENGTH_SHORT).show()
                    Firebase.auth.signOut()
                    val intent = Intent(this@RegisterToWriterActivity, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterToWriterActivity, "Failed to create writer: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterToWriterScreen(
    onImageSelected: (Uri) -> Unit,
    onRegisterClicked: (String, String) -> Unit
) {
    var pseudonym by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val getImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            avatarUri = it
            onImageSelected(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register to Writer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = CircleShape,
                modifier = Modifier.size(100.dp)
            ) {
                if (avatarUri != null) {
                    GlideImage(
                        model = avatarUri,
                        contentDescription = "Writer Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.avatar_default),
                        contentDescription = "Default Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(100.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = { getImage.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = CustomGreen)
            ) {
                Text("Add Author Avatar")
            }
        }

        OutlinedTextField(
            value = pseudonym,
            onValueChange = { pseudonym = it },
            label = { Text("Pseudonym") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF5A8659),
                focusedLabelColor = Color(0xFF5A8659)
            )
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Author Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF5A8659),
                focusedLabelColor = Color(0xFF5A8659)
            )
        )

        Button(
            onClick = {
                if (pseudonym.isNotEmpty() && email.isNotEmpty() && ValidationUtils.isValidEmail(email)) {
                    onRegisterClicked(pseudonym, email)
                } else {
                    CustomToast.show(context, "Please fill all fields correctly", ToastType.FAILURE)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CustomGreen),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterToWriterScreenPreview() {
    RegisterToWriterScreen(
        onImageSelected = {},
        onRegisterClicked = { _, _ -> }
    )
}