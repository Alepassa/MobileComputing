package com.example.myapplicationtask;

import java.util.List;



public interface TaskRepository {

    List<Task> loadTasks();

    void deleteFinishedTasks();

    // TODO: add methods for adding new or updating existing tasks
}
