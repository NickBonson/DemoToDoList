package nick.bonson.demotodolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import nick.bonson.demotodolist.data.entity.TodoEntity
import nick.bonson.demotodolist.data.repository.TodoRepository
import nick.bonson.demotodolist.model.UiState

class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        viewModelScope.launch {
            repository.getTodos().collect { todos ->
                _uiState.value = _uiState.value.copy(todos = todos)
            }
        }
    }

    fun addTodo(title: String) {
        viewModelScope.launch {
            repository.addTodo(TodoEntity(title = title))
        }
    }
}
