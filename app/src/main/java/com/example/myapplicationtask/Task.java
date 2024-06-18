package com.example.myapplicationtask;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "task_table")
public class Task implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int mId;
    private String mShortName;
    private String mDescription;
    private String mDate;
    private boolean mDone;

    public Task(String shortName) {
        this.mShortName = shortName;
    }
    public Task(String shortName, String mDescription, String mDate, Boolean mDone) {
        this.mShortName = shortName;
        this.mDescription= mDescription;
        this.mDate = mDate;
        this.mDone= mDone;
    }

    public Task() {
        this.mShortName = "";
        this.mDescription = "";
        this.mDate = "";
        this.mDone = false;
    }

    protected Task(Parcel in) {
        mId = in.readInt();
        mShortName = in.readString();
        mDescription = in.readString();
        mDate = in.readString();
        mDone = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mShortName);
        dest.writeString(mDescription);
        dest.writeString(mDate);
        dest.writeByte((byte) (mDone ? 1 : 0));
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

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getDate() {
        return mDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public boolean isDone() {
        return mDone;
    }

    public void setDone(boolean done) {
        this.mDone = done;
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
