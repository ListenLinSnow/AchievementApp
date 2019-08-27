package com.example.lc.achievementapp.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
import com.example.lc.achievementapp.bean.SingleChooseItem;
import com.example.lc.achievementapp.common.AchievementStatus;
import com.example.lc.achievementapp.data.LocalData;
import com.example.lc.achievementapp.util.TimeUtil;
import com.example.lc.achievementapp.view.SingleChooseDialog;
import com.example.lc.achievementapp.view.WarningDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AchievementDetailActivity extends AppCompatActivity {

    @BindView(R.id.iv_achievement_detail_back)
    ImageView ivBack;
    @BindView(R.id.tv_achievement_detail_category)
    TextView tvCategory;
    @BindView(R.id.iv_achievement_detail_delete)
    ImageView ivDelete;
    @BindView(R.id.iv_achievement_detail_done)
    ImageView ivDone;
    @BindView(R.id.tv_achievement_detail_start_date)
    TextView tvStartDate;
    @BindView(R.id.tv_achievement_detail_end_date)
    TextView tvEndDate;
    @BindView(R.id.tv_achievement_detail_separator_date)
    TextView tvSeparator;
    @BindView(R.id.et_achievement_detail_title)
    EditText etTitle;
    @BindView(R.id.et_achievement_detail_remark)
    EditText etRemark;

    String action = null;
    Achievement achievement = null;

    String startTime = "";
    String endTime = "";

    int selectedPosition = -1;

    List<AchievementType> typeList = null;
    List<SingleChooseItem<AchievementType>> itemList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        init();
    }

    private void init(){
        LocalData.initDBHelper(this);
        typeList = LocalData.getTypeListData();

        //添加选择的分类
        itemList = new ArrayList<>();
        for (int i=0;i<typeList.size();i++){
            itemList.add(new SingleChooseItem(false, typeList.get(i)));
        }

        action = getIntent().getStringExtra("action");
        if(action.equals("edit")){
            achievement = (Achievement) getIntent().getSerializableExtra("achievement");
            //设置类别内容
            int type = achievement.getType();
            for (int i=0;i<typeList.size();i++){
                if(type == typeList.get(i).getId()){
                    selectedPosition = i;
                    tvCategory.setText(typeList.get(i).getContent());
                    break;
                }
            }
            if(achievement.getStatus() != AchievementStatus.ONGOING){
                tvSeparator.setVisibility(View.VISIBLE);
                tvEndDate.setVisibility(View.VISIBLE);
                ivDelete.setVisibility(View.VISIBLE);
            }
            //设置起止日期
            startTime = TimeUtil.parseTime(achievement.getStartDate());
            tvStartDate.setText(startTime);
            //设置终止日期
            if(achievement.getEndDate() != 0) {
                endTime = TimeUtil.parseTime(achievement.getEndDate());
                tvEndDate.setText(endTime);
            }
            etTitle.setText(achievement.getTitle());
            etRemark.setText(achievement.getRemarks());
        }else {
            tvSeparator.setVisibility(View.GONE);
            tvEndDate.setVisibility(View.GONE);
            ivDelete.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.iv_achievement_detail_back, R.id.tv_achievement_detail_category, R.id.iv_achievement_detail_delete, R.id.iv_achievement_detail_done, R.id.tv_achievement_detail_start_date, R.id.tv_achievement_detail_end_date})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_achievement_detail_back:
                //返回
                back();
                break;
            case R.id.tv_achievement_detail_category:
                //类别 选择
                showChooseDialog();
                break;
            case R.id.iv_achievement_detail_delete:
                //删除此成就
                showDeleteDialog();
                break;
            case R.id.iv_achievement_detail_done:
                //添加新成就
                addNewAchievement();
                break;
            case R.id.tv_achievement_detail_start_date:
                //选择时间
                showDatePickerDialog(view);
                break;
            case R.id.tv_achievement_detail_end_date:
                //选择时间
                showDatePickerDialog(view);
                break;
        }
    }

    /**
     * 退出时的事件处理
     */
    private void back(){
        if(action.equals("edit")){
            //若成就类别、起始日期、终止日期、标题、备注均未发生改变，则不弹出警告窗
            if(achievement.getType() == ((AchievementType)itemList.get(selectedPosition).getT()).getId()
                    && achievement.getStartDate() == TimeUtil.stringToLong(startTime, "yyyy-MM-dd")
                    && achievement.getEndDate() == TimeUtil.stringToLong(endTime, "yyyy-MM-dd")
                    && achievement.getTitle().equals(etTitle.getText().toString())
                    && achievement.getRemarks().equals(etRemark.getText().toString())){
                finish();
            }else {
                WarningDialog dialog = new WarningDialog(this);
                dialog.setTit("警告");
                dialog.setMsg("内容已更改，是否保存？");
                dialog.setCancelInfo("否");
                dialog.setOnSureBtnClickListener(new WarningDialog.OnSureBtnClickListener() {
                    @Override
                    public void onSureBtnClick(WarningDialog dialog) {
                        updateAchievementData();
                        dialog.dismiss();
                    }
                });
                dialog.setOnCancelBtnClickListener(new WarningDialog.OnCancelBtnClickListener() {
                    @Override
                    public void onCancelBtnClick(WarningDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                });
                dialog.show();
            }
        }else {
            //如果未编写内容，直接退出；否则对用户作出提醒
            if(TextUtils.isEmpty(etTitle.getText().toString()) && TextUtils.isEmpty(etRemark.getText().toString())){
                finish();
            }else {
                WarningDialog dialog = new WarningDialog(this);
                dialog.setTit("警告");
                dialog.setMsg("您暂未保存该成就，确定放弃吗？");
                dialog.setOnSureBtnClickListener(new WarningDialog.OnSureBtnClickListener() {
                    @Override
                    public void onSureBtnClick(WarningDialog dialog) {
                        dialog.dismiss();
                        finish();
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
        }
    }

    /**
     * 选择时间
     */
    private void showDatePickerDialog(View view){
        TextView tv = (TextView) view;

        //获取并设置要显示的日期
        Calendar calendar = Calendar.getInstance();
        long date = 0;
        if(achievement != null) {
            if (view.getId() == R.id.tv_achievement_detail_start_date) {
                date = achievement.getStartDate();
            }
            if (view.getId() == R.id.tv_achievement_detail_end_date) {
                date = achievement.getEndDate();
            }
        }else {
            date = TimeUtil.getDateTime();
        }
        calendar.setTime(new Date(date));

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                if(view.getId() == R.id.tv_achievement_detail_start_date) {
                    startTime = String.format("%02d-%02d-%02d", year, (++month), dayOfMonth);
                    tv.setText(startTime);
                }
                if(view.getId() == R.id.tv_achievement_detail_end_date){
                    endTime = String.format("%02d-%02d-%02d", year, (++month), dayOfMonth);
                    tv.setText(endTime);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //设置最大选择日期，不超过当前日期
        dialog.getDatePicker().setMaxDate(TimeUtil.getDateTime());
        dialog.show();
    }

    /**
     * 显示单选选择框
     */
    private void showChooseDialog(){
        SingleChooseDialog dialog = new SingleChooseDialog(this);
        dialog.setTit("选择分类");
        dialog.setList(itemList);
        dialog.setOnSureBtnClickListener(new SingleChooseDialog.OnSureBtnClickListener() {
            @Override
            public void onSureBtnClick(int position) {
                selectedPosition = position;
                tvCategory.setText(((AchievementType)itemList.get(position).getT()).getContent());
                dialog.dismiss();
            }
        });
        dialog.setOnCancelBtnClickListener(new SingleChooseDialog.OnCancelBtnClickListener() {
            @Override
            public void onCancelBtnClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 添加新成就
     */
    private void addNewAchievement(){
        if(selectedPosition == -1){
            showToast("请选择类别");
        }else if(TextUtils.isEmpty(startTime)){
            showToast("请选择起始日期");
        }else if(TextUtils.isEmpty(endTime) && tvEndDate.getVisibility() == View.VISIBLE){
            showToast("请选择结束日期");
        }else if(TextUtils.isEmpty(etTitle.getText().toString())){
            showToast("请填写标题");
        }else {
            if(action.equals("new")) {
                //如果是新建的
                long startDateTime = TimeUtil.stringToLong(startTime, "yyyy-MM-dd");
                LocalData.initDBHelper(this);
                boolean res = LocalData.insertAchiData(startDateTime, 0, etTitle.getText().toString(), etRemark.getText().toString(), ((AchievementType)itemList.get(selectedPosition).getT()).getId(), AchievementStatus.ONGOING);
                if (res) {
                    EventBus.getDefault().post(new ListNotify(true, false));
                } else {
                    showToast("保存失败");
                }
                finish();
            }else if(action.equals("edit")){
                //如果是已存在的
                updateAchievementData();
            }
        }
    }

    /**
     * 更新成就内容
     */
    private void updateAchievementData(){
        LocalData.initDBHelper(this);
        long startDateTime = TimeUtil.stringToLong(startTime, "yyyy-MM-dd");
        long endDateTime = 0;
        //只有非 进行中 状态才可以更改终止日期
        if(achievement.getStatus() != AchievementStatus.ONGOING) {
             endDateTime = TimeUtil.stringToLong(endTime, "yyyy-MM-dd");
        }
        if(achievement.getStatus() != AchievementStatus.ONGOING && endDateTime < startDateTime){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("警告");
            builder.setMessage("结束日期不得早于起始日期！");
            builder.setPositiveButton("确定", null);
            builder.create().show();
        }else {
            boolean res = LocalData.updateAchiDataContent(achievement.getId(), startDateTime, endDateTime, typeList.get(selectedPosition).getId(), etTitle.getText().toString(), etRemark.getText().toString());
            if (res) {
                //showToast("更新成功");
                EventBus.getDefault().post(new ListNotify(true, false));
            } else {
                showToast("更新失败");
            }
            finish();
        }
    }

    /**
     * 删除dialog
     */
    private void showDeleteDialog(){
        WarningDialog dialog = new WarningDialog(this);
        dialog.setTit("警告");
        dialog.setMsg("确定要删除此条成就吗？");
        dialog.setOnSureBtnClickListener(new WarningDialog.OnSureBtnClickListener() {
            @Override
            public void onSureBtnClick(WarningDialog dialog) {
                //删除数据
                LocalData.initDBHelper(getApplicationContext());
                if(LocalData.deleteAchiData(achievement.getId())){
                    //showToast("删除成功");
                    EventBus.getDefault().post(new ListNotify(true, false));
                    dialog.dismiss();
                }else {
                    showToast("删除失败");
                    dialog.dismiss();
                }
                finish();
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

    /**
     * 最基本的提醒
     * @param message
     */
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            back();
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
}
