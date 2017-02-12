package com.example.incredibly.smarttodo.model;

import cn.bmob.v3.BmobUser;


public class MyUser extends BmobUser {

    private String headerImage;
    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }
}
