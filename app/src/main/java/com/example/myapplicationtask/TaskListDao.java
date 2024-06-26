package com.example.myapplicationtask;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskListDao {
    @Insert
    void insert(TaskList taskList);

    @Query("SELECT * FROM task_list_table ORDER BY id ASC")
    LiveData<List<TaskList>> getAllTaskLists();

    @Query("SELECT * FROM task_list_table WHERE name = :taskListName LIMIT 1")
    TaskList getTaskListByName(String taskListName);

    @Query("SELECT * FROM task_list_table WHERE id = :taskListId LIMIT 1")
    TaskList getTaskListById(int taskListId);

    @Query("DELETE FROM task_list_table WHERE id = :taskListId")
    void deleteTaskListById(int taskListId);

    // Metodo per ottenere il nome della lista in modo sincrono
    @Query("SELECT name FROM task_list_table WHERE id = :taskListId")
    String getTaskListNameByIdSync(int taskListId);

    @Query("SELECT id FROM task_list_table LIMIT 1")
    int getFirstTaskListId();
}
