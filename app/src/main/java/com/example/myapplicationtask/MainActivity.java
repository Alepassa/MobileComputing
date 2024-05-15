package com.example.myapplicationtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Debug;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;
import com.example.myapplicationtask.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    public static final String TASK_EXTRA = "TASK_EXTRA";

    private ActivityMainBinding binding;
    private Task task;
    private TaskListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState != null) {
            String description = savedInstanceState.getString("editDescription");
            binding.editDescription.setText(description);
            String name = savedInstanceState.getString("editName");
            binding.editName.setText(name);
            String date = savedInstanceState.getString("editData");
            binding.editData.setText(date);
        }



        ActionBar bar = getSupportActionBar();
        bar.setTitle("Edit Task");

        Task task = getIntent().getParcelableExtra(MainActivity.TASK_EXTRA);
        if(task != null)  fillTask(task); //perchè se task non passa niente è perchè stiamo premendo il tasto


        binding.editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TaskListActivity.class);
                Task task = addTask(v);
                intent.putExtra(TASK_EXTRA, task); //per passare i dettagli della nuova task
                startActivity(intent);
                // OnTaskAdded(v);
            }
        });
    }


   // private void OnTaskAdded(View v) {
     //   Editable mShortName = binding.editName.getText();
       // Snackbar.make(v, "New Task: " +  mShortName, Snackbar.LENGTH_SHORT).show();
    //}

    public Task addTask(View v){
        EditText mShortName =binding.editName;
        EditText mDescription = binding.editDescription;
        CheckBox mDone  = binding.checkBox2;
        TextView mDate = binding.editData;
        String fieldName = (mShortName.getText()).toString();
        boolean fieldDone = mDone.isChecked();
        task = new Task(fieldName);
        task.setDescription(mDescription.getText().toString());
        task.setDone(fieldDone);
        task.setDate(mDate.getText().toString());
        return task;
    }

    public void newTask() { // Rimuovi i parametri -  non serve per ora
        binding.editDescription.setText("");
        binding.editName.setText("");
        binding.checkBox2.setChecked(false);
        binding.editData.setText("Date");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        //save the state
        saveInstanceState.putString("editDescription", binding.editDescription.getText().toString());
        saveInstanceState.putString("editName", binding.editName.getText().toString());
        saveInstanceState.putString("editData", binding.editData.getText().toString());
        super.onSaveInstanceState(saveInstanceState);
    }

    private void openDialog(){
        final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        DatePickerDialog dialog= new DatePickerDialog(this,  new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                binding.editData.setText(monthNames[month] + ", " + String.valueOf(day) + " " + String.valueOf(year));
            }
        }, 2024,4,25);
        dialog.show();
    }

    private void fillTask(Task task) {
        binding.editDescription.setText(task.getShortName());
        binding.editName.setText(task.getDescription());
        binding.checkBox2.setChecked(task.isDone());
        //bisogna controllare perchè se si carica
        //dalla memoria non sempre si mette una data
        String date = task.getDate();
        if (date != null) {
            binding.editData.setText(date);
        } else {
            binding.editData.setText("Date");
        }
    }


}