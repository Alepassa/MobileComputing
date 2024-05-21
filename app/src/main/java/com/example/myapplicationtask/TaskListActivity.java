package com.example.myapplicationtask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity{
    private static final String BUNDLE_TASKS_KEY = "task";
    private static final int SPACE_ITEM = 20; // for space in item list
    private ActivityListTaskBinding binding; //used for the connect with xml object
    private List<Task> tasks;
    private TaskListAdapter adapter;

    @Override //method used when the activity starts
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Simple Task");

        if (savedInstanceState == null) {
            //if it's null it's created by first time so we can load the task in repository
            tasks = TaskRepositoryInMemoryImpl.getInstance().loadTasks();
        } else {
            // we obtain the task by the status saved because it's not created now
            tasks = savedInstanceState.getParcelableArrayList(BUNDLE_TASKS_KEY);
        }

        setupRecyclerView();
        handleIntent();
    }

    private void setupRecyclerView() {
        adapter = new TaskListAdapter(tasks, this::onTaskSelected);
        RecyclerView listView = binding.listview;
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(), DividerItemDecoration.VERTICAL);
        listView.addItemDecoration(dividerItemDecoration);
        listView.addItemDecoration(new SpaceItem(SPACE_ITEM));
    }

    private void setupListeners() {
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(TaskListActivity.this, MainActivity.class);
                startActivity(intent2);
            }
        });
    }


        @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_TASKS_KEY, new ArrayList<>(tasks));
        super.onSaveInstanceState(outState);
    }

    private void handleIntent(){
        Task task = getIntent().getParcelableExtra(MainActivity.TASK_EXTRA);
        if (task != null){
            tasks.add(task);
        }
    }

    // metodo per gestire l'evento del click sulla task
    public void onTaskSelected(Task task){
        // parametro inizio attività e fine destinazione
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.TASK_EXTRA, task);
        startActivity(intent);
    }

}
