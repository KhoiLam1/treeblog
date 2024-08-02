package com.example.tree.tips

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.example.compose.TreeTheme
import com.example.tree.tips.models.Tip
import com.example.tree.utils.AuthHandler
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class WriteTipActivity : ComponentActivity() {
    private val fireStoreInstance = FirebaseFirestore.getInstance()
    private val storageInstance = FirebaseStorage.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var pickedImageUri by remember { mutableStateOf<Uri?>(null) }

            val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                    Toast.makeText(this, "Selected 1 image", Toast.LENGTH_SHORT).show()
                    pickedImageUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                    Toast.makeText(this, "No media selected", Toast.LENGTH_SHORT).show()
                }
            }
            TreeTheme {
                WriteTipScreen(
                    imageUri = pickedImageUri,
                    onPickImage = { pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    onSaveTip = { tip -> uploadImageAndSaveTip(tip, pickedImageUri) },
                    onCancel = { finish() }
                )
            }

        }
    }

    private fun uploadImageAndSaveTip(tip: Tip, imageUri: Uri?) {
        if (imageUri == null) {
            Toast.makeText(this, "Please add an image", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = storageInstance.reference.child("images/productTips/${imageUri.lastPathSegment}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    tip.imageList.add(uri.toString())
                    saveTipToFirestore(tip)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveTipToFirestore(tip: Tip) {
        fireStoreInstance.collection("Tip").add(tip)
            .addOnSuccessListener { documentReference ->
                val documentId = documentReference.id
                lifecycleScope.launch {
                    addFirestoreDocument("checkContent", "Please check this content (Harassment, Hate speech, Sexually explicit content, Dangerous content, not Plant-related, meaningless content and not a tip for plant should be rejected): " + "${tip.title} - ${tip.shortDescription} - ${tip.content}", documentId)
                }
                fireStoreInstance.collection("Tip").document(documentId)
                    .update("id", documentId)
                Toast.makeText(
                    this,
                    "Tip sent successfully, please wait for approval",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private suspend fun addFirestoreDocument(collectionName: String, tipContent: String, tipId: String) {
        val documentData = hashMapOf(
            "tipContent" to tipContent,
            "tipId" to tipId
        )

        try {
            FirebaseFirestore.getInstance().collection(collectionName).add(documentData).await()
        } catch (e: Exception) {
            println("Error adding document: $e")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteTipScreen(
    imageUri: Uri?,
    onPickImage: () -> Unit,
    onSaveTip: (Tip) -> Unit,
    onCancel: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var shortDescription by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = "Selected Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 16.dp)
            )
        } ?: Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No Image Selected",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontSize = 24.sp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onPickImage,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Add a Thumbnail", color = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = shortDescription,
            onValueChange = { shortDescription = it },
            label = { Text("Short Description") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            maxLines = 2,
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            maxLines = 10,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Cancel",
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    val tip = Tip(
                        title = title,
                        shortDescription = shortDescription,
                        content = content,
                        userId = AuthHandler.firebaseAuth.currentUser?.uid ?: "",
                        imageList = mutableListOf()
                    )
                    onSaveTip(tip)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWriteTipScreen() {
    TreeTheme {
        WriteTipScreen(
            imageUri = null,
            onPickImage = {},
            onSaveTip = {},
            onCancel = {}
        )
    }
}
