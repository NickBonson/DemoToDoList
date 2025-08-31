package nick.bonson.demotodolist.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import nick.bonson.demotodolist.R
import nick.bonson.demotodolist.data.db.AppDatabase
import nick.bonson.demotodolist.data.entity.TaskEntity
import nick.bonson.demotodolist.data.preferences.TaskPreferences
import nick.bonson.demotodolist.data.repository.DefaultTaskRepository
import nick.bonson.demotodolist.databinding.FragmentTaskEditBinding
import nick.bonson.demotodolist.ui.viewmodel.TaskListViewModel
import nick.bonson.demotodolist.ui.viewmodel.TaskListViewModelFactory
import nick.bonson.demotodolist.utils.DateFormatter
import java.util.Calendar

class TaskEditFragment : Fragment() {

    private var _binding: FragmentTaskEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by activityViewModels {
        val context = requireContext().applicationContext
        val dao = AppDatabase.getInstance(context).taskDao()
        val repository = DefaultTaskRepository(dao)
        val prefs = TaskPreferences(context)
        TaskListViewModelFactory(repository, prefs)
    }

    private var taskId: Long = 0L
    private var isDone: Boolean = false
    private var priority: Int = 0
    private var dueAt: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            taskId = it.getLong(ARG_ID, 0L)
            isDone = it.getBoolean(ARG_DONE, false)
            priority = it.getInt(ARG_PRIORITY, 0)
            if (it.containsKey(ARG_DUE_AT)) {
                dueAt = it.getLong(ARG_DUE_AT)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTaskEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val titleInput = binding.inputTitle
        val notesInput = binding.inputNotes
        val priorityInput = binding.inputPriority
        val dueDateInput = binding.inputDueDate
        val saveButton = binding.buttonSave
        val cancelButton = binding.buttonCancel

        titleInput.setText(arguments?.getString(ARG_TITLE).orEmpty())
        notesInput.setText(arguments?.getString(ARG_NOTES).orEmpty())

        val priorities = resources.getStringArray(R.array.priority_entries)
        priorityInput.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, priorities)
        )
        priorityInput.setText(priorities[priority], false)
        priorityInput.setOnItemClickListener { _, _, position, _ -> priority = position }

        dueAt?.let {
            dueDateInput.setText(DateFormatter.format(it))
        }
        dueDateInput.setOnClickListener {
            val initialCalendar = Calendar.getInstance().apply {
                dueAt?.let { timeInMillis = it }
            }

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val selected = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    dueAt = selected.timeInMillis
                    dueDateInput.setText(DateFormatter.format(dueAt!!))
                },
                initialCalendar.get(Calendar.YEAR),
                initialCalendar.get(Calendar.MONTH),
                initialCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        saveButton.setOnClickListener {
            val title = titleInput.text?.toString()?.trim()
            if (title.isNullOrEmpty()) {
                titleInput.error = getString(R.string.error_required)
                return@setOnClickListener
            }
            val description = notesInput.text?.toString()?.trim().takeIf { it?.isNotEmpty() == true }
            val task = TaskEntity(
                id = taskId,
                title = title,
                description = description,
                isDone = isDone,
                priority = priority,
                dueAt = dueAt
            )
            if (taskId == 0L) {
                viewModel.onAdd(task)
            } else {
                viewModel.onEdit(task)
            }
            parentFragmentManager.popBackStack()
        }

        cancelButton.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_TITLE = "title"
        private const val ARG_NOTES = "notes"
        private const val ARG_PRIORITY = "priority"
        private const val ARG_DUE_AT = "due_at"
        private const val ARG_DONE = "done"

        fun newInstance(task: TaskEntity? = null): TaskEditFragment {
            val fragment = TaskEditFragment()
            fragment.arguments = Bundle().apply {
                if (task != null) {
                    putLong(ARG_ID, task.id)
                    putString(ARG_TITLE, task.title)
                    putString(ARG_NOTES, task.description)
                    putInt(ARG_PRIORITY, task.priority)
                    task.dueAt?.let { putLong(ARG_DUE_AT, it) }
                    putBoolean(ARG_DONE, task.isDone)
                }
            }
            return fragment
        }
    }
}

