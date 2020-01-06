package com.example.lc.achievementapp.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.common.Constant;
import com.example.lc.achievementapp.common.PersonalInfo;
import com.example.lc.achievementapp.data.LocalData;
import com.example.lc.achievementapp.util.FileUtil;
import com.example.lc.achievementapp.util.TimeUtil;
import com.example.lc.achievementapp.util.WindowViewUtil;
import com.jaeger.library.StatusBarUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.tv_splash_deadline)
    TextView tvDeadLine;
    @BindView(R.id.tv_splash_info_first_word)
    TextView tvFirstWord;
    @BindView(R.id.tv_splash_info_second_word)
    TextView tvSecondWord;

    private int duration = 1000;                        //动画时长

    private MyHandler myHandler;

    private static final int START_FIRST = 1;
    private static final int START_SECOND = 2;
    private static final int START_MAIN = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        init();
    }

    private void init(){
        //设置状态栏为透明
        /*View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setStatusBarColor(Color.TRANSPARENT);*/
        StatusBarUtil.setTransparent(this);

        myHandler = new MyHandler(this);

        setFont();

        initBaseData();

        //标语动画
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(START_FIRST);
            }
        }, 0);
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(START_SECOND);
            }
        }, 0);
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(START_MAIN);
            }
        }, duration * 2);
    }

    private void setFont(){
        boolean on = Boolean.valueOf(LocalData.getPersonalData(this, PersonalInfo.TEXT_FONT));
        Typeface typeface = null;
        if(on){
            typeface = Typeface.createFromAsset(getAssets(), "hanyi.ttf");
        }else {
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);
        }
        try {
            Field field = Typeface.class.getDeclaredField("MONOSPACE");
            field.setAccessible(true);
            field.set(null, typeface);
        }catch (Exception e){
            e.printStackTrace();
        }

        tvDeadLine.setTypeface(typeface);
        tvFirstWord.setTypeface(typeface);
        tvSecondWord.setTypeface(typeface);
    }

    /**
     * 初始化部分数据
     */
    private void initBaseData(){
        long day1 = TimeUtil.getDateTime();
        String endOfYear = TimeUtil.parseTime(System.currentTimeMillis(), "yyyy") + "-12-31";
        long day2 = TimeUtil.stringToLong(endOfYear, "yyyy-MM-dd");
        int days = TimeUtil.differToTwoDays(day1, day2);
        if (days > 0) {
            tvDeadLine.setText(Html.fromHtml("距离年末还有<font color='#ff0000'><big><big><big><big>" + days + "</big></big></big></big></font>天"));
        } else {
            tvDeadLine.setText(Html.fromHtml("今天已经是年末了！"));
        }

        //初始化添加部分分类
        LocalData.initDBHelper(getApplicationContext());
        if(LocalData.getTypeListData().size() == 0){
            LocalData.insertTypeData(FileUtil.resourceIdToUri(this, R.mipmap.ic_read).toString(), "阅读", 1);
            LocalData.insertTypeData(FileUtil.resourceIdToUri(this, R.mipmap.ic_video).toString(), "影视", 2);
            LocalData.insertTypeData(FileUtil.resourceIdToUri(this, R.mipmap.ic_game).toString(), "游戏", 3);
            LocalData.insertTypeData(FileUtil.resourceIdToUri(this, R.mipmap.ic_code).toString(), "代码", 4);
            LocalData.insertTypeData(FileUtil.resourceIdToUri(this, R.mipmap.ic_more).toString(), "其他", 5);
        }

        //初始化部分文件夹
        if(!new File(Constant.APP_FOLDER_PATH).exists()){
            new File(Constant.APP_FOLDER_PATH).mkdirs();
        }

        String typePath = getFilesDir() + File.separator + "type";
        if(!new File(typePath).exists()){
            new File(typePath).mkdirs();
        }
    }

    /**
     * 设置textView的动画
     * @param view
     */
    private void startAnimation(final View view){
        Animator animator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        animator.setDuration(duration * 2).start();
    }

    private void startMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private static class MyHandler extends Handler{

        private WeakReference<Context> reference;

        public MyHandler(Context context){
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = (SplashActivity) reference.get();
            if (activity != null){
                switch (msg.what){
                    case START_FIRST:
                        activity.startAnimation(activity.tvFirstWord);
                        break;
                    case START_SECOND:
                        activity.startAnimation(activity.tvSecondWord);
                        break;
                    case START_MAIN:
                        activity.startMainActivity();
                        break;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }

}
