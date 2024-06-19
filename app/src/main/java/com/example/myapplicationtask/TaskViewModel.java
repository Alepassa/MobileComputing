package com.example.myapplicationtask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private TaskRepository taskRepository;
    private LiveData<List<TaskList>> allTaskLists;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepositoryDatabaseImpl(application);
        allTaskLists = taskRepository.getAllTaskLists();
    }

    public void insertTask(Task task) {
        taskRepository.insert(task);
    }

    public void updateTask(Task task) {
        taskRepository.update(task);
    }

    public void deleteTask(Task task) {
        taskRepository.delete(task);
    }

    public void deleteCompletedTasks() {
        taskRepository.deleteCompletedTasks();
    }

    public LiveData<List<Task>> getTasksByTaskListId(int taskListId) {
        return taskRepository.getTasksByTaskListId(taskListId);
    }

    public void insertTaskList(TaskList taskList) {
        taskRepository.insertTaskList(taskList);
    }

    public LiveData<List<TaskList>> getAllTaskLists() {
        return allTaskLists;
    }

    public void deleteCompletedTasksByTaskListId(int taskListId) {
        taskRepository.deleteCompletedTasksByTaskListId(taskListId);
    }
    public void deleteTaskListById(int taskListId) {
        taskRepository.deleteTaskListById(taskListId);
    }

}
