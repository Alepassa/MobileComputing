package com.example.myapplicationtask;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskListActivity extends AppCompatActivity
        implements TaskListFragment.TaskListFragmentCallbacks,
        TaskDetailFragment.OnTaskUpdatedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private ActivityListTaskBinding binding;
    private String currentTaskListName;
    private TaskListFragment taskListFragment;
    private TaskDetailFragment taskDetailFragment;
    private TaskRepository taskRepository;
    private boolean tabletMode;
    private Menu menu;

    private static final String STATE_CURRENT_TASK_LIST = "current_task_list_state";
    private ActivityResultLauncher<Intent> activityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handleIntent();
        initializeRepository();
        initializeTaskLists(savedInstanceState);
        initializeUI();
        setupFragments();
    }

    private void initializeRepository() {
        taskRepository = new TaskRepositoryDatabaseImpl(getApplication());
    }

    private void initializeTaskLists(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            currentTaskListName = savedInstanceState.getString(STATE_CURRENT_TASK_LIST);
        } else {
            currentTaskListName = ""; // Default list name if none exists
        }
    }

    private void initializeUI() {
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        navigationView.setNavigationItemSelectedListener(this);
        setupDrawerToggle();

        menu = navigationView.getMenu();
        populateMenuWithTaskLists();

        FloatingActionButton fab = binding.fab;
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Log.d("TaskListActivity", "FAB clicked");
                if (!tabletMode) {
                    Intent intent = new Intent(TaskListActivity.this, TaskDetail.class);
                    intent.putExtra("taskListName", currentTaskListName);
                    activityLauncher.launch(intent); // Use the registered launcher
                } else {
                    if (taskDetailFragment != null) {
                        taskDetailFragment.displayTask(null);
                    }
                }
            });
        } else {
            Log.e("TaskListActivity", "FAB is null");
        }
    }

    private void setupDrawerToggle() {
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void populateMenuWithTaskLists() {
        taskRepository.getTaskLists().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> taskListNames) {
                menu.clear();
                menu.add(Menu.NONE, R.id.create_task_list, Menu.NONE, "Create Task List").setIcon(R.drawable.add);
                for (String taskListName : taskListNames) {
                    MenuItem item = menu.add(taskListName);
                    item.setCheckable(false);
                }
            }
        });
    }

    private void setupFragments() {
        FragmentManager fm = getSupportFragmentManager();
        taskListFragment = getOrCreateFragment(fm, R.id.taskListFragment, TaskListFragment.class);
        tabletMode = binding.taskDetailContainer != null;

        if (tabletMode) {
            taskDetailFragment = getOrCreateFragment(fm, R.id.taskDetailContainer, TaskDetailFragment.class);
            taskDetailFragment.setOnTaskUpdatedListener(this);
        }
    }

    private <T extends androidx.fragment.app.Fragment> T getOrCreateFragment(FragmentManager fm, int containerId, Class<T> fragmentClass) {
        T fragment = (T) fm.findFragmentById(containerId);
        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
                fm.beginTransaction().add(containerId, fragment).commit();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!currentTaskListName.isEmpty()) {
            loadAndDisplayTasks(currentTaskListName);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_TASK_LIST, currentTaskListName);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentTaskListName = savedInstanceState.getString(STATE_CURRENT_TASK_LIST);
    }

    private void loadAndDisplayTasks(String taskListName) {
        taskRepository.loadTasks(taskListName).observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                // Only update tasks in the fragment if the list name is not empty
                if (!taskListName.isEmpty()) {
                    taskListFragment.updateTasks(tasks);
                }
                updateActionBarTitle(taskListName);
            }
        });
    }

    private void updateActionBarTitle(String taskListName) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(taskListName);
        }
    }

    @Override
    public void onTaskSelected(Task task) {
        if (tabletMode) {
            if (taskDetailFragment != null) {
                taskDetailFragment.displayTask(task);
            }
        } else {
            Intent intent = new Intent(this, TaskDetail.class);
            intent.putExtra("TASK_EXTRA", task);
            activityLauncher.launch(intent); // Use the registered launcher
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.create_task_list) {
            showCreateTaskListDialog();
        } else {
            String selectedTaskList = item.getTitle().toString();
            currentTaskListName = selectedTaskList;
            loadAndDisplayTasks(currentTaskListName);

            //reset when category changes
            if (tabletMode && taskDetailFragment != null) {
                taskDetailFragment.displayTask(null);
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showCreateTaskListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Task List");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newTaskListName = input.getText().toString().trim();
            handleNewTaskListCreation(newTaskListName);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void handleNewTaskListCreation(String newTaskListName) {
        if (!newTaskListName.isEmpty()) {
            Task newTaskList = new Task(newTaskListName, "", "", false); // Create a dummy task for new task list
            taskRepository.insert(newTaskList); // Insert the new task list into the database
        } else {
            showErrorForTaskListCreation(newTaskListName);
        }
    }

    private void showErrorForTaskListCreation(String taskListName) {
        AlertDialog.Builder errorBuilder = new AlertDialog.Builder(this);
        if (taskListName.isEmpty()) {
            errorBuilder.setMessage("Task list name cannot be empty");
        } else {
            errorBuilder.setMessage("Task list name already exists");
        }
        errorBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        errorBuilder.show();
    }

    @Override
    public void onClearCompletedTasks() {
        taskRepository.deleteCompletedTasks();
    }

    @Override
    public void onUpdateTask(Task updatedTask) {
        taskRepository.update(updatedTask);
    }

    private void handleIntent() {
        activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task taskReceived = result.getData().getParcelableExtra(TaskDetail.TASK_EXTRA);
                        Log.d("TaskListActivity", "Result received. Task: " + (taskReceived != null ? taskReceived.toString() : "null"));
                        if (taskReceived != null) {
                            Log.d("TaskListActivity", "Received task: " + taskReceived.toString());
                            taskRepository.update(taskReceived);
                        } else {
                            Log.d("TaskListActivity", "Task is null");
                        }
                    } else {
                        Log.d("TaskListActivity", "Result not OK or data is null");
                    }
                }
        );
    }
}
