package com.example.myapplicationtask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;


public class SwipeToDeleteCallBack extends ItemTouchHelper.SimpleCallback {

    private final TaskListAdapter adapter;
    private final TaskViewModel taskViewModel;

    public SwipeToDeleteCallBack(TaskListAdapter adapter, TaskViewModel taskViewModel) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        this.taskViewModel = taskViewModel;

    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Task taskToDelete = adapter.getTasks().get(position);

        taskViewModel.deleteTask(taskToDelete);

        adapter.removeTask(position);
    }
}