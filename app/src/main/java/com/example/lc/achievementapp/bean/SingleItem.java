package com.example.lc.achievementapp.bean;

public class SingleItem {

    int imgId;
    int desc;

    public SingleItem(int imgId, int desc) {
        this.imgId = imgId;
        this.desc = desc;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public int getDesc() {
        return desc;
    }

    public void setDesc(int desc) {
        this.desc = desc;
    }
}
