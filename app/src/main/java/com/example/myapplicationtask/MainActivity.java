package com.example.myapplicationtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
            String textInput1Data = savedInstanceState.getString("textInput1Data");
            binding.textInputEditText1.setText(textInput1Data);
            String textInput2Data = savedInstanceState.getString("textInput2Data");
            binding.textInputEditText2.setText(textInput2Data);
            String editTextData = savedInstanceState.getString("editTextData");
        }
            binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(v);
            }
        });

        binding.toolbar.setTitle("My edit task");
        //
        // bar.setTitle("TASK");

    }

    public void addTask(View v){
        EditText mShortName =binding.textInputEditText1;
        EditText mDescription = binding.textInputEditText2;
        //EditText mCreationDate = binding.editTextDate2;
        CheckBox mDone  = binding.checkBox2;
        String fieldName = Objects.requireNonNull(mShortName.getText()).toString();
        String fieldDescription = Objects.requireNonNull(mDescription.getText()).toString();
        boolean fieldDone = mDone.isChecked();
        //bonus task -> check if every field is not empty
        task = new Task(fieldName);
        task.setDescription(fieldDescription);
        task.setDone(fieldDone);
        newTask(mShortName,mDescription,mDone);



        Snackbar.make(v, "New Task Added " , Snackbar.LENGTH_LONG).show();
    }

    public void newTask(EditText mShortName,EditText mDescription,CheckBox mDone ){
        mShortName.setText("");
        mDescription.setText("");
        //mCreationDate.setText("");
        mDone.setChecked(false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        //save the state
        saveInstanceState.putString("textInput1Data", binding.textInputEditText1.getText().toString());
        saveInstanceState.putString("textInput2Data", binding.textInputEditText2.getText().toString());
        //saveInstanceState.putString("editTextData", binding.editTextDate2.getText().toString());
    }


}