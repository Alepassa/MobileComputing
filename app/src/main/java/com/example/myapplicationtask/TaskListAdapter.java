package com.example.myapplicationtask;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

        void onTaskStatusChanged(Task task);
    }

    protected List<Task> tasks;
    private final OnTaskSelectedListener listener;
    private final Context context;

    public TaskListAdapter(List<Task> tasks, OnTaskSelectedListener listener, Context context) {
        this.tasks = tasks;
        this.listener = listener;
        this.context = context;
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

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TaskListItemBinding binding;
        private final Handler handler = new Handler(Looper.getMainLooper());

        public TaskViewHolder(TaskListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task task) {
            Log.d("TaskViewHolder", "Binding task ID: " + task.getId() + ", Name: " + task.getShortName() + ", isChecked: " + task.isDone());
            binding.TaskNameTextView.setText(task.getShortName());
            binding.checkBox3.setChecked(task.isDone()); // Ensure checkbox reflects the current task state

            binding.getRoot().setOnClickListener(v -> {
                Log.d("TaskViewHolder", "Task clicked: " + task.getId());
                listener.onTaskSelected(task);
            });

            binding.checkBox3.setOnClickListener(v -> {
                boolean isChecked = binding.checkBox3.isChecked();
                Log.d("TaskViewHolder", "Checkbox clicked for Task: " + task.getId() + ", isChecked: " + isChecked);

                // Show a confirmation dialog before changing the task status
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Task Status Change")
                        .setMessage("Do you really want to change the status of this task?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            // Disable the checkbox temporarily to prevent rapid clicks
                            binding.checkBox3.setEnabled(false);

                            // Add a delay before processing the checkbox state change and re-enabling it
                            handler.postDelayed(() -> {
                                task.setDone(isChecked);
                                listener.onTaskStatusChanged(task); // Notify the listener of the state change
                                binding.checkBox3.setEnabled(true); // Re-enable the checkbox
                            }, 1000); // 1000 milliseconds delay
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> {
                            // Revert the checkbox state if the user cancels the action
                            binding.checkBox3.setChecked(!isChecked);
                        })
                        .show();
            });
        }
    }
}
