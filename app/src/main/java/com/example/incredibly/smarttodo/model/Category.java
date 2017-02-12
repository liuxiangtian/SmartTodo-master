package com.example.incredibly.smarttodo.model;

import cn.bmob.v3.BmobObject;

public class Category extends BmobObject {

    public static final String CATEGORY_DEFAULT = "默认";

    private int id;
    private String name;
    private boolean isHide = false;
    private long updateTime;
    private MyUser mMyUser;

    public MyUser getMyUser() {
        return mMyUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public void setMyUser(MyUser myUser) {
        mMyUser = myUser;
    }

    public Category() {
        this.name = CATEGORY_DEFAULT;
    }

    public Category(String name, boolean isHide) {
        this.name = name;
        this.isHide = isHide;
    }

    public Category(String name) {
        this.name = name;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Category){
            Category category = (Category) obj;
            if(category.getName().equals(name) && (category.isHide()==isHide)){
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
