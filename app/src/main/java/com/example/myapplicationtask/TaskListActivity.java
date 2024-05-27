package com.example.myapplicationtask;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskListActivity extends AppCompatActivity{
    private static final String BUNDLE_TASKS_KEY = "task";
    private static final int SPACE_ITEM = 20;
    private ActivityListTaskBinding binding;
    private List<Task> tasks;
    private FilteredTasksAdapter adapter;

    private ActivityResultLauncher<Intent> activityLauncher;

    @Override //method used when the activity starts
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Simple Task");

        if (savedInstanceState == null) {
            tasks = TaskRepositoryInMemoryImpl.getInstance().loadTasks();
        } else {
            tasks = savedInstanceState.getParcelableArrayList(BUNDLE_TASKS_KEY);
        }

        setupRecyclerView();
        handleIntent();
        setupListeners();
    }


    public void setupRecyclerView(){
        adapter = new FilteredTasksAdapter(tasks, this::onTaskSelected);
        RecyclerView listView = binding.listview;
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        listView.addItemDecoration(new SpaceItem(SPACE_ITEM));
    }

    //https://developer.android.com/develop/ui/views/components/menus?hl=it
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.firstOption){   //show all task
            adapter.setFilter(true);
            return true;
        }
        else if(item.getItemId() == R.id.secondOption){  //show unfinished task
            adapter.setFilter(false);
            return true;
        }
        else if(item.getItemId() == R.id.thirdOption){      //delete finished task
            List<Task> tasksToRemove =
                    tasks.stream()
                    .filter(Task::isDone)
                    .collect(Collectors.toList());
            tasks.removeAll(tasksToRemove);
            adapter.updateTasks(tasks);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListeners() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(TaskListActivity.this, MainActivity.class);
                activityLauncher.launch(intent2);
            }
        });
    }

    public void onTaskSelected(Task task) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.TASK_EXTRA, task);
        activityLauncher.launch(intent);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_TASKS_KEY, new ArrayList<>(tasks));
        super.onSaveInstanceState(outState);
    }

    private void handleIntent() {
        activityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Task taskReceived = result.getData().getParcelableExtra(MainActivity.TASK_EXTRA);
                        if (taskReceived != null) {
                            boolean isUpdated = false;
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
