package com.example.lc.achievementapp.common;

import android.os.Environment;

import java.io.File;

public class Constant {

    public static final int ACHIEVEMENT_TYPE_ALL = 0;

    public static final String EMPTY_STRING = "";

    public static final String APP_FOLDER_NAME = "AchievementApp";
    public static final String APP_FOLDER_PATH = Environment.getExternalStorageDirectory() + File.separator + APP_FOLDER_NAME;
    public static final String COVER_PATH = APP_FOLDER_PATH + File.separator + "cover.jpg";
    public static final String AVATAR_PATH = APP_FOLDER_PATH + File.separator + "avatar.jpg";
    public static final String TYPE_PATH = APP_FOLDER_PATH + File.separator + "type";

}
