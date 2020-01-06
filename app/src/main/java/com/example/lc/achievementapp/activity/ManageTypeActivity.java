package com.example.lc.achievementapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.adapter.SortRvAdapter;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
import com.example.lc.achievementapp.callback.ItemTouchHelperCallback;
import com.example.lc.achievementapp.common.Constant;
import com.example.lc.achievementapp.data.LocalData;
import com.example.lc.achievementapp.util.TimeUtil;
import com.example.lc.achievementapp.view.CustomVerticalDecoration;
import com.example.lc.achievementapp.view.NormalInputDialog;
import com.example.lc.achievementapp.view.TypeIconDialog;
import com.example.lc.achievementapp.view.WarningDialog;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ManageTypeActivity extends AppCompatActivity {

    @BindView(R.id.iv_manage_type_back)
    ImageView ivBack;
    @BindView(R.id.iv_manage_type_done)
    ImageView ivDone;
    @BindView(R.id.rv_manage_type_sort)
    RecyclerView rvSort;

    String iconPath = null;
    int selectedPosition = -1;

    List<AchievementType> typeList = null;
    private SortRvAdapter adapter = null;

    private static final int PICK_IMG = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_type);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        init();
    }

    private void init(){
        typeList = new ArrayList<>();

        rvSort.setLayoutManager(new LinearLayoutManager(this));
        rvSort.setItemAnimator(new DefaultItemAnimator());
        rvSort.addItemDecoration(new CustomVerticalDecoration(ContextCompat.getDrawable(this, R.drawable.item_divider)));

        refreshData();

        ItemTouchHelperCallback itemTouchHelperCallback = new ItemTouchHelperCallback(adapter);
        itemTouchHelperCallback.setDragEnable(true);
        itemTouchHelperCallback.setSwipeEnable(true);
        ItemTouchHelper helper = new ItemTouchHelper(itemTouchHelperCallback);
        helper.attachToRecyclerView(rvSort);
    }

    /**
     * 刷新数据
     */
    private void refreshData(){
        LocalData.initDBHelper(getApplicationContext());
        typeList.clear();
        typeList.addAll(LocalData.getTypeListData());

        if(adapter == null) {
            adapter = new SortRvAdapter(this, typeList);
            adapter.setOnItemClickListener(new SortRvAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    selectedPosition = position;
                    showManageOperateDialog();
                }
            });
            adapter.setOnDeleteClickListener(new SortRvAdapter.OnDeleteClickListener() {
                @Override
                public void onDeleteClick(int position) {
                    showWarningInfo(position);
                }
            });
            rvSort.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 展示管理操作的dialog
     */
    private void showManageOperateDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_manage_operate, null);
        TextView tvName = (TextView) view.findViewById(R.id.tv_dialog_update_type_first);
        TextView tvIcon = (TextView) view.findViewById(R.id.tv_dialog_update_type_second);
        tvName.setText("更改分类名称");
        tvIcon.setText("更改分类图标");

        Dialog dialog = new Dialog(this);
        dialog.setContentView(view);
        //更改名称
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                updateTypeName();
            }
        });
        //更改图标
        tvIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showSelectIconDialog();
            }
        });
        dialog.show();
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
                String typePath = getFilesDir() + File.separator + "type";
                iconPath = typePath + File.separator + TimeUtil.getLocalTimeForFileName() + ".jpg";
                //挑选图标
                Matisse.from(ManageTypeActivity.this)
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
                boolean res = LocalData.updateTypeIcon(typeList.get(selectedPosition).getId(), icon);
                if(res){
                    showToast("更新成功");
                    EventBus.getDefault().post(new ListNotify(false, true));
                }else {
                    showToast("更新失败");
                }
            }
        });
        dialog.show();
    }

    /**
     * 更改分类名称
     */
    private void updateTypeName(){
        NormalInputDialog dialog = new NormalInputDialog(this);
        dialog.setTit(R.string.update_type_name);
        dialog.setContent(typeList.get(selectedPosition).getContent());
        dialog.setOnSureBtnClickListener(new NormalInputDialog.OnSureBtnClickListener() {
            @Override
            public void onSureBtnClick(NormalInputDialog dialog, String message) {
                //先检测名称是否重复
                for (AchievementType type : typeList){
                    if(type.getContent().equals(message)){
                        showToast("该名称已存在");
                        return;
                    }
                }
                //更新名称
                boolean res = LocalData.updateTypeName(typeList.get(selectedPosition).getId(), message);
                if(res){
                    showToast("更新成功");
                    EventBus.getDefault().post(new ListNotify(false, true));
                }else {
                    showToast("更新失败");
                }
                dialog.dismiss();
            }
        });
        dialog.setOnCancelBtnClickListener(new NormalInputDialog.OnCancelBtnClickListener() {
            @Override
            public void onCancelBtnClick(NormalInputDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 删除时弹出警告信息
     */
    private void showWarningInfo(int position){
        WarningDialog dialog = new WarningDialog(this);
        dialog.setTit("警告");
        dialog.setMsg("该分类下的成就也会一并删除！确定删除当前分类吗？");
        dialog.setOnSureBtnClickListener(new WarningDialog.OnSureBtnClickListener() {
            @Override
            public void onSureBtnClick(WarningDialog dialog) {
                LocalData.initDBHelper(getApplicationContext());
                if(LocalData.deleteTypeData(typeList.get(position).getId())){
                    EventBus.getDefault().post(new ListNotify(true, true));
                    showToast("删除成功");
                }else {
                    showToast("删除失败");
                }
                dialog.dismiss();
            }
        });
        dialog.setOnCancelBtnClickListener(new WarningDialog.OnCancelBtnClickListener() {
            @Override
            public void onCancelBtnClick(WarningDialog dialog) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick({R.id.iv_manage_type_back, R.id.iv_manage_type_done})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_manage_type_back:
                finish();
                break;
            case R.id.iv_manage_type_done:
                saveTypeWeight();
                break;
        }
    }

    /**
     * 保存 类型 权重
     */
    private void saveTypeWeight(){
        LocalData.initDBHelper(getApplicationContext());
        for (int i = 0; i < typeList.size(); i++){
            AchievementType type = typeList.get(i);
            LocalData.updateTypeData(type.getId(), type.getIcon(), type.getContent(), i);
        }
        EventBus.getDefault().post(new ListNotify(false, true));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_IMG:
                if (resultCode == RESULT_OK && data != null){
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
                    boolean res = LocalData.updateTypeIcon(typeList.get(selectedPosition).getId(), Uri.fromFile(new File(iconPath)).toString());
                    if(res){
                        showToast("更新成功");
                        EventBus.getDefault().post(new ListNotify(false, true));
                    }else {
                        showToast("更新失败");
                    }
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListNotify listNotify){
        if(listNotify.isRefreshTypeList()){
            refreshData();
        }
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
