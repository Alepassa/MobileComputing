package com.example.myapplicationtask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplicationtask.databinding.ActivityMainBinding;

public class TaskDetail extends AppCompatActivity {
    public static final String TASK_EXTRA = "TASK_EXTRA";
    private ActivityMainBinding binding;
    private TaskDetailFragment taskDetailFragment;
    private int taskListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        taskListId = intent.getIntExtra("taskListId", -1);

        setupFragment();
    }

    private void setupFragment() {
        FragmentManager fm = getSupportFragmentManager();
        taskDetailFragment = (TaskDetailFragment) fm.findFragmentById(R.id.taskDetailContainer);

        if (taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance();
            Bundle args = new Bundle();
            args.putInt("taskListId", taskListId);
            taskDetailFragment.setArguments(args);
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(R.id.taskDetailContainer, taskDetailFragment);
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        Task task = intent.getParcelableExtra(TASK_EXTRA);

        if (task != null && taskDetailFragment != null) {
            taskDetailFragment.displayTask(task);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Task Detail: " + task.getShortName());
            }
        }
    }

    public void returnUpdatedTask(Task updatedTask) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(TASK_EXTRA, updatedTask);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
