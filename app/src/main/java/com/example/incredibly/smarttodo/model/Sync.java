package com.example.incredibly.smarttodo.model;

public class Sync {

    public static final int TYPE_INVALID= 0;
    public static final int TYPE_TASK = 1;
    public static final int TYPE_REVIEW= 2;
    public static final int TYPE_NOTIFY= 3;
    public static final int TYPE_CATEGORY= 4;

    private int id;
    private int type;
    private long updateTime;
    private boolean isDelete;
    private String objId;

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public Sync() {
        type = TYPE_INVALID;
        this.updateTime = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

}
