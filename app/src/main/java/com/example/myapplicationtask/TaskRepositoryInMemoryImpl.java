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
        // Add default task lists and tasks here for initial testing
        taskLists.put("Default", new ArrayList<>());
        taskLists.put("Home", new ArrayList<>());
        taskLists.get("Default").add(new Task("Sample Task 1", "Description 1", "Feb 20, 2024", false));
        taskLists.get("Home").add(new Task("Sample Task 2", "Description 2", "Feb 21, 2024", true));
    }

    @Override
    public List<Task> loadTasks(String taskListName) {
        return taskLists.getOrDefault(taskListName, new ArrayList<>());
    }

    @Override
    public void deleteFinishedTasks() {
        for (String key : taskLists.keySet()) {
            taskLists.get(key).removeIf(Task::isDone);
        }
    }

    @Override
    public void addTask(String taskListName, Task task) {
        List<Task> tasks = taskLists.get(taskListName);
        if (tasks != null) {
            tasks.add(task);
        }
    }

    @Override
    public List<String> getTaskLists() {
        return new ArrayList<>(taskLists.keySet());
    }
}
