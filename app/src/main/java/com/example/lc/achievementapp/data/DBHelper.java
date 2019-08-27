package com.example.lc.achievementapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    //成就事件数据库
    public static String ACHIEVEMENT_DATABASE = "Achievement";
    public static String ACHIEVEMENT_TABLE = "achievement";

    //成就类型数据库
    public static String ACHIEVEMENT_TYPE_DATABASE = "AchievementType";
    public static String ACHIEVEMENT_TYPE_TABLE = "achievementType";

    //成就事件列
    public static String ID_COLUMN = "id";
    public static String START_DATE_COLUMN = "startDate";
    public static String END_DATE_COLUMN = "endDate";
    public static String TITLE_COLUMN = "title";
    public static String SUBTITLE_COLUMN = "subtitle";
    public static String TYPE_COLUMN = "type";
    public static String ATTITUDE_COLUMN = "attitude";
    public static String REMARKS_COLUMN = "remarks";
    public static String STATUS_COLUMN = "status";

    //成就类型列
    public static String TYPE_ID_COLUMN = "id";
    public static String TYPE_ICON_COLUMN = "icon";
    public static String TYPE_CONTENT_COLUMN = "content";
    public static String TYPE_WEIGHT_COLUMN = "weight";

    /**
     * 成就事件数据库
     */
    private static String CREATE_ACHIEVEMENT = "create table " + ACHIEVEMENT_TABLE + "("
            + ID_COLUMN + " integer primary key autoincrement not null,"     //事件id
            + START_DATE_COLUMN + " long,"                                   //起始日期
            + END_DATE_COLUMN + " long,"                                     //结束日期
            + TITLE_COLUMN + " text not null,"                               //标题
            + SUBTITLE_COLUMN + " text,"                                     //副标题
            + TYPE_COLUMN + " integer,"                                      //类型
            + ATTITUDE_COLUMN + " integer,"                                  //喜恶态度
            + REMARKS_COLUMN + " text,"                                      //备注(结束时使用)
            + STATUS_COLUMN + " integer)";                                   //事件状态

    /**
     * 成就类型数据库
     */
    private static String CREATE_ACHIEVEMENT_TYPE = "create table " + ACHIEVEMENT_TYPE_TABLE + "("
            + TYPE_ID_COLUMN + " integer primary key autoincrement not null,"        //类型id
            + TYPE_ICON_COLUMN + " text,"                                            //类型图标(保存的是uri)
            + TYPE_CONTENT_COLUMN + " text,"                                         //类型内容
            + TYPE_WEIGHT_COLUMN + " integer)";                                      //类型权重(即查看顺序)

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACHIEVEMENT);
        db.execSQL(CREATE_ACHIEVEMENT_TYPE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
