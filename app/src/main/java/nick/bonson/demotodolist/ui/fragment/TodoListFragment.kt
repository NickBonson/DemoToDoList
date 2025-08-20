package nick.bonson.demotodolist.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nick.bonson.demotodolist.R
import nick.bonson.demotodolist.data.db.AppDatabase
import nick.bonson.demotodolist.data.repository.DefaultTodoRepository
import nick.bonson.demotodolist.ui.adapter.TodoAdapter
import nick.bonson.demotodolist.ui.viewmodel.TodoViewModel
import nick.bonson.demotodolist.ui.viewmodel.TodoViewModelFactory

class TodoListFragment : Fragment() {

    private val viewModel: TodoViewModel by viewModels {
        val context = requireContext().applicationContext
        val dao = AppDatabase.getInstance(context).todoDao()
        val repository = DefaultTodoRepository(dao)
        TodoViewModelFactory(repository)
    }
    private lateinit var recyclerView: RecyclerView
    private val adapter = TodoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_todo_list, container, false)
        recyclerView = view.findViewById(R.id.todo_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        return view
    }
}
