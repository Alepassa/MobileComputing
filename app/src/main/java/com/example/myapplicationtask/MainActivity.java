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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    //costante per passare dati fra le attività
    public static final String TASK_EXTRA = "TASK_EXTRA";

    private ActivityMainBinding binding; //binding per le viste
    private Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        restoreSavedInstanceState(savedInstanceState); //restore set
        ActionBar bar = getSupportActionBar();
        bar.setTitle("Edit Task");  //set name of the istance
        handleIntent();
        setupListeners();
    }

    //quando passiamo dall'activity taskListAdapter dobbiamo aprire le informazioni
    //inerenti a quella task, quindi visualizzare nome della task e se sono disponibili
    //anche la descrizione, data e se isDone è riempito o meno
    private void handleIntent(){
        Task task = getIntent().getParcelableExtra(MainActivity.TASK_EXTRA);
        if(task != null) {
            binding.editName.setText(task.getShortName());
            binding.editDescription.setText(task.getDescription());
            binding.checkBox2.setChecked(task.isDone());
            String date = task.getDate();
            binding.editData.setText(date != null ? date : "Date");
        }
    }

    private void setupListeners() {
        //se clicco sul datapicker
        binding.editData.setOnClickListener(v -> openDialog());
        //button save
        binding.button1.setOnClickListener(v -> {
            Intent intent = new Intent();
            Task taskReturned = addOrUpdateTask(v);
            intent.putExtra(TASK_EXTRA, taskReturned);
            setResult(RESULT_OK, intent);
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
    private void restoreSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            binding.editDescription.setText(savedInstanceState.getString("editDescription"));
            binding.editName.setText(savedInstanceState.getString("editName"));
            binding.editData.setText(savedInstanceState.getString("editData"));
        }
    }

    public Task addOrUpdateTask(View v){
        String name = binding.editName.getText().toString();
        String description = binding.editDescription.getText().toString();
        boolean isDone = binding.checkBox2.isChecked();
        String date = binding.editData.getText().toString();

        Task receivedTask = getIntent().getParcelableExtra(MainActivity.TASK_EXTRA);
        //update if already exist the task
        if(receivedTask != null ) {
            receivedTask.setShortName(name);
            receivedTask.setDescription(description);
            receivedTask.setDone(isDone);
            receivedTask.setDate(date);
            return receivedTask;
        }
        //if we pressed add button we just create a new task
        // we check if the task has atleast a short name
        if(!name.isEmpty()) {
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
            day = Integer.parseInt(divideDate[1].replace(",",""));
            year = Integer.parseInt(divideDate[2]);
        }

        DatePickerDialog dialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            binding.editData.setText(monthNames[selectedMonth] + " "+  selectedDay + ", "+selectedYear);
        }, year, month, day);

        dialog.show();
    }

}