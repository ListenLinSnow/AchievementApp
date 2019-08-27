package com.example.lc.achievementapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.common.AchievementStatus;
import com.example.lc.achievementapp.common.PersonalInfo;
import com.example.lc.achievementapp.common.Constant;

import java.util.ArrayList;
import java.util.List;

public class LocalData {

    private static DBHelper achiDbHelper = null;
    private static DBHelper achiTypeDbHelper = null;

    public static void initDBHelper(Context context){
        if (achiDbHelper == null){
            achiDbHelper = new DBHelper(context, DBHelper.ACHIEVEMENT_DATABASE, null, 1);
        }
        if (achiTypeDbHelper == null){
            achiTypeDbHelper = new DBHelper(context, DBHelper.ACHIEVEMENT_TYPE_DATABASE, null, 1);
        }
    }

    /**
     * 读取所需要的类型的数据列表
     * @param type
     * @return
     */
    public static List<Achievement> getAchiData(int type, String order){
        List<Achievement> achievementList = new ArrayList<>();

        SQLiteDatabase db = achiDbHelper.getReadableDatabase();
        Cursor cursor = null;
        if(type == Constant.ACHIEVEMENT_TYPE_ALL) {
            cursor = db.query(DBHelper.ACHIEVEMENT_TABLE, null, null, null, null, null, order);
        }else {
            cursor = db.query(DBHelper.ACHIEVEMENT_TABLE, null, DBHelper.TYPE_COLUMN + "= ?", new String[]{String.valueOf(type)}, null, null, order);
        }
        if(cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ID_COLUMN));
                long startDate = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.START_DATE_COLUMN));
                long endDate = cursor.getLong(cursor.getColumnIndexOrThrow(DBHelper.END_DATE_COLUMN));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TITLE_COLUMN));
                String subtitle = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.SUBTITLE_COLUMN));
                type = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.TYPE_COLUMN));
                int attitude = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.ATTITUDE_COLUMN));
                String remarks = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.REMARKS_COLUMN));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.STATUS_COLUMN));
                achievementList.add(new Achievement(id, startDate, endDate, title, subtitle, type, attitude, remarks, status));
            }
            cursor.close();
        }
        return achievementList;
    }

    /**
     * 查询某一类别是否存在
     * @param content
     * @return
     */
    public static boolean isTypeExists(String content){
        SQLiteDatabase db = achiTypeDbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.ACHIEVEMENT_TYPE_TABLE, null, DBHelper.TYPE_CONTENT_COLUMN + "= ?", new String[]{content}, null, null, null);
        if(cursor != null){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 新建一个新任务
     * @param startDate
     * @param title
     * @param type
     * @param status
     * @return
     */
    public static boolean insertAchiData(long startDate, long endDate, String title, String remarks, int type, @AchievementStatus int status){
        SQLiteDatabase db = achiDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.START_DATE_COLUMN, startDate);
        values.put(DBHelper.END_DATE_COLUMN, endDate);
        values.put(DBHelper.TITLE_COLUMN, title);
        values.put(DBHelper.REMARKS_COLUMN, remarks);
        values.put(DBHelper.TYPE_COLUMN, type);
        values.put(DBHelper.STATUS_COLUMN, status);
        long res = db.insert(DBHelper.ACHIEVEMENT_TABLE, null, values);
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 完成或弃坑一个任务
     * @param id
     * @param endDate
     * @return
     */
    public static boolean updateAchiDataStatus(int id, long endDate, @AchievementStatus int status){
        SQLiteDatabase db = achiDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.END_DATE_COLUMN, endDate);
        values.put(DBHelper.STATUS_COLUMN, status);
        long res = db.update(DBHelper.ACHIEVEMENT_TABLE, values, DBHelper.ID_COLUMN + "= ?", new String[]{String.valueOf(id)});
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 更新成就内容
     * @param id
     * @param startDate
     * @param endDate
     * @param type
     * @param remark
     * @return
     */
    public static boolean updateAchiDataContent(int id, long startDate, long endDate, int type, String title, String remark){
        SQLiteDatabase db = achiDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.START_DATE_COLUMN, startDate);
        values.put(DBHelper.END_DATE_COLUMN, endDate);
        values.put(DBHelper.TYPE_COLUMN, type);
        values.put(DBHelper.TITLE_COLUMN, title);
        values.put(DBHelper.REMARKS_COLUMN, remark);
        long res = db.update(DBHelper.ACHIEVEMENT_TABLE, values, DBHelper.ID_COLUMN  + "= ?", new String[]{String.valueOf(id)});
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 删除某项成就
     * @param id
     * @return
     */
    public static boolean deleteAchiData(int id){
        SQLiteDatabase db = achiDbHelper.getWritableDatabase();
        long res = db.delete(DBHelper.ACHIEVEMENT_TABLE, DBHelper.ID_COLUMN + "= ?", new String[]{String.valueOf(id)});
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 获取类型list
     * @return
     */
    public static List<AchievementType> getTypeListData(){
        List<AchievementType> typeList = new ArrayList<>();

        SQLiteDatabase db = achiTypeDbHelper.getReadableDatabase();
        //按照权重从低到高查询
        Cursor cursor = db.query(DBHelper.ACHIEVEMENT_TYPE_TABLE, null, null, null, null, null, DBHelper.TYPE_WEIGHT_COLUMN +" asc");
        if(cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.TYPE_ID_COLUMN));
                String icon = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TYPE_ICON_COLUMN));
                String content = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TYPE_CONTENT_COLUMN));
                int weight = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.TYPE_WEIGHT_COLUMN));
                typeList.add(new AchievementType(id, icon, content, weight));
            }
            cursor.close();
        }
        return typeList;
    }

    /**
     * 根据id获取类型
     * @param type
     * @return
     */
    public static AchievementType getTypeById(int type){
        SQLiteDatabase db = achiTypeDbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.ACHIEVEMENT_TYPE_TABLE, null, DBHelper.TYPE_ID_COLUMN + "= ?", new String[]{String.valueOf(type)}, null, null, null);
        cursor.moveToNext();
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.TYPE_ID_COLUMN));
        String icon = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TYPE_ICON_COLUMN));
        String content = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TYPE_CONTENT_COLUMN));
        int weight = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.TYPE_WEIGHT_COLUMN));
        cursor.close();
        return new AchievementType(id, icon, content, weight);
    }

    /**
     * 添加新成就类型
     * @param icon
     * @param content
     * @param weight
     * @return
     */
    public static boolean insertTypeData(String icon, String content, int weight){
        SQLiteDatabase db = achiTypeDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.TYPE_ICON_COLUMN, icon.toString());
        values.put(DBHelper.TYPE_CONTENT_COLUMN, content);
        values.put(DBHelper.TYPE_WEIGHT_COLUMN, weight);
        long res = db.insert(DBHelper.ACHIEVEMENT_TYPE_TABLE, null, values);
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 更新成就名称
     * @param id
     * @param content
     * @return
     */
    public static boolean updateTypeName(int id, String content){
        SQLiteDatabase db = achiTypeDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.TYPE_CONTENT_COLUMN, content);
        long res = db.update(DBHelper.ACHIEVEMENT_TYPE_TABLE, values, DBHelper.TYPE_ID_COLUMN + "= ?", new String[]{String.valueOf(id)});
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 更新成就类型图标
     * @param id
     * @param icon
     * @return
     */
    public static boolean updateTypeIcon(int id, String icon){
        SQLiteDatabase db = achiTypeDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.TYPE_ICON_COLUMN, icon);
        long res = db.update(DBHelper.ACHIEVEMENT_TYPE_TABLE, values, DBHelper.TYPE_ID_COLUMN + "= ?", new String[]{String.valueOf(id)});
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 更新成就类型内容
     * @param id
     * @param icon
     * @param content
     * @param weight
     * @return
     */
    public static boolean updateTypeData(int id, String icon, String content, int weight){
        SQLiteDatabase db = achiTypeDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.TYPE_ICON_COLUMN, icon);
        values.put(DBHelper.TYPE_CONTENT_COLUMN, content);
        values.put(DBHelper.TYPE_WEIGHT_COLUMN, weight);
        long res = db.update(DBHelper.ACHIEVEMENT_TYPE_TABLE, values, DBHelper.TYPE_ID_COLUMN + "= ?", new String[]{String.valueOf(id)});
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 删除某项成就类型
     * @param id
     * @return
     */
    public static boolean deleteTypeData(int id){
        SQLiteDatabase db = achiTypeDbHelper.getWritableDatabase();
        db.delete(DBHelper.ACHIEVEMENT_TYPE_TABLE, DBHelper.TYPE_ID_COLUMN + "= ?", new String[]{String.valueOf(id)});

        db = achiDbHelper.getWritableDatabase();
        long res = db.delete(DBHelper.ACHIEVEMENT_TABLE, DBHelper.TYPE_COLUMN + "= ?", new String[]{String.valueOf(id)});
        if(res == -1){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 修改个人信息内容
     * @param context
     * @param param
     * @param content
     * @return
     */
    public static boolean modifyPersonalData(Context context, @PersonalInfo String param, String content){
        SharedPreferences sp = context.getSharedPreferences(PersonalInfo.PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(param, content);
        return editor.commit();
    }

    /**
     * 读取个人信息内容
     * @param context
     * @param param
     * @return
     */
    public static String getPersonalData(Context context, @PersonalInfo String param){
        SharedPreferences sp = context.getSharedPreferences(PersonalInfo.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(param, Constant.EMPTY_STRING);
    }

}
