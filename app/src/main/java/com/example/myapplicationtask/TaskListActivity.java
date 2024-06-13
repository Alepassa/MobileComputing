package com.example.myapplicationtask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class TaskListActivity extends AppCompatActivity implements
        TaskListFragment.OnTaskSelectedListener,
        TaskDetailFragment.OnTaskUpdatedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String BUNDLE_TASKS_KEY = "taskLists";
    private static final String BUNDLE_CURRENT_TASK_LIST_NAME = "currentTaskListName";
    private static final String BUNDLE_TOOLBAR_TITLE = "toolbarTitle";
    static final int SPACE_ITEM = 20;
    private ActivityListTaskBinding binding;
    private Map<String, List<Task>> taskLists;
    private String currentTaskListName;
    private FilteredTasksAdapter adapter;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Menu menu;
    private boolean isDialogShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Controlla se siamo in modalità tablet landscape
        boolean isTabletLandscape = getResources().getBoolean(R.bool.isTabletLandscape);
        if (isTabletLandscape) {
            if (toolbar != null) {
                toolbar.setVisibility(View.GONE);
            }
        }

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            taskLists = new HashMap<>();
            currentTaskListName = "Groceries";
            taskLists.put(currentTaskListName, TaskRepositoryInMemoryImpl.getInstance().loadTasks("Groceries"));
            taskLists.put("University", TaskRepositoryInMemoryImpl.getInstance().loadTasks("University"));

            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                bar.setTitle(currentTaskListName);
            }
        } else {
            taskLists = (Map<String, List<Task>>) savedInstanceState.getSerializable(BUNDLE_TASKS_KEY);
            currentTaskListName = savedInstanceState.getString(BUNDLE_CURRENT_TASK_LIST_NAME);
            String toolbarTitle = savedInstanceState.getString(BUNDLE_TOOLBAR_TITLE);
            ActionBar bar = getSupportActionBar();
            if (bar != null && toolbarTitle != null) {
                bar.setTitle(toolbarTitle);
            }
        }

        TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_task_list);
        if (taskListFragment != null) {
            taskListFragment.updateTasks(taskLists, currentTaskListName);
        }

        setupRecyclerView();
        setupListeners();
        populateNavigationMenu();
    }

    private void setupRecyclerView() {
        adapter = new FilteredTasksAdapter(new ArrayList<>(), task -> {
            if (task != null) {
                onTaskSelected(task);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new SpaceItem(SPACE_ITEM));

        List<Task> initialTasks = taskLists.get(currentTaskListName);
        if (initialTasks == null) {
            initialTasks = new ArrayList<>();
            taskLists.put(currentTaskListName, initialTasks);
        }
        adapter.updateTasks(initialTasks);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.create_task_list) {
            showCreateTaskListDialog();
        } else {
            currentTaskListName = item.getTitle().toString();
            List<Task> tasks = taskLists.get(currentTaskListName);
            if (tasks == null) {
                tasks = new ArrayList<>();
                taskLists.put(currentTaskListName, tasks);
            }
            TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_task_list);
            if (taskListFragment != null) {
                taskListFragment.updateTasks(taskLists, currentTaskListName);
            }
            ActionBar bar = getSupportActionBar();
            if (bar != null) {
                bar.setTitle(currentTaskListName);
            }
            adapter.updateTasks(tasks);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setupListeners() {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                TaskDetailFragment taskDetailFragment = (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_task_detail);
                if (taskDetailFragment != null) {
                    taskDetailFragment.displayTask(null);
                } else {
                    Intent intent = new Intent(TaskListActivity.this, MainActivity.class);
                    intent.putExtra("taskListName", currentTaskListName);
                    startActivity(intent);
                }
            });
        }
    }

    private void showCreateTaskListDialog() {
        if (isDialogShowing) return;

        isDialogShowing = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Task List");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String taskListName = input.getText().toString();
            if (!taskListName.isEmpty()) {
                addTaskListToMenu(taskListName);
                currentTaskListName = taskListName;
                List<Task> tasks = taskLists.get(taskListName);
                if (tasks == null) {
                    tasks = new ArrayList<>();
                    taskLists.put(taskListName, tasks);
                }
                TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_task_list);
                if (taskListFragment != null) {
                    taskListFragment.updateTasks(taskLists, currentTaskListName);
                }
                ActionBar bar = getSupportActionBar();
                if (bar != null) {
                    bar.setTitle(taskListName);
                }
                adapter.updateTasks(tasks);
            }
            isDialogShowing = false;
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            isDialogShowing = false;
        });

        builder.setOnDismissListener(dialog -> isDialogShowing = false);

        builder.show();
    }

    private void addTaskListToMenu(String taskListName) {
        taskLists.put(taskListName, new ArrayList<>());
        MenuItem item = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, taskListName);
        item.setCheckable(false);
    }

    @Override
    public void onTaskSelected(Task task) {
        TaskDetailFragment taskDetailFragment = (TaskDetailFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_task_detail);
        if (taskDetailFragment != null) {
            taskDetailFragment.displayTask(task);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.TASK_EXTRA, task);
            intent.putExtra("taskListName", currentTaskListName);
            startActivity(intent);
        }
    }

    @Override
    public void onTaskUpdated(Task task) {
        if (task != null) {
            List<Task> tasks = taskLists.get(currentTaskListName);
            if (tasks != null) {
                boolean isUpdated = false;
                for (int i = 0; i < tasks.size(); i++) {
                    if (tasks.get(i).getId() == task.getId()) {
                        tasks.set(i, task);
                        isUpdated = true;
                        break;
                    }
                }
                if (!isUpdated) {
                    tasks.add(task);
                }
                TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_task_list);
                if (taskListFragment != null) {
                    taskListFragment.updateTasks(taskLists, currentTaskListName);
                }
                adapter.updateTasks(tasks);
            }
        }
    }

    public void onTaskListChanged(String taskListName) {
        currentTaskListName = taskListName;
        List<Task> tasks = taskLists.get(currentTaskListName);
        if (tasks == null) {
            tasks = new ArrayList<>();
            taskLists.put(currentTaskListName, tasks);
        }
        TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_task_list);
        if (taskListFragment != null) {
            taskListFragment.updateTasks(taskLists, currentTaskListName);
        }
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(taskListName);
        }
        adapter.updateTasks(tasks);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(BUNDLE_TASKS_KEY, new HashMap<>(taskLists));
        outState.putString(BUNDLE_CURRENT_TASK_LIST_NAME, currentTaskListName);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            outState.putString(BUNDLE_TOOLBAR_TITLE, bar.getTitle().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        taskLists = (Map<String, List<Task>>) savedInstanceState.getSerializable(BUNDLE_TASKS_KEY);
        currentTaskListName = savedInstanceState.getString(BUNDLE_CURRENT_TASK_LIST_NAME);
        String toolbarTitle = savedInstanceState.getString(BUNDLE_TOOLBAR_TITLE);
        ActionBar bar = getSupportActionBar();
        if (bar != null && toolbarTitle != null) {
            bar.setTitle(toolbarTitle);
        }
        TaskListFragment taskListFragment = (TaskListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_task_list);
        if (taskListFragment != null) {
            taskListFragment.updateTasks(taskLists, currentTaskListName);
        }
        List<Task> tasks = taskLists.get(currentTaskListName);
        if (tasks == null) {
            tasks = new ArrayList<>();
            taskLists.put(currentTaskListName, tasks);
        }
        adapter.updateTasks(tasks);
    }

    private void populateNavigationMenu() {
        menu.clear();

        MenuItem createTaskListItem = menu.add(Menu.NONE, R.id.create_task_list, Menu.NONE, "Create Task List");
        createTaskListItem.setIcon(R.drawable.add);

        for (String taskListName : taskLists.keySet()) {
            MenuItem item = menu.add(taskListName);
            item.setCheckable(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.firstOption) {
            adapter.setFilter(true);
            return true;
        } else if (item.getItemId() == R.id.secondOption) {
            adapter.setFilter(false);
            return true;
        } else if (item.getItemId() == R.id.thirdOption) {
            List<Task> tasksToRemove =
                    taskLists.get(currentTaskListName).stream()
                            .filter(Task::isDone)
                            .collect(Collectors.toList());
            taskLists.get(currentTaskListName).removeAll(tasksToRemove);
            adapter.updateTasks(taskLists.get(currentTaskListName));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
