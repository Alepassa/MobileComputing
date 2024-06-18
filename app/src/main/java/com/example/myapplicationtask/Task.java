package com.example.myapplicationtask;

import android.os.Parcel;
import android.os.Parcelable;


import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by thorsten on 21.03.20.
 */

public class  Task implements Parcelable {

    // simple ID generator
    private static int MAX_ID = 0;

    private int mId;
    private String mShortName;
    private String mDescription;
    private String mDate;
    private boolean mDone;

    public Task(String shortName) {
        this.mId = MAX_ID++;
        this.mShortName = shortName;
    }
    public Task(String shortName, String mDescription, String mDate, Boolean mDone) {
        this.mId = MAX_ID++;
        this.mShortName = shortName;
        this.mDescription= mDescription;
        this.mDate = mDate;
        this.mDone= mDone;
    }

    public Task() {
        this.mId = MAX_ID++;
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
        return this.mId;
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