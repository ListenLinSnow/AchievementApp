package com.example.lc.achievementapp.common;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 成就喜恶态度
 */

@IntDef({
        AchievementAttitude.NEUTRAL,        //持保留态度
        AchievementAttitude.LIKE,           //喜欢
        AchievementAttitude.DISLIKE         //不喜欢
})
@Retention(RetentionPolicy.SOURCE)
public @interface AchievementAttitude {

    int NEUTRAL = 0;
    int LIKE = 1;
    int DISLIKE = 2;

}
