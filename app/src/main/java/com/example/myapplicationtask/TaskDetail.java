package com.example.myapplicationtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplicationtask.databinding.ActivityMainBinding;

public class TaskDetail extends AppCompatActivity {
    public static final String TASK_EXTRA = "TASK_EXTRA";
    private ActivityMainBinding binding;
    private TaskDetailFragment taskDetailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FragmentManager fm = getSupportFragmentManager();
        taskDetailFragment = (TaskDetailFragment) fm.findFragmentById(R.id.taskDetailContainer);

        if (taskDetailFragment == null) {
            FragmentTransaction t = fm.beginTransaction();
            taskDetailFragment = TaskDetailFragment.newInstance();
            t.add(R.id.taskDetailContainer, taskDetailFragment);
            t.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        Task task = intent.getParcelableExtra(TASK_EXTRA);

        if (task != null && taskDetailFragment != null) {
            taskDetailFragment.displayTask(task);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("Task Detail: " + task.getShortName());
            }
        }
    }


    public void returnUpdatedTask(Task updatedTask) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(TaskDetail.TASK_EXTRA, updatedTask);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}

