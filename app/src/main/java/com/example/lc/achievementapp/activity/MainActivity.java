package com.example.lc.achievementapp.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.adapter.SlideItemAdapter;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
import com.example.lc.achievementapp.common.PersonalInfo;
import com.example.lc.achievementapp.common.Constant;
import com.example.lc.achievementapp.data.LocalData;
import com.example.lc.achievementapp.fragment.AbandonedFragment;
import com.example.lc.achievementapp.fragment.CompletedFragment;
import com.example.lc.achievementapp.fragment.IntendFragment;
import com.example.lc.achievementapp.fragment.SummaryFragment;
import com.example.lc.achievementapp.fragment.OngoingFragment;
import com.example.lc.achievementapp.util.CheckPermissionUtil;
import com.example.lc.achievementapp.util.ExcelUtil;
import com.example.lc.achievementapp.util.FileUtil;
import com.example.lc.achievementapp.util.InternalDataConversion;
import com.example.lc.achievementapp.util.TimeUtil;
import com.example.lc.achievementapp.view.CircleImage;
import com.example.lc.achievementapp.view.NormalInputDialog;
import com.example.lc.achievementapp.view.WarningDialog;
import com.jaeger.library.StatusBarUtil;
import com.yalantis.ucrop.UCrop;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.example.lc.achievementapp.util.ExcelUtil.initExcel;
import static com.example.lc.achievementapp.util.ExcelUtil.readExcelData;
import static com.example.lc.achievementapp.util.ExcelUtil.writeObjListToExcel;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, SlideItemAdapter.OnItemClickListener,
        View.OnClickListener, Toolbar.OnMenuItemClickListener {

    @BindView(R.id.dl_main)
    DrawerLayout dlMain;                                    //整体布局
    @BindView(R.id.ll_main_slide)
    LinearLayout llMainSlide;                               //侧滑布局
    @BindView(R.id.tb_main)
    Toolbar toolbar;                                        //工具栏
    @BindView(R.id.fl_main)
    FrameLayout mainFrame;                                  //帧布局，置放其他fragment
    @BindView(R.id.bnv_main)
    BottomNavigationView bottomNavigationView;              //底部导航栏
    @BindView(R.id.rl_main_slide_cover)
    RelativeLayout rlCover;                                 //侧滑封面壁纸
    @BindView(R.id.iv_main_slide_avatar)
    ImageView ivAvatar;                                     //头像
    @BindView(R.id.tv_main_slide_username)
    TextView tvUsername;                                    //用户名
    @BindView(R.id.tv_main_slide_autograph)
    TextView tvAutograph;                                   //个性签名
    @BindView(R.id.rv_main_slide)
    RecyclerView rvSlide;                                   //侧边成就分类列表
    @BindView(R.id.rl_main_slide_all_type)
    RelativeLayout rlAllType;                               //查看成就(所有分类)
    @BindView(R.id.rl_main_slide_add_type)
    RelativeLayout rlAddType;                               //添加分类
    @BindView(R.id.rl_main_slide_manage_type)
    RelativeLayout rlManageType;                            //管理分类
    @BindView(R.id.rl_main_slide_export)
    RelativeLayout rlExport;                                //导出数据
    @BindView(R.id.rl_main_slide_import)
    RelativeLayout rlImport;                                //导入数据
    @BindView(R.id.rl_main_slide_setting)
    RelativeLayout rlSetting;                               //设置
    @BindView(R.id.rl_main_slide_about)
    RelativeLayout rlAbout;                                 //关于

    String coverPath = null, avatarPath = null;

    List<AchievementType> typeList = null;
    SlideItemAdapter adapter = null;

    OngoingFragment ongoingFragment = null;                 //进行中 成就
    IntendFragment intendFragment = null;                   //计划中 成就
    AbandonedFragment abandonedFragment = null;             //已弃坑 成就
    CompletedFragment completedFragment = null;             //已完成 成就
    SummaryFragment summaryFragment = null;                 //总结 成就

    List<Fragment> fragmentList = null;

    int lastIndex = -1;

    private OnOngoingTypeClickListener onOngoingTypeClickListener = null;
    private OnIntendTypeClickListener onIntendTypeClickListener = null;
    private OnAbandonedTypeClickListener onAbandonedTypeClickListener = null;
    private OnCompletedTypeClickListener onCompletedTypeClickListener = null;

    private static final int ONGOING = 0;
    private static final int INTEND = 1;
    private static final int ABANDONED = 2;
    private static final int COMPLETED = 3;
    private static final int SUMMARY = 4;

    private static final int PICK_COVER = 1;
    private static final int CROP_COVER = 2;
    private static final int PICK_AVATAR = 3;
    private static final int CROP_AVATAR = 4;
    private static final int PICK_EXCEL_FILE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        init();
    }

    private void init(){
        //先添加存储权限、拍照权限、电话权限
        List<String> permissionList = new ArrayList<>();
        CheckPermissionUtil.addPermissionsToList(permissionList, CheckPermissionUtil.PERMISSION_STORAGE);
        CheckPermissionUtil.addPermissionsToList(permissionList, CheckPermissionUtil.PERMISSION_CAMERA);
        CheckPermissionUtil.addPermissionsToList(permissionList, CheckPermissionUtil.PERMISSION_PHONE);
        CheckPermissionUtil.verifyPermissions(MainActivity.this, permissionList);

        //设置状态栏透明
        StatusBarUtil.setColorForDrawerLayout(this, dlMain, getResources().getColor(R.color.md_blue_500));

        coverPath = getFilesDir() + File.separator + "cover.jpg";
        avatarPath = getFilesDir() + File.separator + "avatar.jpg";

        //设置工具栏
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_drawer);
        toolbar.setTitle("所有集");
        toolbar.setTitleTextColor(Color.WHITE);

        //设置点击事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlMain.openDrawer(Gravity.START);
            }
        });
        toolbar.setOnMenuItemClickListener(this);

        //设置封面保存路径
        if (new File(coverPath).exists()){
            rlCover.setBackground(BitmapDrawable.createFromPath(coverPath));
        }

        //设置头像保存路径
        if(new File(avatarPath).exists()) {
            ivAvatar.setImageBitmap(CircleImage.toRoundBitmap(BitmapFactory.decodeFile(avatarPath)));
        }

        //设置用户名及个性签名
        String name = LocalData.getPersonalData(this, PersonalInfo.USERNAME);
        String autograph = LocalData.getPersonalData(this, PersonalInfo.AUTOGRAPH);
        if(!TextUtils.isEmpty(name)) {
            tvUsername.setText(name);
        }
        if(!TextUtils.isEmpty(autograph)){
            tvAutograph.setText(autograph);
        }

        //初始化成就分类List
        typeList = new ArrayList<>();
        refreshTypeList();

        ongoingFragment = new OngoingFragment();
        intendFragment = new IntendFragment();
        abandonedFragment = new AbandonedFragment();
        completedFragment = new CompletedFragment();
        summaryFragment = new SummaryFragment();

        //初始化并添加四个fragment
        fragmentList = new ArrayList<>();
        fragmentList.add(ongoingFragment);
        fragmentList.add(intendFragment);
        fragmentList.add(abandonedFragment);
        fragmentList.add(completedFragment);
        fragmentList.add(summaryFragment);
        lastIndex = 0;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_main, ongoingFragment)
                .show(ongoingFragment)
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    /**
     * 刷新类型列表
     */
    private void refreshTypeList(){
        LocalData.initDBHelper(getApplicationContext());
        typeList.clear();
        typeList.addAll(LocalData.getTypeListData());

        if(adapter == null) {
            adapter = new SlideItemAdapter(this, typeList);
            rvSlide.setLayoutManager(new LinearLayoutManager(this));
            adapter.setOnItemClickListener(this);
            rvSlide.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置 头像、用户名、签名、添加分类、管理分类 的点击事件
     * @param view
     */
    @OnClick({R.id.iv_main_slide_avatar, R.id.tv_main_slide_username, R.id.tv_main_slide_autograph, R.id.rl_main_slide_all_type,
            R.id.rl_main_slide_add_type, R.id.rl_main_slide_manage_type, R.id.rl_main_slide_export, R.id.rl_main_slide_import,
            R.id.rl_main_slide_cover, R.id.rl_main_slide_setting, R.id.rl_main_slide_about})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_main_slide_cover:
                //修改封面壁纸
                Matisse.from(this)
                        .choose(MimeType.ofImage())
                        .showSingleMediaType(true)
                        .countable(true)
                        .maxSelectable(1)
                        .thumbnailScale(0.8f)
                        .imageEngine(new GlideEngine())
                        .forResult(PICK_COVER);
                break;
            case R.id.iv_main_slide_avatar:
                //修改用户头像
                Matisse.from(this)
                        .choose(MimeType.ofImage())
                        .showSingleMediaType(true)
                        .countable(true)
                        .maxSelectable(1)
                        .thumbnailScale(0.8f)
                        .imageEngine(new GlideEngine())
                        .forResult(PICK_AVATAR);
                break;
            case R.id.tv_main_slide_username:
                //修改用户名
                showModifyDialog(PersonalInfo.USERNAME);
                break;
            case R.id.tv_main_slide_autograph:
                //修改签名
                showModifyDialog(PersonalInfo.AUTOGRAPH);
                break;
            case R.id.rl_main_slide_all_type:
                //查询所有分类的成就
                switch (lastIndex){
                    case ONGOING:
                        if(onOngoingTypeClickListener != null){
                            onOngoingTypeClickListener.onOngoingTypeClick(Constant.ACHIEVEMENT_TYPE_ALL);
                        }
                        break;
                    case INTEND:
                        if (onIntendTypeClickListener != null){
                            onIntendTypeClickListener.onIntendTypeClick(Constant.ACHIEVEMENT_TYPE_ALL);
                        }
                        break;
                    case ABANDONED:
                        if(onAbandonedTypeClickListener != null){
                            onAbandonedTypeClickListener.onAbandonedTypeClick(Constant.ACHIEVEMENT_TYPE_ALL);
                        }
                        break;
                    case COMPLETED:
                        if(onCompletedTypeClickListener != null){
                            onCompletedTypeClickListener.onCompletedTypeClickListener(Constant.ACHIEVEMENT_TYPE_ALL);
                        }
                        break;
                }
                dlMain.closeDrawers();
                break;
            case R.id.rl_main_slide_add_type:
                //添加新分类
                Intent intent = new Intent(MainActivity.this, AddTypeActivity.class);
                intent.putExtra("size", typeList.size());
                startActivity(intent);
                break;
            case R.id.rl_main_slide_manage_type:
                //管理所有分类
                startActivity(new Intent(MainActivity.this, ManageTypeActivity.class));
                break;
            case R.id.rl_main_slide_export:
                exportExcelData();
                break;
            case R.id.rl_main_slide_import:
                importExcelData();
                break;
            case R.id.rl_main_slide_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.rl_main_slide_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
        }
    }

    /**
     * 侧边栏适配器的点击事件
     * @param position
     */
    @Override
    public void onItemClick(int position) {
        AchievementType type = typeList.get(position);

        switch (lastIndex){
            case ONGOING:
                if(onOngoingTypeClickListener != null){
                    onOngoingTypeClickListener.onOngoingTypeClick(type.getId());
                }
                break;
            case INTEND:
                if (onIntendTypeClickListener != null){
                    onIntendTypeClickListener.onIntendTypeClick(type.getId());
                }
                break;
            case ABANDONED:
                if(onAbandonedTypeClickListener != null){
                    onAbandonedTypeClickListener.onAbandonedTypeClick(type.getId());
                }
                break;
            case COMPLETED:
                if(onCompletedTypeClickListener != null){
                    onCompletedTypeClickListener.onCompletedTypeClickListener(type.getId());
                }
                break;
        }

        dlMain.closeDrawers();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
        }
        return true;
    }

    /**
     * 要修改的信息
     * @param param
     */
    private void showModifyDialog(@PersonalInfo String param){
        NormalInputDialog dialog = new NormalInputDialog(this);

        //设置标题
        if(param.equals(PersonalInfo.USERNAME)){
            dialog.setTit(R.string.dialog_username_title);
            dialog.setContent(tvUsername.getText().toString());
        }
        if(param.equals(PersonalInfo.AUTOGRAPH)){
            dialog.setTit(R.string.dialog_autograph_title);
            dialog.setContent(tvAutograph.getText().toString());
        }

        dialog.setOnSureBtnClickListener(new NormalInputDialog.OnSureBtnClickListener() {
            @Override
            public void onSureBtnClick(NormalInputDialog dialog, String message) {
                boolean res = LocalData.modifyPersonalData(MainActivity.this, param, message);
                if(res){
                    showToast("保存成功");
                    if(param.equals(PersonalInfo.USERNAME)){
                        tvUsername.setText(message);
                    }
                    if(param.equals(PersonalInfo.AUTOGRAPH)){
                        tvAutograph.setText(message);
                    }
                }else {
                    showToast("保存失败");
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
     * 导出excel表格数据
     */
    private void exportExcelData(){
        LocalData.initDBHelper(getApplicationContext());
        List<Achievement> achievementList = LocalData.getAchiData(Constant.ACHIEVEMENT_TYPE_ALL, null);
        List<AchievementType> typeList = LocalData.getTypeListData();

        String filePath = Constant.APP_FOLDER_PATH + File.separator + "Achievement.xls";

        String[] achiTitles = {"序号", "状态", "起始日期", "完成日期", "类型", "标题", "备注"};
        String[] typeTitles = {"序号", "图标uri", "内容", "权重"};

        initExcel(filePath, achiTitles, typeTitles);
        writeObjListToExcel(achievementList, typeList, filePath, this);
    }

    /**
     * 导入excel表格数据
     */
    private void importExcelData(){
        WarningDialog dialog = new WarningDialog(this);
        dialog.setTit("提醒");
        dialog.setMsg("重复的类别不会再次导入。确定导入吗？");
        dialog.setSureInfo("确定");
        dialog.setCancelInfo("取消");
        dialog.setOnSureBtnClickListener(new WarningDialog.OnSureBtnClickListener() {
            @Override
            public void onSureBtnClick(WarningDialog dialog) {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(intent, PICK_EXCEL_FILE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case PICK_COVER:
                //获取挑选到的图片并启动裁剪程序
                if(resultCode == RESULT_OK && data != null){
                    int width = rlCover.getMeasuredWidth();
                    int height = rlCover.getMeasuredHeight();

                    List<Uri> list = Matisse.obtainResult(data);
                    if(list.size() != 0) {
                        Uri sourceUri = list.get(0);

                        Uri destinationUri = Uri.fromFile(new File(coverPath));
                        UCrop.of(sourceUri, destinationUri)
                                .withAspectRatio((float)width/(float)height, 1)
                                .withMaxResultSize(width, height)
                                .start(this, CROP_COVER);
                    }
                }
                break;
            case CROP_COVER:
                //保存裁剪后的图片并设置为封面
                if(resultCode == RESULT_OK){
                    if(!new File(coverPath).exists()){
                        return;
                    }
                    rlCover.setBackground(BitmapDrawable.createFromPath(coverPath));
                }
                break;
            case PICK_AVATAR:
                //获取挑选到的图片并启动裁剪程序
                if(resultCode == RESULT_OK && data != null){
                    List<Uri> list = Matisse.obtainResult(data);
                    if(list.size() != 0) {
                        Uri sourceUri = list.get(0);

                        Uri destinationUri = Uri.fromFile(new File(avatarPath));
                        UCrop.of(sourceUri, destinationUri)
                                .withAspectRatio(9, 9)
                                .withMaxResultSize(640, 640)
                                .start(this, CROP_AVATAR);
                    }
                }
                break;
            case CROP_AVATAR:
                //保存裁剪后的图片并设置为头像
                if(resultCode == RESULT_OK){
                    if(!new File(avatarPath).exists()){
                        return;
                    }
                    ivAvatar.setImageBitmap(CircleImage.toRoundBitmap(BitmapFactory.decodeFile(avatarPath)));
                }
                break;
            case PICK_EXCEL_FILE:
                if(resultCode == RESULT_OK && data != null){
                    Uri uri = data.getData();
                    String path = FileUtil.getFilePathByUri(MainActivity.this, uri);
                    ExcelUtil.readExcelData(this, path);
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_ongoing:
                if(lastIndex != ONGOING){
                    switchFragment(lastIndex, ONGOING);
                    lastIndex = ONGOING;
                    toolbar.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.action_intend:
                if (lastIndex != INTEND){
                    switchFragment(lastIndex, INTEND);
                    lastIndex = INTEND;
                    toolbar.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.action_abandoned:
                if(lastIndex != ABANDONED){
                    switchFragment(lastIndex, ABANDONED);
                    lastIndex = ABANDONED;
                    toolbar.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.action_completed:
                if(lastIndex != COMPLETED){
                    switchFragment(lastIndex, COMPLETED);
                    lastIndex = COMPLETED;
                    toolbar.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.action_summary:
                if(lastIndex != SUMMARY){
                    switchFragment(lastIndex, SUMMARY);
                    lastIndex = SUMMARY;
                    toolbar.setVisibility(View.GONE);
                }
                return true;
        }
        return false;
    }

    /**
     * 点击时切换fragment
     * @param lastIndex
     * @param index
     */
    public void switchFragment(int lastIndex, int index){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragmentList.get(lastIndex));
        if(!fragmentList.get(index).isAdded()){
            transaction.add(R.id.fl_main, fragmentList.get(index));
        }
        transaction.show(fragmentList.get(index)).commitAllowingStateLoss();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListNotify listNotify){
        if(listNotify.isRefreshTypeList()){
            refreshTypeList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //绑定搜索框
        MenuItem item = menu.findItem(R.id.action_search);

        return true;
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

    /**
     * 分类查询接口
     */
    public interface OnOngoingTypeClickListener{
        void onOngoingTypeClick(int type);
    }

    public interface OnIntendTypeClickListener{
        void onIntendTypeClick(int type);
    }

    public interface OnAbandonedTypeClickListener{
        void onAbandonedTypeClick(int type);
    }

    public interface OnCompletedTypeClickListener{
        void onCompletedTypeClickListener(int type);
    }

    public void registerOngoingTypeClickListener(OnOngoingTypeClickListener onOngoingTypeClickListener) {
        this.onOngoingTypeClickListener = onOngoingTypeClickListener;
    }

    public void registerOnIntendTypeClickListener(OnIntendTypeClickListener onIntendTypeClickListener){
        this.onIntendTypeClickListener = onIntendTypeClickListener;
    }

    public void registerAbandonedTypeClickListener(OnAbandonedTypeClickListener onAbandonedTypeClickListener) {
        this.onAbandonedTypeClickListener = onAbandonedTypeClickListener;
    }

    public void registerCompletedTypeClickListener(OnCompletedTypeClickListener onCompletedTypeClickListener) {
        this.onCompletedTypeClickListener = onCompletedTypeClickListener;
    }
}
