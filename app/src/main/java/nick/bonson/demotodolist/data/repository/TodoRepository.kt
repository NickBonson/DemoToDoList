package nick.bonson.demotodolist.data.repository

import kotlinx.coroutines.flow.Flow
import nick.bonson.demotodolist.data.entity.TodoEntity

interface TodoRepository {
    fun getTodos(): Flow<List<TodoEntity>>
    suspend fun addTodo(todo: TodoEntity)
    suspend fun deleteTodo(todo: TodoEntity)
}

class DefaultTodoRepository(private val dao: nick.bonson.demotodolist.data.dao.TodoDao) : TodoRepository {
    override fun getTodos(): Flow<List<TodoEntity>> = dao.getTodos()
    override suspend fun addTodo(todo: TodoEntity) = dao.insert(todo)
    override suspend fun deleteTodo(todo: TodoEntity) = dao.delete(todo)
}
