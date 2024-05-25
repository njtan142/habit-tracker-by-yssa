package com.example.habittracker2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {
    private lateinit var editName: EditText
    private lateinit var yourName: EditText
    private lateinit var editEmail: EditText
    private lateinit var yourEmail: EditText
    private lateinit var editCurrentPassword: EditText
    private lateinit var editPassword: EditText
    private lateinit var changeProfileButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        getViews()
        loadUserData()
        setListeners()
    }

    private fun getViews() {
        editName = findViewById(R.id.edit_name)
        yourName = findViewById(R.id.your_name)
        editEmail = findViewById(R.id.edit_email)
        yourEmail = findViewById(R.id.your_email)
        editPassword = findViewById(R.id.edit_password)
        editCurrentPassword = findViewById(R.id.current_password)
        changeProfileButton = findViewById(R.id.change_profile_button)
    }

    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            yourName.setText(user.displayName)
            yourEmail.setText(user.email)

            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        if (name != null) {
                            yourName.setText(name)
                        }
                        if (email != null) {
                            yourEmail.setText(email)
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setListeners() {
        changeProfileButton.setOnClickListener {
            val user = auth.currentUser
            if (user != null) {
                val newName = yourName.text.toString()
                val newEmail = yourEmail.text.toString()
                val newPassword = editPassword.text.toString()

                if(newName.isNotEmpty()){
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                db.collection("users").document(user.uid)
                                    .update(mapOf(
                                        "name" to newName,
//                                        "email" to newEmail
                                    ))
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Profile name updated", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                            }
                        }
                }


                if (newEmail != user.email && newEmail.isNotEmpty()) {
                    user.verifyBeforeUpdateEmail(newEmail)
                        .addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                Toast.makeText(this, "Email updated, check your email on this new email", Toast.LENGTH_SHORT).show()
                                db.collection("users").document(user.uid)
                                    .update(mapOf(
//                                        "name" to newName,
                                        "email" to newEmail
                                    ))
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to update Firestore", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Failed to update email", Toast.LENGTH_SHORT).show()
                                Log.d("email update failed", emailTask.exception.toString())
                            }
                        }
                }

                if (newPassword.isNotEmpty()) {
                    val currentPassword = editCurrentPassword.text.toString().trim()
                    auth.signInWithEmailAndPassword(user.email!!, currentPassword)
                        .addOnSuccessListener {
                            val user = it.user!!
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { passwordTask ->
                                    if (passwordTask.isSuccessful) {
                                        Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                                        Log.d("update password", passwordTask.exception.toString())
                                    }
                                }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                        }


                }
            }
        }
    }
}