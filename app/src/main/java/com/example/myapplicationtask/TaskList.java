package com.example.myapplicationtask;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_list_table")
public class TaskList {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;

    public TaskList(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
