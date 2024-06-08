package com.example.myapplicationtask;

import java.util.List;

public interface TaskRepository {
    List<Task> loadTasks(String taskListName);
    void deleteFinishedTasks();
    void addTask(String taskListName, Task task);
    List<String> getTaskLists();
}
