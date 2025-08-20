package nick.bonson.demotodolist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.checkbox.MaterialCheckBox
import nick.bonson.demotodolist.R
import nick.bonson.demotodolist.data.entity.TaskEntity
import nick.bonson.demotodolist.utils.DateFormatter
import nick.bonson.demotodolist.utils.TaskDiffCallback

class TaskAdapter(private val onItemClick: (TaskEntity) -> Unit) :
    ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(TaskDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(itemView: View, private val onItemClick: (TaskEntity) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val checkBox: MaterialCheckBox = itemView.findViewById(R.id.task_checkbox)
        private val title: TextView = itemView.findViewById(R.id.task_title)
        private val notes: TextView = itemView.findViewById(R.id.task_notes)
        private val priorityChip: Chip = itemView.findViewById(R.id.chip_priority)
        private val dueChip: Chip = itemView.findViewById(R.id.chip_due)
        private var current: TaskEntity? = null

        init {
            itemView.setOnClickListener { current?.let(onItemClick) }
        }

        fun bind(task: TaskEntity) {
            current = task
            checkBox.isChecked = task.isDone
            title.text = task.title
            notes.text = task.description.orEmpty()
            notes.visibility = if (task.description.isNullOrBlank()) View.GONE else View.VISIBLE
            priorityChip.text = task.priority.toString()
            dueChip.text = task.dueAt?.let(DateFormatter::format) ?: ""
            dueChip.visibility = if (task.dueAt != null) View.VISIBLE else View.GONE
        }
    }
}
