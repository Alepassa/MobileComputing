package com.example.myapplicationtask;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepositoryDatabaseImpl implements TaskRepository {
    private TaskDao taskDao;
    private TaskListDao taskListDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<TaskList>> allTaskLists;
    private ExecutorService executorService;

    public TaskRepositoryDatabaseImpl(Application application) {
        TaskDatabase db = TaskDatabase.getDatabase(application);
        taskDao = db.taskDao();
        taskListDao = db.taskListDao();
        allTasks = taskDao.getAllTasks();
        allTaskLists = taskListDao.getAllTaskLists();
        executorService = Executors.newFixedThreadPool(2);
    }

    @Override
    public void insert(Task task) {
        executorService.execute(() -> {
            TaskList taskList = taskListDao.getTaskListById(task.getTaskListId());
            if (taskList != null) {
                Log.d("TaskRepository", "Inserting task: " + task.getShortName() + " into TaskList ID: " + task.getTaskListId());
                taskDao.insert(task);
            } else {
                Log.e("TaskRepository", "TaskList with id " + task.getTaskListId() + " does not exist. Cannot insert task.");
            }
        });
    }

    @Override
    public void update(Task task) {
        executorService.execute(() -> {
            TaskList taskList = taskListDao.getTaskListById(task.getTaskListId());
            if (taskList != null) {
                Log.d("TaskRepository", "Updating task: " + task.getShortName() + " in TaskList ID: " + task.getTaskListId());
                taskDao.update(task);
            } else {
                Log.e("TaskRepository", "TaskList with id " + task.getTaskListId() + " does not exist. Cannot update task.");
            }
        });
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
    public LiveData<List<TaskList>> getTaskLists() {
        return allTaskLists;
    }

    @Override
    public LiveData<List<Task>> loadTasks(String taskListName) {
        TaskList taskList = taskListDao.getTaskListByName(taskListName);
        if (taskList != null) {
            return taskDao.getTasksByTaskListId(taskList.getId());
        }
        return new MutableLiveData<>(new ArrayList<>()); // Return empty LiveData if taskList is null
    }

    @Override
    public void insertTaskList(TaskList taskList) {
        executorService.execute(() -> {
            taskListDao.insert(taskList);
            Log.d("TaskRepository", "Inserted TaskList: " + taskList.getName());
        });
    }

    @Override
    public LiveData<List<TaskList>> getAllTaskLists() {
        return allTaskLists;
    }

    @Override
    public LiveData<List<Task>> getTasksByTaskListId(int taskListId) {
        return taskDao.getTasksByTaskListId(taskListId);
    }

    @Override
    public void deleteCompletedTasksByTaskListId(int taskListId) {
        executorService.execute(() -> taskDao.deleteCompletedTasksByTaskListId(taskListId));
    }

    @Override
    public void deleteTaskListById(int taskListId) {
        executorService.execute(() -> taskListDao.deleteTaskListById(taskListId));
    }
}
