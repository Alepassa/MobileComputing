package com.example.myapplicationtask;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepositoryDatabaseImpl implements TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;
    private ExecutorService executorService;

    public TaskRepositoryDatabaseImpl(Context context) {
        TaskDatabase db = TaskDatabase.getDatabase(context);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void insert(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    @Override
    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    @Override
    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    @Override
    public Task getTaskById(int id) {
        // Implement as a blocking call for simplicity
        return taskDao.getTaskById(id);
    }

    @Override
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    @Override
    public List<Task> loadTasks(String taskListName) {
        // Implement this if you need this method for the database repository
        return null;
    }

    @Override
    public void saveTasks(String taskListName, List<Task> tasks) {
        // Implement this if you need this method for the database repository
    }

    @Override
    public List<String> getTaskLists() {
        // Implement this if you need this method for the database repository
        return null;
    }

    @Override
    public void deleteCompletedTasks() {
        executorService.execute(() -> taskDao.deleteCompletedTasks());
    }
}
