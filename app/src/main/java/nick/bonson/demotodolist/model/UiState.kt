package nick.bonson.demotodolist.model

import nick.bonson.demotodolist.data.entity.TodoEntity

data class UiState(
    val todos: List<TodoEntity> = emptyList(),
    val filter: Filter = Filter.ALL,
    val sort: Sort = Sort.BY_DATE
)
