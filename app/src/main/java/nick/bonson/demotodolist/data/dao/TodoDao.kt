package nick.bonson.demotodolist.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import nick.bonson.demotodolist.data.entity.TodoEntity

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getTodos(): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)
}
