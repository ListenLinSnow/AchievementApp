package com.example.lc.achievementapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * 检查权限，主要用于高于Android6.0的动态权限管理情况
 */

public class CheckPermissionUtil {

    public static final int REQUEST_CODE = 100;

    /**
     * 日历
     */
    public static final String[] PERMISSION_CALENDAR = { Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR };

    /**
     * 摄像头
     */
    public static final String[] PERMISSION_CAMERA = { Manifest.permission.CAMERA };

    /**
     * 通讯录
     */
    public static final String[] PERMISSION_CONTACTS = { Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.GET_ACCOUNTS };

    /**
     * 地理位置
     */
    public static final String[] PERMISSION_LOCATION = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION };

    /**
     * 麦克风
     */
    public static final String[] PERMISSION_MICROPHONE = { Manifest.permission.RECORD_AUDIO };

    /**
     * 手机状态读取
     */
    public static final String[] PERMISSION_PHONE = { Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG,
                                                        Manifest.permission.WRITE_CALL_LOG, Manifest.permission.ADD_VOICEMAIL, Manifest.permission.USE_SIP,
                                                        Manifest.permission.PROCESS_OUTGOING_CALLS };

    /**
     * 身体传感器
     */
    public static final String[] PERMISSION_SENSORS = { Manifest.permission.BODY_SENSORS };

    /**
     * 短信
     */
    public static final String[] PERMISSION_SMS = { Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS,
                                            Manifest.permission.BROADCAST_WAP_PUSH, Manifest.permission.RECEIVE_MMS };

    /**
     * 存储空间
     */
    public static final String[] PERMISSION_STORAGE = { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    /**
     * 申请单个权限
     * @param activity
     * @param permissions
     */
    public static void verifySinglePermission(Activity activity, String[] permissions){
        if(ActivityCompat.checkSelfPermission(activity, permissions[0]) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
        }
    }

    /**
     * 申请多个权限
     * @param activity
     * @param permissionList
     */
    public static void verifyPermissions(Activity activity, List<String> permissionList){
        String[] permissions = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
    }

    /**
     * 将权限组添加到list中
     * @param permissionList
     * @param permissions
     */
    public static void addPermissionsToList(List<String> permissionList, String[] permissions){
        for(int i=0;i<permissions.length;i++){
            permissionList.add(permissions[i]);
        }
    }

}
