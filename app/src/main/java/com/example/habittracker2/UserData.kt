package com.example.habittracker2

object UserData {
    var selectedHabit : String = "";
    var habitToEdit: Activity? = null;
}


data class Activity(
    var color: String = "",
    var completed: Boolean = false,
    var date: String = "",
    var name: String = "",
    var time: String = "",
    var path: String = "",
)