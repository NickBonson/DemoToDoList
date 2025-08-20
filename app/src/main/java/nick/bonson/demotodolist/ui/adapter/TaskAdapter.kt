package nick.bonson.demotodolist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nick.bonson.demotodolist.R
import nick.bonson.demotodolist.data.entity.TaskEntity
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
        private val title: TextView = itemView.findViewById(R.id.task_title)
        private var current: TaskEntity? = null

        init {
            itemView.setOnClickListener { current?.let(onItemClick) }
        }

        fun bind(task: TaskEntity) {
            current = task
            title.text = task.title
        }
    }
}
