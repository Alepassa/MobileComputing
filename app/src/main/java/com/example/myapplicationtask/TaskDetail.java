package com.example.myapplicationtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplicationtask.databinding.ActivityMainBinding;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.myapplicationtask.databinding.ActivityMainBinding;

public class TaskDetail extends AppCompatActivity {
    public static final String TASK_EXTRA = "TASK_EXTRA";
    private ActivityMainBinding binding;
    private TaskDetailFragment taskDetailFragment;
    private int taskListId;
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();

        if (savedInstanceState != null) {
            taskListId = savedInstanceState.getInt("taskListId", -1);
            task = savedInstanceState.getParcelable("task");
        } else {
            Intent intent = getIntent();
            taskListId = intent.getIntExtra("taskListId", -1);
            task = intent.getParcelableExtra(TASK_EXTRA);
        }

        setupFragment();
    }

    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        taskDetailFragment = (TaskDetailFragment) fm.findFragmentById(R.id.taskDetailContainer);

        if (taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance();
            Bundle args = new Bundle();
            args.putInt("taskListId", taskListId);
            args.putParcelable(TASK_EXTRA, task);
            taskDetailFragment.setArguments(args);
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.taskDetailContainer, taskDetailFragment);
            transaction.commit();
        }
    }

    public void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("Task Detail");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("taskListId", taskListId);
        outState.putParcelable("task", task);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void returnUpdatedTask(Task updatedTask) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(TASK_EXTRA, updatedTask);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}