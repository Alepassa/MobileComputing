package com.example.myapplicationtask;

import java.util.List;

public interface TaskRepository {
    List<Task> loadTasks(String taskListName);
    void saveTasks(String taskListName, List<Task> tasks); // Definizione del metodo per salvare le task
    List<String> getTaskLists();
}
