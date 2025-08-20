package nick.bonson.demotodolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import nick.bonson.demotodolist.data.preferences.TaskPreferences
import nick.bonson.demotodolist.data.repository.TaskRepository

class TaskListViewModelFactory(
    private val repository: TaskRepository,
    private val preferences: TaskPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListViewModel(repository, preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
