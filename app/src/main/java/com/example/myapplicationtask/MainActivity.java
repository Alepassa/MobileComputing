package com.example.myapplicationtask;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.myapplicationtask.databinding.ActivityMainBinding;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static final String TASK_EXTRA = "TASK_EXTRA";
    private ActivityMainBinding binding;
    private Task task;
    private String taskListName;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        handleIntent();

        if (savedInstanceState != null) {
            String description = savedInstanceState.getString("editDescription");
            binding.editDescription.setText(description);
            String name = savedInstanceState.getString("editName");
            binding.editName.setText(name);
            String date = savedInstanceState.getString("editData");
            binding.editData.setText(date);
        } else {
            resetFields();
        }


        setupToolbar();
        setupListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true); // Show the back button
            actionBar.setHomeAsUpIndicator(R.drawable.arrow_back); // Set custom back button icon
            actionBar.setTitle("Task List Detail"); // Set the title to "Task List Detail"
        }
    }

    private boolean isTablet() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void handleIntent() {
        Intent intent = getIntent();
        taskListName = intent.getStringExtra("taskListName");
        task = intent.getParcelableExtra(TASK_EXTRA);
        if (task != null) {
            binding.editName.setText(task.getShortName());
            binding.editDescription.setText(task.getDescription());
            binding.checkBox2.setChecked(task.isDone());
            String date = task.getDate();
            binding.editData.setText(date != null ? date : "Date");
        } else {
            resetFields();
        }
    }

    private void setupListeners() {
        binding.editData.setOnClickListener(v -> openDialog());
        binding.fab1.setOnClickListener(v -> {
            Intent intent = new Intent();
            Task taskReturned = addOrUpdateTask(v);
            intent.putExtra(TASK_EXTRA, taskReturned);
            intent.putExtra("taskListName", taskListName);
            setResult(RESULT_OK, intent);
            resetFields();  // Reset fields after adding the task
            finish();
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        saveInstanceState.putString("editDescription", binding.editDescription.getText().toString());
        saveInstanceState.putString("editName", binding.editName.getText().toString());
        saveInstanceState.putString("editData", binding.editData.getText().toString());
        super.onSaveInstanceState(saveInstanceState);
    }

    public Task addOrUpdateTask(View v) {
        String name = binding.editName.getText().toString();
        String description = binding.editDescription.getText().toString();
        boolean isDone = binding.checkBox2.isChecked();
        String date = binding.editData.getText().toString();

        Task receivedTask = getIntent().getParcelableExtra(TASK_EXTRA);
        if (receivedTask != null) {
            receivedTask.setShortName(name);
            receivedTask.setDescription(description);
            receivedTask.setDone(isDone);
            receivedTask.setDate(date);
            return receivedTask;
        }
        if (!name.isEmpty()) {
            return new Task(name, description, date, isDone);
        }

        return null;
    }

    private void openDialog() {
        final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        Calendar calendar = Calendar.getInstance();
        int year, month, day;
        String currentData = binding.editData.getText().toString();

        if (currentData.equals("Date")) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] divideDate = currentData.split(" ");
            month = Arrays.asList(monthNames).indexOf(divideDate[0]);
            day = Integer.parseInt(divideDate[1].replace(",", ""));
            year = Integer.parseInt(divideDate[2]);
        }

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                binding.editData.setText(monthNames[selectedMonth] + " " + selectedDay + ", " + selectedYear);
            }
        }, year, month, day);

        dialog.show();
    }

    private void resetFields() {
        binding.editName.setText("");
        binding.editDescription.setText("");
        binding.checkBox2.setChecked(false);
        binding.editData.setText("Date");
    }
}
