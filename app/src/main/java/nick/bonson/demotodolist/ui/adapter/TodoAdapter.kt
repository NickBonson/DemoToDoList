package nick.bonson.demotodolist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import nick.bonson.demotodolist.R
import nick.bonson.demotodolist.data.entity.TodoEntity
import nick.bonson.demotodolist.utils.TodoDiffCallback

class TodoAdapter : ListAdapter<TodoEntity, TodoAdapter.TodoViewHolder>(TodoDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.todo_title)
        fun bind(todo: TodoEntity) {
            title.text = todo.title
        }
    }
}
