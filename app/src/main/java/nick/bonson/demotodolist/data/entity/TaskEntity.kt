package nick.bonson.demotodolist.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "tasks",
    indices = [
        Index(value = ["isDone"]),
        Index(value = ["priority"]),
        Index(value = ["dueAt"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val isDone: Boolean = false,
    val priority: Int = 0,
    val dueAt: Long? = null
)
