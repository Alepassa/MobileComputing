package com.example.myapplicationtask;

import androidx.lifecycle.LiveData;
import java.util.List;

public interface TaskRepository {
    void insert(Task task);
    void update(Task task);
    void delete(Task task);
    LiveData<List<Task>> getAllTasks();
    Task getTaskById(int id);
    void deleteCompletedTasks();
    LiveData<List<String>> getTaskLists();
    LiveData<List<Task>> loadTasks(String taskListName);
}
