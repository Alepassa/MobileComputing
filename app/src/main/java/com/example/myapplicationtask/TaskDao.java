package com.example.myapplicationtask;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM task_table WHERE mDone = 1")
    void deleteCompletedTasks();

    @Query("SELECT * FROM task_table ORDER BY mId ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM task_table WHERE mId = :id")
    Task getTaskById(int id);

    @Query("SELECT DISTINCT mShortName FROM task_table")
    LiveData<List<String>> getTaskLists();

    @Query("SELECT * FROM task_table WHERE mShortName = :taskListName")
    LiveData<List<Task>> loadTasks(String taskListName);
}
