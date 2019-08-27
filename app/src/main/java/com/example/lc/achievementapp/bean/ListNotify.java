package com.example.lc.achievementapp.bean;

/**
 * 成就列表、类型列表 更新通知
 */
public class ListNotify {

    private boolean refreshAchievementList = false;             //是否更新成就列表
    private boolean refreshTypeList = false;                    //是否更新类型列表

    public ListNotify(boolean refreshAchievementList, boolean refreshTypeList) {
        this.refreshAchievementList = refreshAchievementList;
        this.refreshTypeList = refreshTypeList;
    }

    public boolean isRefreshAchievementList() {
        return refreshAchievementList;
    }

    public void setRefreshAchievementList(boolean refreshAchievementList) {
        this.refreshAchievementList = refreshAchievementList;
    }

    public boolean isRefreshTypeList() {
        return refreshTypeList;
    }

    public void setRefreshTypeList(boolean refreshTypeList) {
        this.refreshTypeList = refreshTypeList;
    }
}
