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

public class TaskListActivity extends AppCompatActivity{
    private static final String BUNDLE_TASKS_KEY = "task";
    private static final int SPACE_ITEM = 20;
    private ActivityListTaskBinding binding; //used for the connect with xml object
    private List<Task> tasks;
    private TaskListAdapter adapter;
    private FilteredTasksAdapter filterTasks;

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

        filterTasks = new FilteredTasksAdapter(tasks);
        adapter = new TaskListAdapter(filterTasks.getFilteredTasks(), this::onTaskSelected);
        RecyclerView listView = binding.listview;
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(), DividerItemDecoration.VERTICAL);
        listView.addItemDecoration(dividerItemDecoration);
        listView.addItemDecoration(new SpaceItem(SPACE_ITEM));
        handleIntent();
        setupListeners();
    }


    //https://developer.android.com/develop/ui/views/components/menus?hl=it
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.firstOption){
            filterTasks.setShowUnfinished(false);
            adapter.updateTasks(filterTasks.getFilteredTasks());
            return true;
        }
        else if(item.getItemId() == R.id.secondOption){
            filterTasks.setShowUnfinished(true);
            adapter.updateTasks(filterTasks.getFilteredTasks());
            return true;
        }
        else if(item.getItemId() == R.id.thirdOption){
            filterTasks.deleteFinishedTasks();
            adapter.updateTasks(filterTasks.getFilteredTasks());
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

        @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_TASKS_KEY, new ArrayList<>(tasks));
        super.onSaveInstanceState(outState);
    }

    private void handleIntent(){
        Task task = getIntent().getParcelableExtra(MainActivity.TASK_EXTRA);

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
                                    isUpdated = true; //if we update an existing task
                                    break;
                                }
                            }
                            //it means that it's a new task with a new id!
                            if (!isUpdated) {
                                tasks.add(taskReceived);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }

    // metodo per gestire l'evento del click sulla task
    public void onTaskSelected(Task task){
        // parametro inizio attività e fine destinazione
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.TASK_EXTRA, task);
        activityLauncher.launch(intent);
    }



}
