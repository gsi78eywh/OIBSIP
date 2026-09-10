package com.oibsip.todo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.oibsip.unitconverter.R;
import com.oibsip.todo.adapter.TaskAdapter;
import com.oibsip.todo.db.TodoDbHelper;
import com.oibsip.todo.model.TaskItem;
import com.oibsip.todo.session.SessionManager;

import java.util.List;

public class TodoListActivity extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {

    private TextView tvUserGreeting;
    private TextView tvTaskCounter;
    private ImageButton btnLogout;
    private RecyclerView rvTasks;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddTask;
    private MaterialButtonToggleGroup toggleGroupFilter;

    private TaskAdapter taskAdapter;
    private TodoDbHelper dbHelper;
    private SessionManager sessionManager;

    private long currentUserId;
    private String currentFilter = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_todo_list);

        dbHelper = new TodoDbHelper(this);
        currentUserId = sessionManager.getUserId();

        initViews();
        setupRecyclerView();
        setupListeners();
        loadTasks();
    }

    private void initViews() {
        tvUserGreeting = findViewById(R.id.tvUserGreeting);
        tvTaskCounter = findViewById(R.id.tvTaskCounter);
        btnLogout = findViewById(R.id.btnLogout);
        rvTasks = findViewById(R.id.rvTasks);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabAddTask = findViewById(R.id.fabAddTask);
        toggleGroupFilter = findViewById(R.id.toggleGroupFilter);

        tvUserGreeting.setText("Hello, " + sessionManager.getUserName() + "!");
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(this);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);
    }

    private void setupListeners() {
        btnLogout.setOnClickListener(v -> showLogoutDialog());
        fabAddTask.setOnClickListener(v -> showAddTaskDialog());

        if (toggleGroupFilter != null) {
            toggleGroupFilter.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                if (isChecked) {
                    if (checkedId == R.id.btnFilterPending) {
                        currentFilter = "PENDING";
                    } else if (checkedId == R.id.btnFilterCompleted) {
                        currentFilter = "COMPLETED";
                    } else {
                        currentFilter = "ALL";
                    }
                    loadTasks();
                }
            });
        }
    }

    private void loadTasks() {
        List<TaskItem> tasks = dbHelper.getTasksForUser(currentUserId, currentFilter);
        taskAdapter.setTasks(tasks);

        if (tasks.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvTasks.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvTasks.setVisibility(View.VISIBLE);
        }

        int[] counts = dbHelper.getTaskCounts(currentUserId);
        tvTaskCounter.setText(counts[0] + " Pending  •  " + counts[1] + " Completed");
    }

    @Override
    public void onStatusToggled(TaskItem task, boolean isCompleted) {
        dbHelper.updateTaskStatus(task.getId(), isCompleted);
        loadTasks();
    }

    @Override
    public void onDeleteRequested(TaskItem task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to permanently delete \"" + task.getTitle() + "\"?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteTask(task.getId());
                    Toast.makeText(TodoListActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                    loadTasks();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddTaskDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);

        EditText etTitle = dialogView.findViewById(R.id.etDialogTaskTitle);
        EditText etNotes = dialogView.findViewById(R.id.etDialogTaskNotes);
        Button btnSave = dialogView.findViewById(R.id.btnDialogSave);
        Button btnCancel = dialogView.findViewById(R.id.btnDialogCancel);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String notes = etNotes.getText().toString().trim();

            if (title.isEmpty()) {
                etTitle.setError("Task name cannot be empty");
                etTitle.requestFocus();
                return;
            }

            long newId = dbHelper.insertTask(currentUserId, title, notes);
            if (newId > 0) {
                Toast.makeText(TodoListActivity.this, "Task added!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                loadTasks();
            } else {
                Toast.makeText(TodoListActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    sessionManager.logout();
                    Toast.makeText(TodoListActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    redirectToLogin();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(TodoListActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
