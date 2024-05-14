package com.example.myapplicationtask;

import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity{
    private int space_item = 20;
    private ActivityListTaskBinding binding; //used for the connect with xml object
    private List<Task> tasks;
    private static final String BUNDLE_TASKS_KEY = "task";

    private TaskListAdapter adapter;

    @Override //method used when the activity starts
    protected void onCreate(Bundle savedInstanceState) {
        //invokes the superclass's method
        super.onCreate(savedInstanceState);


        binding = ActivityListTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if (savedInstanceState == null) {
            //if it's null it's created by first time so we can load the task in repository
            tasks = TaskRepositoryInMemoryImpl.getInstance().loadTasks();
        } else {
            // we obtain the task by the status saved because it's not created now
            tasks = savedInstanceState.getParcelableArrayList(BUNDLE_TASKS_KEY);
        }

        adapter = new TaskListAdapter(tasks);
        RecyclerView listView = binding.listview;
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(), DividerItemDecoration.VERTICAL);
        listView.addItemDecoration(dividerItemDecoration);
        listView.addItemDecoration(new SpaceItem(space_item));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(BUNDLE_TASKS_KEY, new ArrayList<>(tasks));
        super.onSaveInstanceState(outState);
    }
}
