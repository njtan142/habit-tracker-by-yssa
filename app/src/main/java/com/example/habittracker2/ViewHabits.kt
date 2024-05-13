package com.example.habittracker2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewHabits : AppCompatActivity() {
    private lateinit var onGoingHabitsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_habits)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onGoingHabitsRecyclerView = findViewById(R.id.ongoingHabitsRecyclerView)
        displayJournalList()

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

    }



    fun displayJournalList() {
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseFirestore.getInstance().collection("users")
            .document(id)
            .collection("habits")
            .get()
            .addOnCompleteListener { task ->
                run {
                    if (task.isSuccessful) {
                        val snapshot = task.result
                        val documents = snapshot.documents

                        val adapter = HabitListAdapter(
                            this@ViewHabits,
                            documents,
                        ) {
                            displayJournalList()
                        }
                        onGoingHabitsRecyclerView.layoutManager = LinearLayoutManager(this@ViewHabits)
                        onGoingHabitsRecyclerView.adapter = adapter
                    } else {
                        Log.e("Firestore", "Error getting documents: ", task.exception)
                    }
                }
            }
    }
}

