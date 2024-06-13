package com.example.myapplicationtask;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplicationtask.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.Calendar;

public class TaskDetailFragment extends Fragment {

    private ActivityMainBinding binding;
    private Task task;
    private OnTaskUpdatedListener callback;

    public interface OnTaskUpdatedListener {
        void onTaskUpdated(Task task);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskUpdatedListener) {
            callback = (OnTaskUpdatedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTaskUpdatedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
    }

    public void displayTask(Task task) {
        this.task = task;
        if (task != null) {
            binding.editName.setText(task.getShortName());
            binding.editDescription.setText(task.getDescription());
            binding.checkBox2.setChecked(task.isDone());
            String date = task.getDate();
            binding.editData.setText(date != null ? date : "Date");
        } else {
            clearFields();
        }
    }

    public void clearFields() {
        binding.editName.setText("");
        binding.editDescription.setText("");
        binding.checkBox2.setChecked(false);
        binding.editData.setText("Date");
    }

    private void setupListeners() {
        binding.editData.setOnClickListener(v -> openDialog());
        binding.fab1.setOnClickListener(v -> {
            Task taskReturned = addOrUpdateTask();
            if (callback != null) {
                callback.onTaskUpdated(taskReturned);
            }
            clearFields();  // Clear fields after adding/updating the task
        });
    }

    public Task addOrUpdateTask() {
        String name = binding.editName.getText().toString();
        String description = binding.editDescription.getText().toString();
        boolean isDone = binding.checkBox2.isChecked();
        String date = binding.editData.getText().toString();

        if (task != null) {
            task.setShortName(name);
            task.setDescription(description);
            task.setDone(isDone);
            task.setDate(date);
            return task;
        } else {
            return new Task(name, description, date, isDone);
        }
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

        DatePickerDialog dialog = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            binding.editData.setText(monthNames[selectedMonth] + " " + selectedDay + ", " + selectedYear);
        }, year, month, day);

        dialog.show();
    }
}
