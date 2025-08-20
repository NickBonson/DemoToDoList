package nick.bonson.demotodolist.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.activityViewModels
import nick.bonson.demotodolist.R
import nick.bonson.demotodolist.data.db.AppDatabase
import nick.bonson.demotodolist.data.entity.TaskEntity
import nick.bonson.demotodolist.data.repository.DefaultTaskRepository
import nick.bonson.demotodolist.data.preferences.TaskPreferences
import nick.bonson.demotodolist.ui.viewmodel.TaskListViewModel
import nick.bonson.demotodolist.ui.viewmodel.TaskListViewModelFactory
import nick.bonson.demotodolist.utils.DateFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class TaskEditBottomSheet : BottomSheetDialogFragment() {

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
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.dialog_task_edit, container, false)
        val titleInput = view.findViewById<TextInputEditText>(R.id.input_title)
        val notesInput = view.findViewById<TextInputEditText>(R.id.input_notes)
        val priorityInput = view.findViewById<AutoCompleteTextView>(R.id.input_priority)
        val dueDateInput = view.findViewById<TextInputEditText>(R.id.input_due_date)
        val saveButton = view.findViewById<Button>(R.id.button_save)
        val cancelButton = view.findViewById<Button>(R.id.button_cancel)

        titleInput.setText(arguments?.getString(ARG_TITLE).orEmpty())
        notesInput.setText(arguments?.getString(ARG_NOTES).orEmpty())

        val priorities = resources.getStringArray(R.array.priority_entries)
        priorityInput.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, priorities))
        priorityInput.setText(priorities[priority], false)
        priorityInput.setOnItemClickListener { _, _, position, _ -> priority = position }

        dueAt?.let {
            dueDateInput.setText(DateFormatter.format(it))
        }
        dueDateInput.setOnClickListener {
            val initial = dueAt?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            } ?: LocalDate.now()

            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val date = LocalDate.of(year, month + 1, day)
                    dueAt = date
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                    dueDateInput.setText(DateFormatter.format(dueAt!!))
                },
                initial.year,
                initial.monthValue - 1,
                initial.dayOfMonth
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
            dismiss()
        }

        cancelButton.setOnClickListener { dismiss() }

        return view
    }

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_TITLE = "title"
        private const val ARG_NOTES = "notes"
        private const val ARG_PRIORITY = "priority"
        private const val ARG_DUE_AT = "due_at"
        private const val ARG_DONE = "done"

        fun newInstance(task: TaskEntity? = null): TaskEditBottomSheet {
            val fragment = TaskEditBottomSheet()
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
