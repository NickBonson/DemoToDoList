package nick.bonson.demotodolist.model

import nick.bonson.demotodolist.data.entity.TaskEntity

// Holds UI state for displaying a list of tasks
// items - list of tasks, filter - current filter, sort - sorting mode, query - search query, isEmpty - whether list is empty
// isEmpty is maintained separately to simplify observing empty state in UI

data class TaskListUiState(
    val items: List<TaskEntity> = emptyList(),
    val filter: Filter = Filter.ALL,
    val sort: TaskSort = TaskSort.BY_DUE_AT,
    val query: String = "",
    val isEmpty: Boolean = true
)

