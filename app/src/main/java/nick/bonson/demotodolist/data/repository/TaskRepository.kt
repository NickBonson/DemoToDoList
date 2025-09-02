package nick.bonson.demotodolist.data.repository

import kotlinx.coroutines.flow.Flow
import nick.bonson.demotodolist.data.dao.TaskDao
import nick.bonson.demotodolist.data.entity.TaskEntity
import nick.bonson.demotodolist.model.Filter
import nick.bonson.demotodolist.model.TaskSort

interface TaskRepository {
    fun getAllFlow(sortMode: TaskSort): Flow<List<TaskEntity>>
    fun getByStatusFlow(isDone: Boolean, sortMode: TaskSort): Flow<List<TaskEntity>>
    fun searchFlow(query: String, filter: Filter, sortMode: TaskSort): Flow<List<TaskEntity>>
    suspend fun insert(task: TaskEntity)
    suspend fun update(task: TaskEntity)
    suspend fun delete(task: TaskEntity)
}

class DefaultTaskRepository(private val dao: TaskDao) : TaskRepository {
    override fun getAllFlow(sortMode: TaskSort): Flow<List<TaskEntity>> = dao.getAllFlow(sortMode)

    override fun getByStatusFlow(isDone: Boolean, sortMode: TaskSort): Flow<List<TaskEntity>> =
        dao.getByStatusFlow(isDone, sortMode)

    override fun searchFlow(query: String, filter: Filter, sortMode: TaskSort): Flow<List<TaskEntity>> =
        dao.searchFlow(query, filter.ordinal, sortMode)

    override suspend fun insert(task: TaskEntity) = dao.insert(task)

    override suspend fun update(task: TaskEntity) = dao.update(task)

    override suspend fun delete(task: TaskEntity) = dao.delete(task)
}
