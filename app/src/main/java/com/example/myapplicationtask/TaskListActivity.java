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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String BUNDLE_TASKS_KEY = "task";
    private static final int SPACE_ITEM = 20;
    private ActivityListTaskBinding binding;
    private Map<String, List<Task>> taskLists;
    private String currentTaskListName;
    private FilteredTasksAdapter adapter;
    private ActivityResultLauncher<Intent> activityLauncher;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
            currentTaskListName = "Default";
            taskLists.put(currentTaskListName, TaskRepositoryInMemoryImpl.getInstance().loadTasks());
        } else {
            taskLists = (Map<String, List<Task>>) savedInstanceState.getSerializable(BUNDLE_TASKS_KEY);
            currentTaskListName = savedInstanceState.getString("currentTaskListName");
        }

        setupRecyclerView();
        handleIntent();
        setupListeners();
    }

    private void setupRecyclerView() {
        adapter = new FilteredTasksAdapter(taskLists.get(currentTaskListName), this::onTaskSelected);
        RecyclerView listView = binding.listview;
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listView.addItemDecoration(new SpaceItem(SPACE_ITEM));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.firstOption) {   // show all task
            adapter.setFilter(true);
            return true;
        } else if (item.getItemId() == R.id.secondOption) {  // show unfinished task
            adapter.setFilter(false);
            return true;
        } else if (item.getItemId() == R.id.thirdOption) {      // delete finished task
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        currentTaskListName = item.getTitle().toString();
        adapter.updateTasks(taskLists.get(currentTaskListName));
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(currentTaskListName);
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
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(TaskListActivity.this, MainActivity.class);
                intent2.putExtra("taskListName", currentTaskListName);
                activityLauncher.launch(intent2);
            }
        });

        // Create Task List button click listener
        View headerView = navigationView.getHeaderView(0);
        headerView.findViewById(R.id.nav_header_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreateTaskListDialog();
            }
        });
    }

    private void showCreateTaskListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Task List");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskListName = input.getText().toString();
                if (!taskListName.isEmpty()) {
                    addTaskListToMenu(taskListName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addTaskListToMenu(String taskListName) {
        taskLists.put(taskListName, new ArrayList<>());
        MenuItem item = menu.add(taskListName);
        item.setIcon(R.drawable.ic_menu_gallery); // Set an icon if needed
        item.setCheckable(true);
    }

    public void onTaskSelected(Task task) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.TASK_EXTRA, task);
        intent.putExtra("taskListName", currentTaskListName);
        activityLauncher.launch(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(BUNDLE_TASKS_KEY, new HashMap<>(taskLists));
        outState.putString("currentTaskListName", currentTaskListName);
        super.onSaveInstanceState(outState);
    }

    private void handleIntent() {
        activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task taskReceived = result.getData().getParcelableExtra(MainActivity.TASK_EXTRA);
                        String taskListName = result.getData().getStringExtra("taskListName");
                        if (taskReceived != null && taskListName != null) {
                            boolean isUpdated = false;
                            List<Task> tasks = taskLists.get(taskListName);
                            for (int i = 0; i < tasks.size(); i++) {
                                if (tasks.get(i).getId() == taskReceived.getId()) {
                                    tasks.set(i, taskReceived);
                                    isUpdated = true;
                                    break;
                                }
                            }
                            if (!isUpdated) {
                                tasks.add(taskReceived);
                            }
                            adapter.updateTasks(tasks);
                        }
                    }
                }
        );
    }
}
