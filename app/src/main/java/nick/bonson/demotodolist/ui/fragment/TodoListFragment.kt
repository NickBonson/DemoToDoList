package nick.bonson.demotodolist.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collect
import nick.bonson.demotodolist.R
import nick.bonson.demotodolist.data.db.AppDatabase
import nick.bonson.demotodolist.data.repository.DefaultTodoRepository
import nick.bonson.demotodolist.ui.adapter.TodoAdapter
import nick.bonson.demotodolist.ui.viewmodel.TodoViewModel
import nick.bonson.demotodolist.ui.viewmodel.TodoViewModelFactory

class TodoListFragment : Fragment(R.layout.fragment_todo_list) {

    private val viewModel: TodoViewModel by viewModels {
        val context = requireContext().applicationContext
        val dao = AppDatabase.getInstance(context).todoDao()
        val repository = DefaultTodoRepository(dao)
        TodoViewModelFactory(repository)
    }

    private val adapter = TodoAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.todo_list)
        val emptyState = view.findViewById<View>(R.id.empty_state)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val window = requireActivity().window
        val surfaceColor = MaterialColors.getColor(view, com.google.android.material.R.attr.colorSurface)
        window.statusBarColor = surfaceColor
        window.navigationBarColor = surfaceColor

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                adapter.submitList(state.todos)
                val isEmpty = state.todos.isEmpty()
                emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                fab.visibility = if (isEmpty) View.GONE else View.VISIBLE
            }
        }
    }
}

