package nick.bonson.demotodolist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nick.bonson.demotodolist.data.entity.TaskEntity
import nick.bonson.demotodolist.databinding.ItemTaskBinding
import nick.bonson.demotodolist.utils.DateFormatter
import nick.bonson.demotodolist.utils.TaskDiffCallback

class TaskAdapter(
    private val onItemClick: (TaskEntity) -> Unit,
    private val onCheckboxClick: (TaskEntity) -> Unit
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(TaskDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding, onItemClick, onCheckboxClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TaskViewHolder(
        private val binding: ItemTaskBinding,
        private val onItemClick: (TaskEntity) -> Unit,
        private val onCheckboxClick: (TaskEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var current: TaskEntity? = null

        init {
            binding.root.setOnClickListener { current?.let(onItemClick) }
        }

        fun bind(task: TaskEntity) {
            current = task
            binding.taskCheckbox.setOnCheckedChangeListener(null)
            binding.taskCheckbox.isChecked = task.isDone
            binding.taskCheckbox.setOnCheckedChangeListener { _, _ ->
                onCheckboxClick(task)
            }
            binding.taskTitle.text = task.title
            binding.taskNotes.text = task.description.orEmpty()
            binding.taskNotes.visibility =
                if (task.description.isNullOrBlank()) View.GONE else View.VISIBLE
            binding.chipPriority.text = task.priority.toString()
            binding.chipDue.text = task.dueAt?.let(DateFormatter::format) ?: ""
            binding.chipDue.visibility =
                if (task.dueAt != null) View.VISIBLE else View.GONE
        }
    }
}
