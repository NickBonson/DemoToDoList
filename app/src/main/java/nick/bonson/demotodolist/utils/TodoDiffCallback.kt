package nick.bonson.demotodolist.utils

import androidx.recyclerview.widget.DiffUtil
import nick.bonson.demotodolist.data.entity.TodoEntity

object TodoDiffCallback : DiffUtil.ItemCallback<TodoEntity>() {
    override fun areItemsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: TodoEntity, newItem: TodoEntity): Boolean = oldItem == newItem
}
