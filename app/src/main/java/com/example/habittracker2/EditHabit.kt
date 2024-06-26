package com.example.habittracker2

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditHabit : AppCompatActivity() {

    private lateinit var habitNameEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var colorSpinner: Spinner
    private lateinit var addHabitButton: Button
    private lateinit var colorPreview: View

    private val myCalendar = Calendar.getInstance()
    private val datePicker = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_habit)

        // Apply edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        habitNameEditText = findViewById(R.id.habit_name_edittext)
        timeEditText = findViewById(R.id.time_edittext)
        colorSpinner = findViewById(R.id.color_spinner)
        addHabitButton = findViewById(R.id.add_habit_button)
        colorPreview = findViewById(R.id.color_preview)

        // Create an ArrayAdapter using the color options array and a default spinner layout
        val colorOptions = arrayOf("Light Pink", "Pink", "Hot Pink", "Deep Pink", "Pale Violet Red", "Medium Violet Red")
        val colorAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colorOptions)

        // Specify the layout to use when the list of choices appears
        colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        colorSpinner.adapter = colorAdapter

        // Set a listener to capture the selected color and change the color of the colorPreview view
        colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Get the selected color
                val selectedColor = colorOptions[position]

                // Update the background color of the colorPreview view based on the selected color
                val color = when (selectedColor) {
                    "Light Pink" -> Color.parseColor("#FFB6C1")
                    "Pink" -> Color.parseColor("#FFC0CB")
                    "Hot Pink" -> Color.parseColor("#FF69B4")
                    "Deep Pink" -> Color.parseColor("#FF1493")
                    "Pale Violet Red" -> Color.parseColor("#DB7093")
                    "Medium Violet Red" -> Color.parseColor("#C71585")
                    else -> Color.WHITE
                }
                colorPreview.setBackgroundColor(color)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no color selected
            }
        }

        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Set OnClickListener for the "Add Habit" button


        // Set OnClickListener for the "Select Date" button
        val selectDateButton = findViewById<Button>(R.id.select_date_button)
        selectDateButton.setOnClickListener {
            showDatePicker()
        }

        if(UserData.habitToEdit != null){
            val habit: Activity = UserData.habitToEdit!!
            habitNameEditText.setText(habit.name)
            timeEditText.setText(habit.time)
            val selectedColor = habit.color
            val color = when (selectedColor) {
                "Light Pink" -> Color.parseColor("#FFB6C1")
                "Pink" -> Color.parseColor("#FFC0CB")
                "Hot Pink" -> Color.parseColor("#FF69B4")
                "Deep Pink" -> Color.parseColor("#FF1493")
                "Pale Violet Red" -> Color.parseColor("#DB7093")
                "Medium Violet Red" -> Color.parseColor("#C71585")
                else -> Color.WHITE
            }
            colorPreview.setBackgroundColor(color)
            selectDateButton.text = habit.date
            addHabitButton.setOnClickListener {
                // Retrieve habit name, time, and selected color from EditText and Spinner
                val habitName = habitNameEditText.text.toString()
                val time = timeEditText.text.toString()
                val selectedColor = colorOptions[colorSpinner.selectedItemPosition]
                val selectDateButton = findViewById<Button>(R.id.select_date_button)
                val selectedDate = selectDateButton.text

                val user = FirebaseAuth.getInstance().currentUser!!
                val id = user.uid

                val db = FirebaseFirestore.getInstance()
                val data = mapOf(
                    "name" to habitName,
                    "time" to time,
                    "color" to selectedColor,
                    "date" to selectedDate,
                    "completed" to false,
                )

                db.document(habit.path).set(
                    data
                ).addOnSuccessListener {
                    Toast.makeText(this@EditHabit, "Editted Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, datePicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
        datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
            val selectDateButton = findViewById<Button>(R.id.select_date_button)
            selectDateButton.text = "${dayOfMonth}-${month+1}-${year}"
        }
    }

    private fun updateLabel() {
        // Update your UI with the selected date
        val selectedDateTextView = findViewById<TextView>(R.id.selected_date_textview)
        selectedDateTextView.text = myCalendar.time.toString()
    }
}
