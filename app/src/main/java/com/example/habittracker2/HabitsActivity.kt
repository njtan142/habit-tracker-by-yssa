package com.example.habittracker2

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HabitsActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_habits)

        val backHabits: Button = findViewById(R.id.back_button)
        backHabits.setOnClickListener {
            val intent = Intent(this@HabitsActivity, DashboardActivity::class.java)
            startActivity(intent)
        }

        val addHabits: Button = findViewById(R.id.editprofile_button)
        addHabits.setOnClickListener {
            val intent = Intent(this@HabitsActivity, AddHabits::class.java)
            startActivity(intent)
        }

        val editHabits: Button = findViewById(R.id.logout_button)
        editHabits.setOnClickListener {
            val intent = Intent(this@HabitsActivity, EditHabits::class.java)
            startActivity(intent)
        }

        val viewHabits: Button = findViewById(R.id.viewhabits_button)
        viewHabits.setOnClickListener {
            val intent = Intent(this@HabitsActivity, ViewHabits::class.java)
            startActivity(intent)
        }

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}