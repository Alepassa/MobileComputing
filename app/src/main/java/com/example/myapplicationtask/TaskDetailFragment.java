package com.example.myapplicationtask;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Arrays;
import java.util.Calendar;
import com.example.myapplicationtask.databinding.FragmentTaskDetailBinding;


import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import com.example.myapplicationtask.databinding.FragmentTaskDetailBinding;

public class TaskDetailFragment extends Fragment {

    private FragmentTaskDetailBinding binding;
    private Task task;
    private OnTaskUpdatedListener callbacks;

    public static TaskDetailFragment newInstance() {
        return new TaskDetailFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
    }

    public interface OnTaskUpdatedListener {
        void onUpdateTask(Task updatedTask);
    }

    public void setOnTaskUpdatedListener(OnTaskUpdatedListener listener) {
        this.callbacks = listener;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    public void displayTask(Task task) {
        if (task == null) {
            this.task = new Task();
            clearTaskDetails();
        } else {
            this.task = task;
            populateTaskDetails(task);
        }
    }

    private void setupListeners() {
        binding.editData.setOnClickListener(v -> openDatePickerDialog());
        binding.fab1.setOnClickListener(v -> updateTask());
    }

    private void clearTaskDetails() {
        binding.editName.setText("");
        binding.editDescription.setText("");
        binding.checkBox2.setChecked(false);
        binding.editData.setText("Date");
    }

    private void populateTaskDetails(Task task) {
        binding.editName.setText(task.getShortName());
        binding.editDescription.setText(task.getDescription());
        binding.checkBox2.setChecked(task.isDone());
        binding.editData.setText(task.getDate() != null && !task.getDate().isEmpty() ? task.getDate() : "Date");
    }

    private void updateTask() {
        String taskName = binding.editName.getText().toString().trim();

        if (taskName.isEmpty()) {
            Log.d("TaskDetailFragment", "Task name cannot be empty");
            // Mostra un messaggio all'utente, ad esempio:
            Toast.makeText(requireContext(), "Task name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (task == null) {
            Log.d("TaskDetailFragment", "Creating a new task");
            task = new Task();
        } else {
            Log.d("TaskDetailFragment", "Updating task: " + task.toString());
        }
        task.setShortName(binding.editName.getText().toString());
        task.setDescription(binding.editDescription.getText().toString());
        task.setDone(binding.checkBox2.isChecked());
        task.setDate(binding.editData.getText().toString());

        if (callbacks != null) {
            callbacks.onUpdateTask(task);
        }

        if (getActivity() instanceof TaskDetail) {
            ((TaskDetail) getActivity()).returnUpdatedTask(task);
        }
    }

    private void openDatePickerDialog() {
        final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        Calendar calendar = Calendar.getInstance();
        int year, month, day;
        String currentDate = binding.editData.getText().toString();

        if (currentDate.equals("Date")) {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] dividedDate = currentDate.split(" ");
            month = Arrays.asList(monthNames).indexOf(dividedDate[0]);
            day = Integer.parseInt(dividedDate[1].replace(",", ""));
            year = Integer.parseInt(dividedDate[2]);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            binding.editData.setText(monthNames[selectedMonth] + " " + selectedDay + ", " + selectedYear);
        }, year, month, day);

        datePickerDialog.show();
    }
}