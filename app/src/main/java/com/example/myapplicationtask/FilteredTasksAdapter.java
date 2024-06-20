package com.example.myapplicationtask;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FilteredTasksAdapter extends TaskListAdapter {

    private List<Task> originalTasks;
    private boolean showAllTasks;

    public FilteredTasksAdapter(List<Task> tasks, OnTaskSelectedListener listener) {
        super(new ArrayList<>(tasks), listener);
        this.originalTasks = new ArrayList<>(tasks);
        this.showAllTasks = true;
    }

    // Return all tasks or just uncompleted tasks
    private void filterTasks() {
        this.tasks = showAllTasks ? new ArrayList<>(originalTasks) :
                originalTasks.stream()
                        .filter(task -> !task.isDone())
                        .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public void setFilter(boolean showAll) {  // true to show all tasks, false to show uncompleted tasks
        this.showAllTasks = showAll;
        filterTasks();
    }

    public void updateTasks(List<Task> updateTasks) {  // Called after a modification of an item or new task
        this.originalTasks = new ArrayList<>(updateTasks);
        filterTasks();
    }
}
