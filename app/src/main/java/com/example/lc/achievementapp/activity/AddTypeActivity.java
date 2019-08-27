package com.example.lc.achievementapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
import com.example.lc.achievementapp.common.Constant;
import com.example.lc.achievementapp.data.LocalData;
import com.example.lc.achievementapp.util.FileUtil;
import com.example.lc.achievementapp.util.TimeUtil;
import com.example.lc.achievementapp.view.TypeIconDialog;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTypeActivity extends AppCompatActivity {

    @BindView(R.id.iv_add_type_back)
    ImageView ivBack;
    @BindView(R.id.iv_add_type_done)
    ImageView ivDone;
    @BindView(R.id.et_add_type_name)
    EditText etName;
    @BindView(R.id.ll_add_type_new_icon)
    LinearLayout llNewIcon;
    @BindView(R.id.iv_add_type_icon)
    ImageView ivIcon;

    int size = 0;

    String iconPath = null;
    String lastIcon = null;

    private static final int PICK_IMG = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_type);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        init();
    }

    private void init(){
        size = getIntent().getIntExtra("size", 0);

        iconPath = Constant.TYPE_PATH + File.separator + TimeUtil.getLocalTimeForFileName() + ".jpg";
    }

    @OnClick({R.id.iv_add_type_back, R.id.iv_add_type_done, R.id.ll_add_type_new_icon})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_add_type_back:
                if(new File(iconPath).exists()){
                    new File(iconPath).delete();
                }
                finish();
                break;
            case R.id.iv_add_type_done:
                saveNewType();
                break;
            case R.id.ll_add_type_new_icon:
                showSelectIconDialog();
                break;
        }
    }

    /**
     * 更改图标的dialog
     */
    private void showSelectIconDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_manage_operate, null);
        TextView tvApp = (TextView) view.findViewById(R.id.tv_dialog_update_type_first);
        TextView tvLocal = (TextView) view.findViewById(R.id.tv_dialog_update_type_second);
        tvApp.setText("选择APP内ICON");
        tvLocal.setText("选择本地图片");

        Dialog dialog = new Dialog(this);
        dialog.setTitle("分类操作");
        dialog.setContentView(view);
        //选择app的icon
        tvApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showAppIconDialog();
            }
        });
        //选择本地图片
        tvLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //设置图标名称
                iconPath = Constant.TYPE_PATH + File.separator + TimeUtil.getLocalTimeForFileName() + ".jpg";
                //挑选图标
                Matisse.from(AddTypeActivity.this)
                        .choose(MimeType.ofImage())
                        .showSingleMediaType(true)
                        .countable(true)
                        .maxSelectable(1)
                        .thumbnailScale(0.85f)
                        .imageEngine(new GlideEngine())
                        .forResult(PICK_IMG);
            }
        });
        dialog.show();
    }

    /**
     * 选择APP内icon
     */
    private void showAppIconDialog(){
        TypeIconDialog dialog = new TypeIconDialog(this);
        dialog.setOnItemClickListener(new TypeIconDialog.OnItemClickListener() {
            @Override
            public void onItemClick(String icon) {
                dialog.dismiss();
                lastIcon = icon;
                Glide.with(AddTypeActivity.this).load(icon).into(ivIcon);
            }
        });
        dialog.show();
    }

    /**
     * 保存新分类
     */
    private void saveNewType(){
        LocalData.initDBHelper(this);

        if(lastIcon != null) {
            LocalData.insertTypeData(lastIcon, etName.getText().toString(), size);
        }else {
            LocalData.insertTypeData(FileUtil.resourceIdToUri(this, R.mipmap.ic_list).toString(), etName.getText().toString(), size);
        }
        EventBus.getDefault().post(new ListNotify(false, true));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMG:
                if(resultCode == RESULT_OK && data != null){
                    List<Uri> list = Matisse.obtainResult(data);
                    if(list.size() != 0){
                        Uri sourceUri = list.get(0);
                        Uri destinationUri = Uri.fromFile(new File(iconPath));

                        UCrop.of(sourceUri, destinationUri)
                                .withAspectRatio(9, 9)
                                .withMaxResultSize(640, 640)
                                .start(this);
                    }
                }
                break;
            case UCrop.REQUEST_CROP:
                if(resultCode == RESULT_OK){
                    if(!new File(iconPath).exists()){
                        return;
                    }
                    lastIcon = Uri.fromFile(new File(iconPath)).toString();
                    Glide.with(this).load(iconPath).into(ivIcon);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(new File(iconPath).exists()){
                new File(iconPath).delete();
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListNotify listNotify){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 最基本的提醒
     * @param message
     */
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
