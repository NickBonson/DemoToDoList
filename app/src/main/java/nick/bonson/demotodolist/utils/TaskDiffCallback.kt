package nick.bonson.demotodolist.utils

import androidx.recyclerview.widget.DiffUtil
import nick.bonson.demotodolist.data.entity.TaskEntity

object TaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
    override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean = oldItem == newItem
}
