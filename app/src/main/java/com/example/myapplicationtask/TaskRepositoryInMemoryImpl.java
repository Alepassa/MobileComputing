package com.example.myapplicationtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskRepositoryInMemoryImpl implements TaskRepository {
    private static TaskRepositoryInMemoryImpl instance;

    private Map<String, List<Task>> taskLists;

    public static synchronized TaskRepositoryInMemoryImpl getInstance() {
        if (instance == null) {
            instance = new TaskRepositoryInMemoryImpl();
        }
        return instance;
    }

    private TaskRepositoryInMemoryImpl() {
        taskLists = new HashMap<>();
        taskLists.put("Groceries", new ArrayList<>());
        taskLists.put("University", new ArrayList<>());

        taskLists.get("Groceries").add(new Task("Buy Milk", "2 liters of milk", "June 22, 2024", false));
        taskLists.get("Groceries").add(new Task("Buy Chocolate", "", "", false));
        taskLists.get("Groceries").add(new Task("Buy Bread", "Whole grain bread", "June 23, 2024", false));
    }

    @Override
    public List<Task> loadTasks(String taskListName) {
        return taskLists.getOrDefault(taskListName, new ArrayList<>());
    }

    @Override
    public void saveTasks(String taskListName, List<Task> tasks) {
        taskLists.put(taskListName, tasks); // Salvataggio delle task nel repository
    }

    @Override
    public List<String> getTaskLists() {
        return new ArrayList<>(taskLists.keySet());
    }
}
