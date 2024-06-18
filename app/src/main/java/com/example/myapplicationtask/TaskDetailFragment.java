package com.example.myapplicationtask;

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.myapplicationtask.databinding.FragmentTaskDetailBinding;

import java.util.Arrays;
import java.util.Calendar;

public class TaskDetailFragment extends Fragment {

    private FragmentTaskDetailBinding binding;
    private Task task;
    private TaskViewModel taskViewModel;
    private OnTaskUpdatedListener callbacks;
    private int taskListId;

    public interface OnTaskUpdatedListener {
        void onUpdateTask(Task updatedTask);
    }

    public static TaskDetailFragment newInstance() {
        return new TaskDetailFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskViewModel = new TaskViewModel(requireActivity().getApplication());
        setupListeners();

        if (getArguments() != null) {
            taskListId = getArguments().getInt("taskListId");
            Log.d("TaskDetailFragment", "Received taskListId: " + taskListId);
        } else {
            Log.e("TaskDetailFragment", "No taskListId received");
        }
    }

    private void setupListeners() {
        binding.editData.setOnClickListener(v -> openDatePickerDialog());
        binding.fab1.setOnClickListener(v -> saveTask());
    }

    public void displayTask(Task task) {
        this.task = task;
        if (task == null) {
            clearTaskDetails();
        } else {
            populateTaskDetails(task);
        }
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

    private void saveTask() {
        String taskName = binding.editName.getText().toString().trim();

        if (taskName.isEmpty()) {
            Toast.makeText(requireContext(), "Task name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (task == null) {
            task = new Task();
        }
        task.setShortName(binding.editName.getText().toString());
        task.setDescription(binding.editDescription.getText().toString());
        task.setDone(binding.checkBox2.isChecked());
        task.setDate(binding.editData.getText().toString());
        task.setTaskListId(taskListId); // Set the correct taskListId

        if (taskListId == 0) {
            Toast.makeText(requireContext(), "Task list ID is invalid. Please select a valid task list.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (task.getId() == 0) {
            taskViewModel.insertTask(task);
        } else {
            taskViewModel.updateTask(task);
        }

        if (callbacks != null) {
            callbacks.onUpdateTask(task);
        }

        requireActivity().onBackPressed();
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

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    public void setOnTaskUpdatedListener(OnTaskUpdatedListener listener) {
        this.callbacks = listener;
    }
}
