package com.example.habittracker2

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class EditHabits : AppCompatActivity() {
    private var habitNameEditText: EditText? = null
    private var timeEditText: EditText? = null
    private var colorSpinner: Spinner? = null
    private var selectDateButton: Button? = null
    private var saveButton: Button? = null
    private var db: FirebaseFirestore? = null
    private var habitId: String? = null
    private val myCalendar = Calendar.getInstance()
    private val datePicker =
        OnDateSetListener { view: DatePicker?, year: Int, month: Int, day: Int ->
            myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = month
            myCalendar[Calendar.DAY_OF_MONTH] = day
            updateLabel()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_habits)

        // Initialize views
//        habitNameEditText = findViewById(R.id.edit_habit_name)
//        timeEditText = findViewById<EditText>(R.id.edit_habit_time)
//        colorSpinner = findViewById(R.id.edit_color_spinner)
//        selectDateButton = findViewById(R.id.edit_select_date_button)
//        saveButton = findViewById(R.id.edit_save_button)
//        db = FirebaseFirestore.getInstance()
//        habitId = intent.getStringExtra("habit_id")
//        loadHabitData()
//        selectDateButton?.setOnClickListener { showDatePicker() }
//        saveButton?.setOnClickListener { saveHabitData() }

    }

    private fun loadHabitData() {
        db!!.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("habits")
            .document(habitId!!)
            .get()
            .addOnSuccessListener { document: DocumentSnapshot ->
                if (document.exists()) {
                    habitNameEditText!!.setText(document.getString("name"))
                    timeEditText!!.setText(document.getString("time"))
                    selectDateButton!!.text = document.getString("date")
                    val colorAdapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_item,
                        arrayOf("Red", "Blue", "Green", "Yellow")
                    )
                    colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    colorSpinner!!.setAdapter(colorAdapter)
                    colorSpinner!!.setSelection(colorAdapter.getPosition(document.getString("color")))
                }
            }
    }

    private fun saveHabitData() {
        val updatedName = habitNameEditText!!.getText().toString()
        val updatedTime = timeEditText!!.getText().toString()
        val updatedDate = selectDateButton!!.getText().toString()
        val selectedColor = colorSpinner!!.getSelectedItem().toString()
        val updatedData: MutableMap<String, Any> = HashMap()
        updatedData["name"] = updatedName
        updatedData["time"] = updatedTime
        updatedData["date"] = updatedDate
        updatedData["color"] = selectedColor
        db!!.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("habits")
            .document(habitId!!)
            .update(updatedData)
            .addOnSuccessListener { aVoid: Void? -> finish() }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this, datePicker, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH],
            myCalendar[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
        datePickerDialog.setOnDateSetListener { view: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            selectDateButton!!.text =
                String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
        }
    }

    private fun updateLabel() {
        selectDateButton!!.text = String.format(
            "%02d-%02d-%d",
            myCalendar[Calendar.DAY_OF_MONTH],
            myCalendar[Calendar.MONTH] + 1,
            myCalendar[Calendar.YEAR]
        )
    }
}
