package com.example.lc.achievementapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.common.PersonalInfo;
import com.example.lc.achievementapp.data.LocalData;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.iv_setting_type_back)
    ImageView ivBack;
    @BindView(R.id.switch_setting_app_font)
    Switch switchFont;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        init();
    }

    private void init(){
        boolean on = Boolean.valueOf(LocalData.getPersonalData(this, PersonalInfo.TEXT_FONT));
        switchFont.setChecked(on);
        switchFont.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocalData.modifyPersonalData(SettingActivity.this, PersonalInfo.TEXT_FONT, String.valueOf(isChecked));
            }
        });
    }

    @OnClick({R.id.iv_setting_type_back})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_setting_type_back:
                finish();
                break;
        }
    }

}
