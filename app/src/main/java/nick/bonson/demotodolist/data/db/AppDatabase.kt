package nick.bonson.demotodolist.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import nick.bonson.demotodolist.data.dao.TaskDao
import nick.bonson.demotodolist.data.dao.TodoDao
import nick.bonson.demotodolist.data.entity.TaskEntity
import nick.bonson.demotodolist.data.entity.TodoEntity

@Database(entities = [TodoEntity::class, TaskEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "todo_db"
                ).build().also { INSTANCE = it }
            }
    }
}
