package com.example.myapplicationtask;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class TaskListActivity extends AppCompatActivity
        implements TaskListFragment.TaskListFragmentCallbacks,
        TaskDetailFragment.OnTaskUpdatedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private ActivityListTaskBinding binding;
    private TaskListFragment taskListFragment;
    private TaskDetailFragment taskDetailFragment;
    private TaskViewModel taskViewModel;
    private boolean tabletMode;
    private Menu menu;

    private static final String STATE_CURRENT_TASK_LIST = "current_task_list_state";
    private ActivityResultLauncher<Intent> activityLauncher;
    private int currentTaskListId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handleIntent();
        initializeViewModel();
        initializeUI();
        setupFragments();
        createExampleTaskListAndTasks();
    }

    private void initializeViewModel() {
        taskViewModel = new TaskViewModel(getApplication());
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
                if (currentTaskListId == -1) {
                    Log.e("TaskListActivity", "No TaskList selected. Cannot create Task.");
                    return;
                }
                if (!tabletMode) {
                    Intent intent = new Intent(TaskListActivity.this, TaskDetail.class);
                    intent.putExtra("taskListId", currentTaskListId);
                    activityLauncher.launch(intent);
                } else {
                    if (taskDetailFragment != null) {
                        Bundle args = new Bundle();
                        args.putInt("taskListId", currentTaskListId);
                        taskDetailFragment.setArguments(args);
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
        taskViewModel.getAllTaskLists().observe(this, new Observer<List<TaskList>>() {
            @Override
            public void onChanged(List<TaskList> taskListNames) {
                menu.clear();
                menu.add(Menu.NONE, R.id.create_task_list, Menu.NONE, "Create Task List").setIcon(R.drawable.add);
                for (TaskList taskList : taskListNames) {
                    MenuItem item = menu.add(Menu.NONE, taskList.getId(), Menu.NONE, taskList.getName());
                    item.setCheckable(true);
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
        if (currentTaskListId != -1) {
            loadAndDisplayTasks(currentTaskListId);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CURRENT_TASK_LIST, currentTaskListId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentTaskListId = savedInstanceState.getInt(STATE_CURRENT_TASK_LIST, -1);
    }

    private void loadAndDisplayTasks(int taskListId) {
        taskViewModel.getTasksByTaskListId(taskListId).observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                taskListFragment.updateTasks(tasks);
                updateActionBarTitle(taskListId);
            }
        });
    }

    private void updateActionBarTitle(int taskListId) {
        TaskList taskList = taskViewModel.getAllTaskLists().getValue().stream().filter(t -> t.getId() == taskListId).findFirst().orElse(null);
        if (taskList != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(taskList.getName());
            }
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
            activityLauncher.launch(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.create_task_list) {
            showCreateTaskListDialog();
        } else {
            currentTaskListId = item.getItemId();
            loadAndDisplayTasks(currentTaskListId);

            if (tabletMode && taskDetailFragment != null) {
                taskDetailFragment.displayTask(null);
                Bundle args = new Bundle();
                args.putInt("taskListId", currentTaskListId);
                taskDetailFragment.setArguments(args);
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
            TaskList newTaskList = new TaskList(newTaskListName);
            taskViewModel.insertTaskList(newTaskList);
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
        taskViewModel.deleteCompletedTasks();
    }

    @Override
    public void onUpdateTask(Task updatedTask) {
        taskViewModel.updateTask(updatedTask);
        if (currentTaskListId != -1) {
            loadAndDisplayTasks(currentTaskListId);
        }
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
                            taskViewModel.updateTask(taskReceived);
                            if (currentTaskListId != -1) {
                                loadAndDisplayTasks(currentTaskListId);
                            }
                        } else {
                            Log.d("TaskListActivity", "Task is null");
                        }
                    } else {
                        Log.d("TaskListActivity", "Result not OK or data is null");
                    }
                }
        );
    }

    private void createExampleTaskListAndTasks() {
        taskViewModel.getAllTaskLists().observe(this, new Observer<List<TaskList>>() {
            @Override
            public void onChanged(List<TaskList> taskLists) {
                TaskList exampleTaskList = null;
                for (TaskList taskList : taskLists) {
                    if (taskList.getName().equals("Example Task List")) {
                        exampleTaskList = taskList;
                        break;
                    }
                }
                if (exampleTaskList == null) {
                    exampleTaskList = new TaskList("Example Task List");
                    taskViewModel.insertTaskList(exampleTaskList);
                    taskViewModel.getAllTaskLists().removeObserver(this); // Remove the observer to prevent repeated insertions
                }
            }
        });

        taskViewModel.getAllTaskLists().observe(this, new Observer<List<TaskList>>() {
            @Override
            public void onChanged(List<TaskList> taskLists) {
                for (TaskList taskList : taskLists) {
                    if (taskList.getName().equals("Example Task List")) {
                        int exampleTaskListId = taskList.getId();
                        Task task1 = new Task("Example Task 1", "Description for Task 1", "Jan 1, 2023", false, exampleTaskListId);
                        Task task2 = new Task("Example Task 2", "Description for Task 2", "Jan 2, 2023", false, exampleTaskListId);
                        taskViewModel.insertTask(task1);
                        taskViewModel.insertTask(task2);
                        break;
                    }
                }
            }
        });
    }
}
