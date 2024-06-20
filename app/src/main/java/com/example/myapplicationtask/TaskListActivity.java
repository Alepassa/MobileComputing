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
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class TaskListActivity extends AppCompatActivity
        implements TaskListFragment.TaskListFragmentCallbacks,
        TaskDetailFragment.OnTaskUpdatedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String STATE_CURRENT_TASK_LIST = "current_task_list_state";

    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActivityListTaskBinding binding;
    private TaskListFragment taskListFragment;
    private TaskDetailFragment taskDetailFragment;
    private TaskViewModel taskViewModel;
    private boolean tabletMode;
    private Menu menu;
    private ActivityResultLauncher<Intent> activityLauncher;
    private int currentTaskListId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState != null) {
            currentTaskListId = savedInstanceState.getInt(STATE_CURRENT_TASK_LIST, -1);
            Log.d("TaskListActivity", "Restored Task List ID from savedInstanceState: " + currentTaskListId);

        }

        handleIntent();
        initializeViewModel();
        initializeUI();
        setupFragments();
        createInitialTaskLists();

        observeTaskLists();
    }

    private void initializeViewModel() {
        taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
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
        Log.d("TaskListActivity", "initializeUI: toolbar initialized = " + (toolbar != null));
        Log.d("TaskListActivity", "initializeUI: drawer initialized = " + (drawer != null));

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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void populateMenuWithTaskLists() {
        taskViewModel.getAllTaskLists().observe(this, taskLists -> {
            menu.clear();
            menu.add(Menu.NONE, R.id.create_task_list, Menu.NONE, "Create Task List").setIcon(R.drawable.add);
            Log.d("TaskListActivity", "populateMenuWithTaskLists: menu size = " + menu.size());

            for (TaskList taskList : taskLists) {
                MenuItem item = menu.add(Menu.NONE, taskList.getId(), Menu.NONE, taskList.getName());

                item.setCheckable(true);
            }
        });
    }

    private void setupFragments() {
        FragmentManager fm = getSupportFragmentManager();
        taskListFragment = getOrCreateFragment(fm, R.id.taskListFragment, TaskListFragment.class);
        tabletMode = binding.taskDetailContainer != null;
        Log.d("TaskListActivity", "setupFragments: tabletMode = " + tabletMode);

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
        Log.d("TaskListActivity", "onStart: currentTaskListId = " + currentTaskListId);

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
        Log.d("TaskListActivity", "loadAndDisplayTasks: taskListId = " + taskListId);

        taskViewModel.getTasksByTaskListId(taskListId).observe(this, tasks -> {
            taskListFragment.updateTasks(tasks);
            updateActionBarTitle(taskListId);
        });
    }

    private void updateActionBarTitle(int taskListId) {
        TaskList taskList = taskViewModel.getAllTaskLists().getValue().stream()
                .filter(t -> t.getId() == taskListId)
                .findFirst()
                .orElse(null);
        if (taskList != null) {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(taskList.getName());
            }
        }
    }

    @Override
    public void onTaskSelected(Task task) {
        Log.d("TaskListActivity", "onTaskSelected: task = " + task);

        if (tabletMode && taskDetailFragment != null) {
            Bundle args = new Bundle();
            args.putParcelable(TaskDetail.TASK_EXTRA, task);
            args.putInt("taskListId", task.getTaskListId());
            taskDetailFragment.setArguments(args);
            taskDetailFragment.displayTask(task);
        } else {
            Intent intent = new Intent(this, TaskDetail.class);
            intent.putExtra(TaskDetail.TASK_EXTRA, task);
            intent.putExtra("taskListId", task.getTaskListId());
            activityLauncher.launch(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.create_task_list) {
            showCreateTaskListDialog();
        } else {
            int newTaskListId = item.getItemId();
            Log.d("TaskListActivity", "Selected Task List ID: " + newTaskListId);

            currentTaskListId = newTaskListId;
            Log.d("TaskListActivity", "Current Task List ID set to: " + currentTaskListId);

            loadAndDisplayTasks(currentTaskListId);
            // Aggiorna il fragment TaskDetailFragment se si è in modalità tablet
            if (tabletMode && taskDetailFragment != null) {
                taskDetailFragment.updateTaskListId(currentTaskListId);
                taskDetailFragment.displayTask(null);
            }
            loadAndDisplayTasks(currentTaskListId);

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

        builder.setPositiveButton("OK", (dialog, which) -> handleNewTaskListCreation(input.getText().toString().trim()));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void handleNewTaskListCreation(String newTaskListName) {
        if (!newTaskListName.isEmpty()) {
            TaskList newTaskList = new TaskList(newTaskListName);
            taskViewModel.insertTaskList(newTaskList);

            taskViewModel.getAllTaskLists().observe(this, taskLists -> {
                for (TaskList taskList : taskLists) {
                    if (taskList.getName().equals(newTaskListName)) {
                        currentTaskListId = taskList.getId();
                        Log.d("TaskListActivity", "New Task List created with ID: " + currentTaskListId);
                        break;
                    }
                }
                loadAndDisplayTasks(currentTaskListId);
                updateActionBarTitle(currentTaskListId);
            });
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
        Log.d("TaskListActivity", "onClearCompletedTasks: currentTaskListId = " + currentTaskListId);

        if (currentTaskListId != -1) {
            taskViewModel.deleteCompletedTasksByTaskListId(currentTaskListId);
        }
    }

    @Override
    public void onUpdateTask(Task updatedTask) {
        Log.d("TaskListActivity", "onUpdateTask: updatedTask = " + updatedTask);

        taskViewModel.updateTask(updatedTask);
        if (currentTaskListId != -1) {
            loadAndDisplayTasks(currentTaskListId);
        }
    }

    private void handleIntent() {
        Log.d("TaskListActivity", "handleIntent: currentTaskListId = " + currentTaskListId);

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

    private void createInitialTaskLists() {
        taskViewModel.getAllTaskLists().observe(this, taskLists -> {
            TaskList groceriesTaskList = null;
            TaskList universityTaskList = null;

            for (TaskList taskList : taskLists) {
                if ("Groceries".equals(taskList.getName())) {
                    groceriesTaskList = taskList;
                } else if ("University".equals(taskList.getName())) {
                    universityTaskList = taskList;
                }
            }

            if (groceriesTaskList == null) {
                groceriesTaskList = new TaskList("Groceries");
                taskViewModel.insertTaskList(groceriesTaskList);
            }
            if (universityTaskList == null) {
                universityTaskList = new TaskList("University");
                taskViewModel.insertTaskList(universityTaskList);
            }
        });
    }

    private void observeTaskLists() {
        taskViewModel.getAllTaskLists().observe(this, taskLists -> {
            if (currentTaskListId == -1 && !taskLists.isEmpty()) {
                TaskList firstTaskList = taskLists.get(0);
                currentTaskListId = firstTaskList.getId();

                loadAndDisplayTasks(currentTaskListId);
                updateActionBarTitle(currentTaskListId);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taskViewModel.getAllTaskLists().removeObservers(this);
    }

    private void showDeleteTaskListDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task List")
                .setMessage("Do you really want to delete this task list?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    taskViewModel.deleteTaskListById(currentTaskListId);
                    currentTaskListId = -1;
                    updateActionBarTitle(currentTaskListId);
                    loadAndDisplayTasks(currentTaskListId);
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete_task_list) {
            showDeleteTaskListDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}