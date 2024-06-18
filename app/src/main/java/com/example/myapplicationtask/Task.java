package com.example.myapplicationtask;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table", foreignKeys = @ForeignKey(entity = TaskList.class,
        parentColumns = "id",
        childColumns = "taskListId",
        onDelete = ForeignKey.CASCADE))
public class Task implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int mId;
    private String mShortName;
    private String mDescription;
    private String mDate;
    private boolean mDone;
    private int taskListId;

    public Task(int mId, String mShortName, String mDescription, String mDate, boolean mDone, int taskListId) {
        this.mId = mId;
        this.mShortName = mShortName;
        this.mDescription = mDescription;
        this.mDate = mDate;
        this.mDone = mDone;
        this.taskListId = taskListId;
    }

    @Ignore
    public Task(String shortName, String mDescription, String mDate, boolean mDone, int taskListId) {
        this.mShortName = shortName;
        this.mDescription = mDescription;
        this.mDate = mDate;
        this.mDone = mDone;
        this.taskListId = taskListId;
    }

    @Ignore
    public Task() {
        this.mShortName = "";
        this.mDescription = "";
        this.mDate = "";
        this.mDone = false;
    }

    @Ignore
    protected Task(Parcel in) {
        mId = in.readInt();
        mShortName = in.readString();
        mDescription = in.readString();
        mDate = in.readString();
        mDone = in.readByte() != 0;
        taskListId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mShortName);
        dest.writeString(mDescription);
        dest.writeString(mDate);
        dest.writeByte((byte) (mDone ? 1 : 0));
        dest.writeInt(taskListId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getShortName() {
        return mShortName;
    }

    public void setShortName(String shortName) {
        this.mShortName = shortName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public boolean isDone() {
        return mDone;
    }

    public void setDone(boolean done) {
        this.mDone = done;
    }

    public int getTaskListId() {
        return taskListId;
    }

    public void setTaskListId(int taskListId) {
        this.taskListId = taskListId;
    }

    @Override
    public String toString() {
        return mShortName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Task) {
            return this.getId() == ((Task) obj).getId();
        }
        return false;
    }
}
