package com.example.habittracker2

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import com.example.habittracker2.databinding.CalendarDayBinding
import com.example.habittracker2.databinding.CalendarHeaderBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))


fun DayOfWeek.displayText(uppercase: Boolean = false): String {
    return getDisplayName(TextStyle.SHORT, Locale.ENGLISH).let { value ->
        if (uppercase) value.uppercase(Locale.ENGLISH) else value
    }
}

data class Habit(val date: Date, val color: String)

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: com.kizitonwose.calendar.view.CalendarView
    private var selectedDate: LocalDate? = null
    private var allPeriods = mutableListOf<Habit>()
    private var mappedPeriods: Map<LocalDate, List<Habit>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        calendarView = findViewById(R.id.home_calendar_view)
        getPeriods()

        // Initialize the back button and set its click listener
        val backButton = findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    fun getPeriods() {
        allPeriods.clear();
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        val firebase = FirebaseFirestore.getInstance()
        val docRef = firebase.collection("users").document(uid)
        val colRef = docRef.collection("habits")
        colRef.whereEqualTo("name", UserData.selectedHabit).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("cycles", document.data.toString())
                    val data = document.data
                    val day = data["date"].toString()
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val date = dateFormat.parse(day) ?: continue
                    val color = data["color"].toString()
                    allPeriods.add(Habit(date, color))
                }
                mappedPeriods = allPeriods.groupBy {
                    it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                }
                Log.d("habits ni yssa", mappedPeriods.toString());
                setupCalendar()
            }
            .addOnFailureListener { exception ->
            }
    }


    fun setupCalendar() {
        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(20)
        val endMonth = currentMonth.plusMonths(20)
        configureBinders(daysOfWeek)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val binding = CalendarDayBinding.bind(view)

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            calendarView.notifyDateChanged(day.date)
                            oldDate?.let { calendarView.notifyDateChanged(it) }
                        }
                    }
                }
            }
        }
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val context = container.binding.root.context
                val textView = container.binding.exFiveDayText
                val layout = container.binding.exFiveDayLayout
                textView.text = data.date.dayOfMonth.toString()

//                val flightTopView = container.binding.exFiveDayFlightTop
//                val flightBottomView = container.binding.exFiveDayFlightBottom
//                flightTopView.background = null
//                flightBottomView.background = null
                val frame = container.binding.frameLayout2


                if (data.position == DayPosition.MonthDate) {
                    textView.setTextColorRes(R.color.black)
                    layout.setBackgroundResource(if (selectedDate == data.date) R.drawable.calendar_selected_bg else 0)
                    if(mappedPeriods != null){
                        val periods = mappedPeriods!![data.date]
                        Log.d("habits ni yssa", periods.toString());
                        if (periods != null) {
                            for(period in periods){
                                val color = period.color;
                                Log.d("habits ni yssa", period.toString());
                                if (period != null) {
                                    val setColor = when (color) {
                                        "Light Pink" -> android.graphics.Color.parseColor("#FFB6C1")
                                        "Pink" -> android.graphics.Color.parseColor("#FFC0CB")
                                        "Hot Pink" -> android.graphics.Color.parseColor("#FF69B4")
                                        "Deep Pink" -> android.graphics.Color.parseColor("#FF1493")
                                        "Pale Violet Red" -> android.graphics.Color.parseColor("#DB7093")
                                        "Medium Violet Red" -> android.graphics.Color.parseColor("#C71585")
                                        else -> android.graphics.Color.WHITE
                                    }
                                    Log.d("habits ni yssa", setColor.toString());
                                    frame.setBackgroundColor(setColor)
                                }else{
                                    frame.setBackgroundColor(Color.WHITE)
                                }
                            }
                        }

                    }

//                    val flights = flights[data.date]
//                    if (flights != null) {
//                        if (flights.count() == 1) {
//                            flightBottomView.setBackgroundColor(context.getColorCompat(flights[0].color))
//                        } else {
//                            flightTopView.setBackgroundColor(context.getColorCompat(flights[0].color))
//                            flightBottomView.setBackgroundColor(context.getColorCompat(flights[1].color))
//                        }
//                    }
                } else {
                    textView.setTextColorRes(R.color.example_5_text_grey)
                    layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = CalendarHeaderBinding.bind(view).legendLayout.root
        }

        val typeFace = Typeface.create("sans-serif-light", Typeface.NORMAL)
        calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
//                     Setup each header day text if we have not done that already.
                    if (container.legendLayout.tag == null) {
                        container.legendLayout.tag = data.yearMonth
                        container.legendLayout.children.map { it as TextView }
                            .forEachIndexed { index, tv ->
                                tv.text = daysOfWeek[index].displayText(uppercase = true)
                                tv.setTextColorRes(R.color.black)
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                                tv.typeface = typeFace
                            }
                    }
//                    container.legendLayout.children.map {  }
                }
            }
    }
}