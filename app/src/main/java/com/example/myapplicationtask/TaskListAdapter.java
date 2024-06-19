package com.example.myapplicationtask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationtask.databinding.TaskListItemBinding;

import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskViewHolder> {

    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);
        void onTaskStatusChanged(Task task);  // New method for task status change
    }

    protected List<Task> tasks;
    private OnTaskSelectedListener listener;

    public TaskListAdapter(List<Task> tasks, OnTaskSelectedListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskListItemBinding binding = TaskListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bindItem(task);

        holder.binding.checkBox3.setOnCheckedChangeListener(null);  // Reset listener to avoid flickering
        holder.binding.checkBox3.setChecked(task.isDone());

        holder.binding.checkBox3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setDone(isChecked);
            if (listener != null) {
                listener.onTaskStatusChanged(task);  // Notify listener of the status change
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskSelected(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TaskListItemBinding binding;

        public TaskViewHolder(TaskListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindItem(Task task) {
            binding.checkBox3.setChecked(task.isDone());
            binding.TaskNameTextView.setText(task.getShortName());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskSelected(task);
                }
            });
        }
    }
}
