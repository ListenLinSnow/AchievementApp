package com.example.lc.achievementapp.bean;

import android.net.Uri;

/**
 * 成就类型
 */

public class AchievementType {

    int id;                     //id
    String icon;                   //图标
    String content;             //内容
    int weight;                 //权重

    public AchievementType(String icon, String content, int weight) {
        this.icon = icon;
        this.content = content;
        this.weight = weight;
    }

    public AchievementType(int id, String icon, String content, int weight) {
        this.id = id;
        this.icon = icon;
        this.content = content;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
