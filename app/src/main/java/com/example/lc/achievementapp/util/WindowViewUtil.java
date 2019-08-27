package com.example.lc.achievementapp.util;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WindowViewUtil {

    /**
     * 获取某个视图的宽与高
     * @param view
     * @return
     */
    public static Map<String, Integer> getWidthAndHeight(View view){
        int height = view.getMeasuredHeight();
        int width = view.getMeasuredWidth();

        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put("height", height);
        map.put("width", width);
        return map;
    }

    /**
     * 获取整个界面的宽与高
     * @param context
     * @return
     */
    public static Map<String, Integer> getWidthAndHeight(Context context){
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put("width", display.getWidth());
        map.put("height", display.getHeight());
        return map;
    }

}
