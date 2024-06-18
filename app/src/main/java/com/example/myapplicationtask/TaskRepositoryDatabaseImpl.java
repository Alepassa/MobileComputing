package com.example.myapplicationtask;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepositoryDatabaseImpl implements TaskRepository {
    private TaskDao taskDao;
    private LiveData<List<Task>> allTasks;
    private ExecutorService executorService;

    public TaskRepositoryDatabaseImpl(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        taskDao = db.taskDao();
        allTasks = taskDao.getAllTasks();
        executorService = Executors.newFixedThreadPool(2);
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
    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    @Override
    public Task getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    @Override
    public void deleteCompletedTasks() {
        executorService.execute(taskDao::deleteCompletedTasks);
    }

    @Override
    public LiveData<List<String>> getTaskLists() {
        return taskDao.getTaskLists();
    }

    @Override
    public LiveData<List<Task>> loadTasks(String taskListName) {
        return taskDao.loadTasks(taskListName);
    }
}
