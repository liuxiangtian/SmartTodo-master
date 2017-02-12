package com.example.incredibly.smarttodo.model;

import cn.bmob.v3.BmobObject;

public class Review extends BmobObject {
    public static final int TIME_TYPE_DAY = 1;
    public static final int TIME_TYPE_WEEK = 2;
    public static final int TIME_TYPE_WEEKEND = 3;
    public static final int TIME_TYPE_MONTH = 4;
    public static final int TIME_TYPE_FIX = 5;
    public static final int TIME_TYPE_MULTI = 6;

    private int id, taskId, count;
    private int index;
    private int timeType;
    private boolean isDone;
    private long reviewTime;
    private long updateTime;
    private MyUser myUser;

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public Review() {
        this.updateTime = System.currentTimeMillis();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimeType() {
        return timeType;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
    }

    public long getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(long reviewTime) {
        this.reviewTime = reviewTime;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }
}
