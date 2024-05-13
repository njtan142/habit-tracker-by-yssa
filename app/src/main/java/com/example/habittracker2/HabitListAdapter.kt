package com.example.habittracker2


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot

class HabitListAdapter(
    private val context: Context,
    private var dataSet: List<DocumentSnapshot>,
    private val reloader: () -> Unit
) :
    RecyclerView.Adapter<HabitListAdapter.ViewHolder>() {

    /**
     * Provide memo_backgrounds reference to the type of views that you are using
     * (custom ViewHolder)
     */

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val card: CardView
        val time: TextView
        val selectedDays: TextView // Add TextView for selected days
        val completed: CheckBox

        init {
            title = view.findViewById(R.id.habit_item_name)
            card = view.findViewById(R.id.habit_item_card)
            time = view.findViewById(R.id.habit_item_time)
            selectedDays = view.findViewById(R.id.selected_date_textview) // Initialize TextView for selected days
            completed = view.findViewById(R.id.checkbox_completed)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create memo_backgrounds new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.habit_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of memo_backgrounds view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val data = dataSet[position].data!!
        viewHolder.title.text = data["name"].toString()
        viewHolder.time.text = data["time"].toString()
        viewHolder.selectedDays.text = data["date"].toString()
        val completed = data["completed"].toString().toBoolean()
        viewHolder.completed.isChecked = completed

        viewHolder.card.setOnClickListener {
            val intent = Intent(context, CalendarActivity::class.java)
            context.startActivity(intent)
        }

        viewHolder.card.setOnClickListener {
            run {
            }
        }

        viewHolder.card.setOnLongClickListener {
            run {
                openDeleteCategoryDialog(dataSet[position].reference)
            }
            true
        }

        viewHolder.completed.setOnClickListener {
            val checked = viewHolder.completed.isChecked
            dataSet[position].reference
                .update(
                    mapOf(
                        "completed" to checked
                    )
                )
                .addOnSuccessListener {
                    reloader()
                }
        }

        val color = data["color"].toString()

        val setColor = when (color) {
            "Red" -> android.graphics.Color.RED
            "Blue" -> android.graphics.Color.BLUE
            "Green" -> android.graphics.Color.GREEN
            "Yellow" -> android.graphics.Color.YELLOW
            else -> android.graphics.Color.WHITE // default color if option is not recognized
        }

        viewHolder.card.setCardBackgroundColor(setColor)
    }

    private fun openDeleteCategoryDialog(ref: DocumentReference) {
        val builder = AlertDialog.Builder(context
        )
        builder.setTitle("Delete Journal")
            .setMessage("Are you sure you want to delete this journal?")
        builder.setPositiveButton(
            "Yes"
        ) { dialog, which ->
            ref.delete().addOnSuccessListener {
                reloader()
            }
        }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> dialog.cancel() }

        builder.show()
    }

    fun updateData(newDataSet: List<DocumentSnapshot>) {
        dataSet = newDataSet
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}