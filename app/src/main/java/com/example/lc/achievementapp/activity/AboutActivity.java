package com.example.lc.achievementapp.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.lc.achievementapp.R;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.tv_about_info)
    TextView tvInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        init();
    }

    private void init(){
        try {
            String info = "";

            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String version = packageInfo.versionName;

            info += "version:" + version + "\n";
            info += getResources().getString(R.string.copyright);

            tvInfo.setText(info);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
