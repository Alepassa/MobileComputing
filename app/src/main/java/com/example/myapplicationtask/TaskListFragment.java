package com.example.myapplicationtask;


import static com.example.myapplicationtask.TaskListActivity.SPACE_ITEM;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class TaskListFragment extends Fragment {

    private RecyclerView recyclerView;
    private FilteredTasksAdapter adapter;
    private Map<String, List<Task>> taskLists;
    private String currentTaskListName;

    public interface OnTaskSelectedListener {
        void onTaskSelected(Task task);
    }

    private OnTaskSelectedListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnTaskSelectedListener) {
            callback = (OnTaskSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTaskSelectedListener");
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
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);
        recyclerView = view.findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new SpaceItem(TaskListActivity.SPACE_ITEM));
        adapter = new FilteredTasksAdapter(new ArrayList<>(), task -> {
            if (callback != null) {
                callback.onTaskSelected(task);
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void updateTasks(Map<String, List<Task>> taskLists, String currentTaskListName) {
        this.taskLists = taskLists;
        this.currentTaskListName = currentTaskListName;
        List<Task> tasks = taskLists.get(currentTaskListName);
        if (tasks == null) {
            tasks = new ArrayList<>();
            taskLists.put(currentTaskListName, tasks);
        }
        adapter.updateTasks(tasks);
    }
}

