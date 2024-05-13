package com.example.habittracker2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val viewHabits: Button = findViewById(R.id.viewhabits1_button)
        viewHabits.setOnClickListener {
            val intent = Intent(this@DashboardActivity, HabitsActivity::class.java)
            startActivity(intent)
        }

        val viewProgress: Button = findViewById(R.id.edithabits_button)
        viewProgress.setOnClickListener {
            val intent = Intent(this@DashboardActivity, ProgressActivity::class.java)
            startActivity(intent)
        }

        val viewProfile: Button = findViewById(R.id.viewhabits_button)
        viewProfile.setOnClickListener {
            val intent = Intent(this@DashboardActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        val exitAppButton: Button = findViewById(R.id.back_button)
        exitAppButton.setOnClickListener {
            finishAffinity() // Finish this activity and all activities immediately below it in the current task
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}