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
    LiveData<List<TaskList>> getTaskLists();
    LiveData<List<Task>> loadTasks(String taskListName);
    void insertTaskList(TaskList taskList);
    LiveData<List<TaskList>> getAllTaskLists();
    LiveData<List<Task>> getTasksByTaskListId(int taskListId);
    void deleteCompletedTasksByTaskListId(int taskListId);
    void deleteTaskListById(int taskListId);

    void deleteTaskById(int taskId);

     String getTaskListNameByIdSync(int taskListId);
    List<Task> getTasksByTaskListIdSync(int taskListId);

    int getFirstTaskListId();
}
