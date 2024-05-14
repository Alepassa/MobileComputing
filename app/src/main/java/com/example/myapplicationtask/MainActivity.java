package com.example.myapplicationtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
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


        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(v);
            }
        });

        binding.editData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        ActionBar bar = getSupportActionBar();
        bar.setTitle("Edit Task");
        Task task = getIntent().getParcelableExtra(MainActivity.TASK_EXTRA);
        fillTask(task);

    }

    public void addTask(View v){
        EditText mShortName =binding.editDescription;
        EditText mDescription = binding.editName;
        CheckBox mDone  = binding.checkBox2;
        TextView mDate = binding.editData;
        String fieldName = Objects.requireNonNull(mShortName.getText()).toString();
        String fieldDescription = Objects.requireNonNull(mDescription.getText()).toString();
        boolean fieldDone = mDone.isChecked();
        task = new Task(fieldName);
        task.setDescription(fieldDescription);
        task.setDone(fieldDone);
        String dateToSet = mDate.getText().toString();
        task.setDate(dateToSet);
        newTask();

        Snackbar.make(v, "New Task Added " , Snackbar.LENGTH_LONG).show();
    }

    public void newTask() { // Rimuovi i parametri
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