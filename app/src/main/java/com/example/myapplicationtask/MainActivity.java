package com.example.myapplicationtask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import com.example.myapplicationtask.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //create activity
        super.onCreate(savedInstanceState); // -> we pass istance if it's present

        //convert file xml in object type view, inflate convert in object activitymainbinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy.
        super.onRestoreInstanceState(savedInstanceState);
        String textInput1Data = savedInstanceState.getString("textInput1Data");
        binding.textInputEditText1.setText(textInput1Data);
        String textInput2Data = savedInstanceState.getString("textInput2Data");
        binding.textInputEditText2.setText(textInput2Data);
        String editTextData = savedInstanceState.getString("editTextData");
        binding.editTextDate2.setText(editTextData);
    }

    // Invoked when the activity might be temporarily destroyed; save the instance state here.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        //save the state
        outState.putString("textInput1Data", binding.textInputEditText1.getText().toString());
        outState.putString("textInput2Data", binding.textInputEditText2.getText().toString());
        outState.putString("editTextData", binding.editTextDate2.getText().toString());

        super.onSaveInstanceState(outState);
    }
}