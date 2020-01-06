package com.example.lc.achievementapp.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 成就事件状态
 */

@IntDef({
        AchievementStatus.INTEND,           //计划中
        AchievementStatus.ONGOING,          //进行中
        AchievementStatus.COMPLETED,        //已完成
        AchievementStatus.ABANDONED         //已弃坑
})
@Retention(RetentionPolicy.SOURCE)
public @interface AchievementStatus {

    int INTEND = 1;
    int ONGOING = 2;
    int COMPLETED = 3;
    int ABANDONED = 4;

}
