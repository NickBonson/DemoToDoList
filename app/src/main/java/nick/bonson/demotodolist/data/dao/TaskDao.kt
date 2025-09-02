package nick.bonson.demotodolist.data.dao

import androidx.room.*
import nick.bonson.demotodolist.data.db.TaskSortConverters
import kotlinx.coroutines.flow.Flow
import nick.bonson.demotodolist.data.entity.TaskEntity
import nick.bonson.demotodolist.model.TaskSort

@Dao
@TypeConverters(TaskSortConverters::class)
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query(
        """
        SELECT * FROM tasks
        ORDER BY
            CASE WHEN :sortMode = 0 THEN dueAt END ASC,
            CASE WHEN :sortMode = 1 THEN priority END DESC
        """
    )
    fun getAllFlow(sortMode: TaskSort): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE isDone = :isDone
        ORDER BY
            CASE WHEN :sortMode = 0 THEN dueAt END ASC,
            CASE WHEN :sortMode = 1 THEN priority END DESC
        """
    )
    fun getByStatusFlow(isDone: Boolean, sortMode: TaskSort): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT * FROM tasks
        WHERE title LIKE '%' || :query || '%'
        AND (:filter = 0 OR (:filter = 1 AND isDone = 0) OR (:filter = 2 AND isDone = 1))
        ORDER BY
            CASE WHEN :sortMode = 0 THEN dueAt END ASC,
            CASE WHEN :sortMode = 1 THEN priority END DESC
        """
    )
    fun searchFlow(query: String, filter: Int, sortMode: TaskSort): Flow<List<TaskEntity>>
}
