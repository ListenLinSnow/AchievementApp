package com.example.lc.achievementapp.common;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 一些个人信息的本地属性设置
 */

@StringDef({
        PersonalInfo.PREFERENCE_NAME,       //本地存储文件名
        PersonalInfo.AVATAR_PATH,           //头像路径
        PersonalInfo.USERNAME,              //用户名
        PersonalInfo.AUTOGRAPH,             //个性签名
        PersonalInfo.TEXT_FONT,             //字体
})
@Retention(RetentionPolicy.SOURCE)
public @interface PersonalInfo {

    String PREFERENCE_NAME = "personal";
    String AVATAR_PATH = "avatar";
    String USERNAME = "username";
    String AUTOGRAPH = "autograph";
    String TEXT_FONT = "textfont";

}
