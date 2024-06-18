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

    List<Task> loadTasks(String taskListName);
    void saveTasks(String taskListName, List<Task> tasks);
    List<String> getTaskLists();
}
