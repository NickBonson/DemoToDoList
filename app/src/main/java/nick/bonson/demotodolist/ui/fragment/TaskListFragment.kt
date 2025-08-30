package nick.bonson.demotodolist.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import nick.bonson.demotodolist.R
import nick.bonson.demotodolist.data.db.AppDatabase
import nick.bonson.demotodolist.data.entity.TaskEntity
import nick.bonson.demotodolist.data.preferences.TaskPreferences
import nick.bonson.demotodolist.data.repository.DefaultTaskRepository
import nick.bonson.demotodolist.model.Filter
import nick.bonson.demotodolist.model.TaskSort
import nick.bonson.demotodolist.ui.adapter.TaskAdapter
import nick.bonson.demotodolist.ui.viewmodel.TaskListViewModel
import nick.bonson.demotodolist.ui.viewmodel.TaskListViewModelFactory

class TaskListFragment : Fragment(R.layout.fragment_task_list) {

    private val viewModel: TaskListViewModel by viewModels {
        val context = requireContext().applicationContext
        val dao = AppDatabase.getInstance(context).taskDao()
        val repository = DefaultTaskRepository(dao)
        val prefs = TaskPreferences(context)
        TaskListViewModelFactory(repository, prefs)
    }

    private val adapter = TaskAdapter { task -> showTaskSheet(task) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.top_app_bar)
        val searchView = view.findViewById<SearchView>(R.id.search_view)
        val chipGroup = view.findViewById<ChipGroup>(R.id.filter_group)
        val recyclerView = view.findViewById<RecyclerView>(R.id.task_list)
        val emptyState = view.findViewById<View>(R.id.empty_state)
        val emptyButton = view.findViewById<Button>(R.id.empty_button)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        toolbar.inflateMenu(R.menu.menu_task_list)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_sort_due -> {
                    viewModel.onSortChanged(TaskSort.BY_DUE_AT)
                    true
                }
                R.id.action_sort_priority -> {
                    viewModel.onSortChanged(TaskSort.BY_PRIORITY)
                    true
                }
                else -> false
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.items)

                val isEmpty = state.isEmpty
                Log.d("isEmpty", "isEmpty: $isEmpty")
                emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                fab.visibility = if (isEmpty) View.GONE else View.VISIBLE

                //emptyState.visibility = if (state.isEmpty) View.VISIBLE else View.GONE
                when (state.filter) {
                    Filter.ALL -> chipGroup.check(R.id.chip_all)
                    Filter.ACTIVE -> chipGroup.check(R.id.chip_active)
                    Filter.COMPLETED -> chipGroup.check(R.id.chip_done)
                }
                if (searchView.query.toString() != state.query) {
                    searchView.setQuery(state.query, false)
                }
                when (state.sort) {
                    TaskSort.BY_DUE_AT -> toolbar.menu.findItem(R.id.action_sort_due).isChecked = true
                    TaskSort.BY_PRIORITY -> toolbar.menu.findItem(R.id.action_sort_priority).isChecked = true
                }
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.onSearch(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearch(newText.orEmpty())
                return true
            }
        })

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val filter = when (checkedId) {
                R.id.chip_active -> Filter.ACTIVE
                R.id.chip_done -> Filter.COMPLETED
                else -> Filter.ALL
            }
            viewModel.onFilterChanged(filter)
        }

        fab.setOnClickListener { showTaskSheet(null) }

        emptyButton.setOnClickListener { fab.performClick() }

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = adapter.currentList[viewHolder.adapterPosition]
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        viewModel.onDelete(task)
                        Snackbar.make(recyclerView, R.string.no_tasks, Snackbar.LENGTH_LONG)
                            .setAction(R.string.add_task) { viewModel.onAdd(task) }
                            .show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        viewModel.onToggleDone(task)
                    }
                }
                adapter.notifyItemChanged(viewHolder.adapterPosition)
            }
        })
        touchHelper.attachToRecyclerView(recyclerView)
    }

    private fun showTaskSheet(task: TaskEntity? = null) {
        TaskEditBottomSheet.newInstance(task).show(childFragmentManager, "TaskEdit")
    }
}
