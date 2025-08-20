package nick.bonson.demotodolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import nick.bonson.demotodolist.data.entity.TaskEntity
import nick.bonson.demotodolist.data.preferences.TaskPreferences
import nick.bonson.demotodolist.data.repository.TaskRepository
import nick.bonson.demotodolist.model.Filter
import nick.bonson.demotodolist.model.TaskListUiState
import nick.bonson.demotodolist.model.TaskSort

/**
 * ViewModel responsible for managing a list of [TaskEntity].
 * It exposes a [StateFlow] of [TaskListUiState] that the UI can observe.
 */
class TaskListViewModel(
    private val repository: TaskRepository,
    private val preferences: TaskPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState

    private var observeJob: Job? = null

    init {
        viewModelScope.launch {
            val filter = preferences.filterFlow.first()
            val sort = preferences.sortFlow.first()
            val query = preferences.queryFlow.first()
            _uiState.value = _uiState.value.copy(filter = filter, sort = sort, query = query)
            observeTasks()
        }
    }

    private fun observeTasks() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            val state = _uiState.value
            val flow = when {
                state.query.isNotEmpty() -> repository.searchFlow(state.query, state.filter, state.sort)
                state.filter == Filter.ALL -> repository.getAllFlow(state.sort)
                else -> repository.getByStatusFlow(
                    isDone = state.filter == Filter.COMPLETED,
                    sortMode = state.sort
                )
            }
            flow.collect { tasks ->
                _uiState.value = _uiState.value.copy(items = tasks, isEmpty = tasks.isEmpty())
            }
        }
    }

    fun onAdd(task: TaskEntity) {
        viewModelScope.launch {
            repository.insert(task)
        }
    }

    fun onEdit(task: TaskEntity) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun onDelete(task: TaskEntity) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    fun onToggleDone(task: TaskEntity) {
        onEdit(task.copy(isDone = !task.isDone))
    }

    fun onSearch(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        viewModelScope.launch { preferences.setQuery(query) }
        observeTasks()
    }

    fun onFilterChanged(filter: Filter) {
        _uiState.value = _uiState.value.copy(filter = filter)
        viewModelScope.launch { preferences.setFilter(filter) }
        observeTasks()
    }

    fun onSortChanged(sort: TaskSort) {
        _uiState.value = _uiState.value.copy(sort = sort)
        viewModelScope.launch { preferences.setSort(sort) }
        observeTasks()
    }
}

