package com.example.votree.users.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.votree.MainActivity
import com.example.votree.admin.activities.AdminMainActivity
import com.example.votree.databinding.ActivitySignInBinding
import com.example.votree.utils.CustomToast
import com.example.votree.utils.ToastType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFirebaseAuth()
        setupUIListeners()
    }

    private fun setupFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupUIListeners() {
        binding.textView2.setOnClickListener {
            navigateToSignUp()
        }

        binding.button.setOnClickListener {
            performSignIn()
        }

        binding.forgotPassword.setOnClickListener {
            navigateToForgotPassword()
        }
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun performSignIn() {
        val email = binding.emailEt.text.toString()
        val pass = binding.passET.text.toString()

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            signInWithEmail(email, pass)
        } else {
            Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithEmail(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkUserAccessLevel(firebaseAuth.currentUser?.uid)
            } else {
                // Use CustomToast to show the error message
                val errorMessage = when {
                    task.exception is FirebaseAuthInvalidCredentialsException -> "The password is incorrect."
                    task.exception is FirebaseAuthInvalidUserException -> "The email address is not registered."
                    else -> "Sign in failed: ${task.exception?.message}"
                }
                CustomToast.show(this, errorMessage, ToastType.FAILURE)
            }
        }
    }

    private fun checkUserAccessLevel(uid: String?) {
        uid?.let {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(it).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    navigateToMainActivity(document.getString("email")!!)
                } else {
                    checkAdminAccess(it)
                }
            }
        }
    }

    private fun checkAdminAccess(uid: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("admins").document(uid).get().addOnSuccessListener { document ->
            if (document.exists()) {
                navigateToAdminMainActivity(document.getString("email")!!)
            }
        }
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToMainActivity(email: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("name", "User using email")
        startActivity(intent)
    }

    private fun navigateToAdminMainActivity(email: String) {
        val intent = Intent(this, AdminMainActivity::class.java)
        intent.putExtra("email", email)
        intent.putExtra("name", "Admin using email")
        startActivity(intent)
    }

    fun signOut() {
        if (::firebaseAuth.isInitialized) {
            firebaseAuth.signOut()
        }
    }
}