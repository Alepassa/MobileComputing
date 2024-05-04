package com.example.myapplicationtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
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



        //binding.toolbar.setTitle("My edit task");
        //
        // bar.setTitle("TASK");

    }

    public void addTask(View v){
        EditText mShortName =binding.editDescription;
        EditText mDescription = binding.editName;
        //EditText mCreationDate = binding.editTextDate2;
        CheckBox mDone  = binding.checkBox2;
        TextView mDate = binding.editData;
        String fieldName = Objects.requireNonNull(mShortName.getText()).toString();
        String fieldDescription = Objects.requireNonNull(mDescription.getText()).toString();
        boolean fieldDone = mDone.isChecked();
        //bonus task -> check if every field is not empty
        task = new Task(fieldName);
        task.setDescription(fieldDescription);
        task.setDone(fieldDone);
        newTask(mShortName,mDescription,mDone,mDate);


        Snackbar.make(v, "New Task Added " , Snackbar.LENGTH_LONG).show();
    }

    public void newTask(EditText mShortName,EditText mDescription,CheckBox mDone, TextView mDate){
        mShortName.setText("");
        mDescription.setText("");
        //mCreationDate.setText("");
        mDone.setChecked(false);
        mDate.setText("Date");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        //save the state
        saveInstanceState.putString("editDescription", binding.editDescription.getText().toString());
        saveInstanceState.putString("editName", binding.editName.getText().toString());
        saveInstanceState.putString("editData", binding.editData.getText().toString());
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

}