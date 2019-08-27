package com.example.lc.achievementapp.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
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

        setFont();

        initBaseData();

        //标语动画
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(START_FIRST);
            }
        }, 0);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(START_SECOND);
            }
        }, 0);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.sendEmptyMessage(START_MAIN);
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
        tvDeadLine.setText(Html.fromHtml("距离年末还有<font color='#ff0000'><big><big><big><big>" + days + "</big></big></big></big></font>天"));

        //初始化添加部分分类
        LocalData.initDBHelper(this);
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
        if(!new File(Constant.TYPE_PATH).exists()){
            new File(Constant.TYPE_PATH).mkdirs();
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

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case START_FIRST:
                    startAnimation(tvFirstWord);
                    break;
                case START_SECOND:
                    startAnimation(tvSecondWord);
                    break;
                case START_MAIN:
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    break;
            }
        }
    };

}
