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

    @Query("SELECT * FROM task_table WHERE mId = :id")
    Task getTaskById(int id);

    @Query("SELECT * FROM task_table")
    LiveData<List<Task>> getAllTasks();

    @Query("DELETE FROM task_table WHERE mDone = 1")
    void deleteCompletedTasks();
}
