package com.example.myapplicationtask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplicationtask.databinding.TaskListItemBinding;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class FilteredTasksAdapter extends TaskListAdapter {

    private List<Task> originalTasks;
    private boolean showAllTasks;

    public FilteredTasksAdapter(List<Task> tasks, OnTaskSelectedListener listener) {
        super(new ArrayList<>(tasks), listener);
        this.originalTasks = new ArrayList<>(tasks);
        this.showAllTasks = true;  //default show all task
    }

    // return all task or just uncompleted tasks
    private void filterTasks() {
        this.tasks = showAllTasks ? new ArrayList<>(originalTasks):
                originalTasks.stream()
                        .filter(task -> !task.isDone())
                        .collect(Collectors.toList());
        notifyDataSetChanged();
    }


    public void setFilter(boolean showAll) {  //true all task, false uncompleted task
        this.showAllTasks = showAll;
        filterTasks();
    }

    public void updateTasks(List<Task> updateTasks) {  //called after a modify of item or new task
        this.originalTasks = new ArrayList<>(updateTasks);
        filterTasks();
    }

}