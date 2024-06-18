package com.example.myapplicationtask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository taskRepository;
    private LiveData<List<Task>> allTasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepositoryDatabaseImpl(application);
        allTasks = taskRepository.getAllTasks();
    }

    public void insert(Task task) {
        taskRepository.insert(task);
    }

    public void update(Task task) {
        taskRepository.update(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public Task getTaskById(int id) {
        return taskRepository.getTaskById(id);
    }

    public void deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks();
    }
}
