package nick.bonson.demotodolist.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nick.bonson.demotodolist.model.Filter
import nick.bonson.demotodolist.model.TaskSort

private val Context.dataStore by preferencesDataStore(name = "task_prefs")

/**
 * Handles persistence of task list UI state using [androidx.datastore.preferences.core.PreferencesDataStore].
 */
class TaskPreferences(private val context: Context) {

    private object Keys {
        val FILTER = stringPreferencesKey("filter")
        val SORT = stringPreferencesKey("sort")
        val QUERY = stringPreferencesKey("query")
    }

    val filterFlow: Flow<Filter> = context.dataStore.data.map { prefs ->
        Filter.valueOf(prefs[Keys.FILTER] ?: Filter.ALL.name)
    }

    val sortFlow: Flow<TaskSort> = context.dataStore.data.map { prefs ->
        TaskSort.valueOf(prefs[Keys.SORT] ?: TaskSort.BY_DUE_AT.name)
    }

    val queryFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.QUERY] ?: ""
    }

    suspend fun setFilter(filter: Filter) {
        context.dataStore.edit { it[Keys.FILTER] = filter.name }
    }

    suspend fun setSort(sort: TaskSort) {
        context.dataStore.edit { it[Keys.SORT] = sort.name }
    }

    suspend fun setQuery(query: String) {
        context.dataStore.edit { it[Keys.QUERY] = query }
    }
}

