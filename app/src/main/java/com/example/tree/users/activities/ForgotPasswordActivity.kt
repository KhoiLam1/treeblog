package com.example.tree.users.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import com.example.tree.utils.AuthHandler
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tree.R

class ForgotPasswordActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgotPasswordScreen(
                onResetPasswordRequested = { email -> sendPasswordResetEmail(email) },
                onBackToSignInRequested = { navigateBackToSignIn() },
                onNavigateToSignUp = { navigateToSignUp() }
            )
        }
        // Initialize the AuthHandler
        auth = AuthHandler.firebaseAuth
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    navigateBackToSignIn()
                } else {
                    val message = task.exception?.message ?: "Failed to send reset email"
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun navigateBackToSignIn() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onResetPasswordRequested: (String) -> Unit,
    onBackToSignInRequested: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var emailValid by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp)) // Adjust this height to move the logo higher
        Image(
            painter = painterResource(id = R.drawable.logo_tree),
            contentDescription = "Logo",
            modifier = Modifier
                .size(207.dp, 110.dp)
                .padding(top = 22.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Having trouble logging in?",
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Please enter your email address, and we will assist you in recovering your account",
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 48.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            isError = !emailValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = CustomGreen,
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red
            )
        )
        if (!emailValid) {
            Text(
                text = "Please enter a valid email address",
                color = Color.Red,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                if (emailValid) {
                    onResetPasswordRequested(email)
                    onBackToSignInRequested()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = CustomGreen),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(text = "SEND LOGIN LINK", color = Color.White)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Don't have an account?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Sign Up",
                color = CustomGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onNavigateToSignUp() }
                    .padding(start = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onBackToSignInRequested,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(text = "BACK TO SIGN IN", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(
        onResetPasswordRequested = {},
        onBackToSignInRequested = {},
        onNavigateToSignUp = {}
    )
}