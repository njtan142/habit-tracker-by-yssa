package com.example.habittracker2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setListeners()
    }

    private fun setListeners() {
        // Listener for the "Edit Profile" button
        val editUserProfileButton = findViewById<Button>(R.id.addhabits_button)
        editUserProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }

        // Listener for the "Logout" button
        findViewById<Button>(R.id.logout_button).setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Log out the user
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish() // Finish this activity after starting MainActivity
        }

        // Listener for the "Back" button
        findViewById<Button>(R.id.back_button).setOnClickListener {
            onBackPressed()
        }
    }
}