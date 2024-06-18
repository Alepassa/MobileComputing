package com.example.myapplicationtask;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import com.example.myapplicationtask.databinding.NavHeaderBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TaskListActivity extends AppCompatActivity
        implements TaskListFragment.TaskListFragmentCallbacks,
        TaskDetailFragment.OnTaskUpdatedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private ActivityListTaskBinding binding;
    private TaskRepository taskRepository;
    private boolean tabletMode;
    private Menu menu;
    private TaskListFragment taskListFragment;
    private TaskDetailFragment taskDetailFragment;

    private static final String STATE_TASK_LISTS = "task_lists_state";
    private static final String STATE_CURRENT_TASK_LIST = "current_task_list_state";
    private ActivityResultLauncher<Intent> activityLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handleIntent();
        initializeRepository();
        initializeUI();
        setupFragments();

        // Test data
        testDatabaseOperations();
    }

    private void initializeRepository() {
        taskRepository = new TaskRepositoryDatabaseImpl(getApplication());
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
                    intent.putExtra("taskListName", "default");
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
        menu.clear();

        menu.add(Menu.NONE, R.id.create_task_list, Menu.NONE, "Create Task List").setIcon(R.drawable.add);
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
        loadAndDisplayTasks();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void loadAndDisplayTasks() {
        taskRepository.getAllTasks().observe(this, tasks -> {
            taskListFragment.updateTasks(tasks);
            updateActionBarTitle("Task List");
        });
    }

    private void updateActionBarTitle(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
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
            Task task = new Task(newTaskListName);
            taskRepository.insert(task);
            loadAndDisplayTasks();
        }
    }

    @Override
    public void onClearCompletedTasks() {
        taskRepository.deleteCompletedTasks();
        loadAndDisplayTasks();
    }

    @Override
    public void onUpdateTask(Task updatedTask) {
        taskRepository.update(updatedTask);
        loadAndDisplayTasks();
    }

    private void handleIntent() {
        activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task taskReceived = result.getData().getParcelableExtra(TaskDetail.TASK_EXTRA);
                        if (taskReceived != null) {
                            taskRepository.update(taskReceived);
                            loadAndDisplayTasks();
                        }
                    }
                }
        );
    }

    private void testDatabaseOperations() {
        Task sampleTask1 = new Task("Sample Task 1", "Description for task 1", "2024-06-19", false);
        Task sampleTask2 = new Task("Sample Task 2", "Description for task 2", "2024-06-20", true);

        taskRepository.insert(sampleTask1);
        taskRepository.insert(sampleTask2);

        loadAndDisplayTasks();
    }
}
