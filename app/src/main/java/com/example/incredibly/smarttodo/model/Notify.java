package com.example.incredibly.smarttodo.model;

import cn.bmob.v3.BmobObject;

public class Notify extends BmobObject {
    public static final int TIME_TYPE_MINITE = 1;
    public static final int TIME_TYPE_HOUR = 2;
    public static final int TIME_TYPE_DAY = 3;
    public static final int TIME_TYPE_WEEK = 4;
    public static final int TIME_TYPE_MONTH = 5;
    public static final int TIME_TYPE_FIX = 6;
    public static final int TIME_TYPE_AT_TIME = 0;

    private int timeType;
    private int id, taskId, count;
    private boolean isDone;
    private long notifyTime;
    private long updateTime;
    private MyUser myUser;

    public Notify() {
    }

    public Notify(int timeType, int id, boolean isDone, long notifyTime) {
        this.timeType = timeType;
        this.id = id;
        this.isDone = isDone;
        this.notifyTime = notifyTime;
    }

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

    public long getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(long notifyTime) {
        this.notifyTime = notifyTime;
    }

    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

}
