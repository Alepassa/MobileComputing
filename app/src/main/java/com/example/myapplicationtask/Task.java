package com.example.myapplicationtask;

import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;


public class Task {

    // simple ID generator
    private static int MAX_ID = 0;

    private int mId;
    private String mShortName;
    private String mDescription;
    private Date mCreationDate;
    private boolean mDone;

    public Task(String shortName) {
        this.mId = MAX_ID++;
        this.mShortName = shortName;
        this.mCreationDate = GregorianCalendar.getInstance().getTime();
    }

    public int getId() {
        return this.mId;
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

    public Date getCreationDate() {
        return mCreationDate;
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
