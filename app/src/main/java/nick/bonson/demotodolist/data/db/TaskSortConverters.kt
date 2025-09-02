package nick.bonson.demotodolist.data.db

import androidx.room.TypeConverter
import nick.bonson.demotodolist.model.TaskSort

class TaskSortConverters {
    @TypeConverter
    fun fromTaskSort(sort: TaskSort): Int = sort.ordinal

    @TypeConverter
    fun toTaskSort(value: Int): TaskSort = TaskSort.values()[value]
}

