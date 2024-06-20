package com.example.myapplicationtask;


import android.os.Handler;
import android.util.Log;
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
        Log.d("TaskListAdapter", "onCreateViewHolder called");
        TaskListItemBinding binding = TaskListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        Log.d("TaskListAdapter", "onBindViewHolder for position: " + position + ", task ID: " + task.getId());
        holder.bind(task);
    }

    public void removeTask(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    public void moveTask(int fromPosition, int toPosition) {
        Task movedTask = tasks.remove(fromPosition);
        tasks.add(toPosition, movedTask);
        notifyItemMoved(fromPosition, toPosition);
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
            binding.checkBox3.setOnCheckedChangeListener(null);

            binding.checkBox3.setChecked(task.isDone());
            binding.checkBox3.setOnClickListener(v -> {
                boolean isChecked = binding.checkBox3.isChecked();
                task.setDone(isChecked);
                Log.d("TaskListAdapter", "Checkbox clicked for Task: " + task.getId() + ", isChecked: " + isChecked);

                binding.checkBox3.setEnabled(false);

                new Handler().postDelayed(() -> {
                    listener.onTaskStatusChanged(task);
                    binding.checkBox3.setEnabled(true);
                }, 2000);
            });

            binding.getRoot().setOnClickListener(v -> listener.onTaskSelected(task));
        }
    }

}

