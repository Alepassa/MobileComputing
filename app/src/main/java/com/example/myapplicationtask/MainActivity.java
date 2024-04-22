package com.example.myapplicationtask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;

import com.example.myapplicationtask.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

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
            binding.editTextDate2.setText(editTextData);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);

        //save the state
        saveInstanceState.putString("textInput1Data", binding.textInputEditText1.getText().toString());
        saveInstanceState.putString("textInput2Data", binding.textInputEditText2.getText().toString());
        saveInstanceState.putString("editTextData", binding.editTextDate2.getText().toString());
    }
}