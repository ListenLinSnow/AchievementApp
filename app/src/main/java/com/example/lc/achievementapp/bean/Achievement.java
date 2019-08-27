package com.example.lc.achievementapp.bean;

import com.example.lc.achievementapp.common.AchievementAttitude;
import com.example.lc.achievementapp.common.AchievementStatus;

import java.io.Serializable;

/**
 * 成就
 */

public class Achievement implements Serializable {

    int id;                                         //id
    long startDate;                                 //起始日期
    long endDate;                                   //结束日期
    String title;                                   //标题
    String subtitle;                                //副标题
    int type;                                       //类型
    @AchievementAttitude
    int attitude;                                   //喜好态度
    String remarks;                                 //备注
    @AchievementStatus
    int status;                                     //事件状态

    public Achievement(int id, long startDate, long endDate, String title, String subtitle, int type, int attitude, String remarks, int status) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.attitude = attitude;
        this.remarks = remarks;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAttitude() {
        return attitude;
    }

    public void setAttitude(int attitude) {
        this.attitude = attitude;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
