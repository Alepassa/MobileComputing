package com.example.myapplicationtask;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.ActivityListTaskBinding;
import com.example.myapplicationtask.databinding.TaskListItemBinding;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);

        void onTaskStatusChanged(Task task);
    }

    protected List<Task> tasks;
    private final OnTaskSelectedListener listener;

    public TaskListAdapter(List<Task> tasks, OnTaskSelectedListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaskListItemBinding binding = TaskListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TaskListItemBinding binding;

        public TaskViewHolder(TaskListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task task) {
            binding.TaskNameTextView.setText(task.getShortName());
            binding.checkBox3.setChecked(task.isDone());
            binding.getRoot().setOnClickListener(v -> listener.onTaskSelected(task));
            binding.checkBox3.setOnClickListener(v -> {
                boolean isChecked = binding.checkBox3.isChecked();
                task.setDone(isChecked);
                listener.onTaskStatusChanged(task); // Notify listener of status change

            });
        }
    }
}