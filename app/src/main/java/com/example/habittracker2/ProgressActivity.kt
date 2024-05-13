package com.example.habittracker2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class ProgressActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var habitListAdapter: HabitList2Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress)

        // Initialize RecyclerView and adapter
        recyclerView = findViewById(R.id.recycler_view_habits)

        // Set layout manager and adapter for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch habit data from Firestore
        fetchHabitData()
    }

    private fun fetchHabitData() {
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(id)
            .collection("habits")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val habitsList = querySnapshot.documents
                // Update the adapter with the fetched data
                habitListAdapter = HabitList2Adapter(this@ProgressActivity, habitsList, {fetchHabitData()})

                recyclerView.adapter = habitListAdapter
            }
            .addOnFailureListener { exception ->
                // Handle any errors or exceptions
            }
    }
}
