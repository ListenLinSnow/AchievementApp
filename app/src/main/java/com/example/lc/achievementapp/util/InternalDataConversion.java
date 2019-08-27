package com.example.lc.achievementapp.util;

import com.example.lc.achievementapp.common.AchievementStatus;

public class InternalDataConversion {

    public static String getStatusById(@AchievementStatus int status){
        switch (status){
            case AchievementStatus.INTEND:
                return "进行中";
            case AchievementStatus.ABANDONED:
                return "已弃坑";
            case AchievementStatus.COMPLETED:
                return "已完成";
        }
        return null;
    }

}
