package com.example.myapplicationtask;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplicationtask.databinding.FragmentTaskListBinding;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment implements TaskListAdapter.OnTaskSelectedListener {

    private FragmentTaskListBinding binding;
    private FilteredTasksAdapter adapter;
    private TaskListFragmentCallbacks callback;
    private boolean showAllTask = true;
    private TaskViewModel taskViewModel;

    public interface TaskListFragmentCallbacks {
        void onTaskSelected(Task task);
        void onClearCompletedTasks();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof TaskListFragmentCallbacks) {
            callback = (TaskListFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TaskListFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("showUnfinishedTasks", showAllTask);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        taskViewModel = new TaskViewModel(requireActivity().getApplication());
        setupRecyclerView();
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            showAllTask = savedInstanceState.getBoolean("showUnfinishedTasks", false);
            adapter.setFilter(showAllTask);
        }

        return view;
    }

    private void setupRecyclerView() {
        adapter = new FilteredTasksAdapter(new ArrayList<>(), this);
        binding.listview.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.listview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.listview.addItemDecoration(new SpaceItem(20));
        binding.listview.setAdapter(adapter);
    }

    @Override
    public void onTaskSelected(Task task) {
        if (callback != null) {
            callback.onTaskSelected(task);
        }
    }

    @Override
    public void onTaskStatusChanged(Task task) {
        // Update the task in the database through the ViewModel
        taskViewModel.updateTask(task);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.firstOption) {
            adapter.setFilter(true);
            showAllTask = true;
            return true;
        } else if (item.getItemId() == R.id.secondOption) {
            showAllTask = false;
            adapter.setFilter(false);
            return true;
        } else if (item.getItemId() == R.id.thirdOption) {
            if (callback != null) {
                callback.onClearCompletedTasks();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateTasks(List<Task> tasks) {
        adapter.updateTasks(tasks);
    }
}
