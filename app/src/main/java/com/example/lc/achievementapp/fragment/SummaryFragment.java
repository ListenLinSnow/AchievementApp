package com.example.lc.achievementapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
import com.example.lc.achievementapp.bean.TotalData;
import com.example.lc.achievementapp.common.AchievementStatus;
import com.example.lc.achievementapp.common.Constant;
import com.example.lc.achievementapp.data.DBHelper;
import com.example.lc.achievementapp.data.LocalData;
import com.example.lc.achievementapp.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 计划中
 */

public class SummaryFragment extends Fragment {

    @BindView(R.id.tv_summary_content)
    TextView tvContent;

    private String showInfo = "";

    private List<Achievement> allAchievementList = null;
    private List<Achievement> completedAchievementList = null;
    private List<Achievement> abandonedAchievementList = null;
    private List<Achievement> ongoingAchievementList = null;
    private List<Achievement> intendAchievementList = null;
    private List<AchievementType> typeList = null;
    private List<TotalData> totalDataList = null;

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = (Context) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init(){
        allAchievementList = new ArrayList<>();
        completedAchievementList = new ArrayList<>();
        abandonedAchievementList = new ArrayList<>();
        ongoingAchievementList = new ArrayList<>();
        intendAchievementList = new ArrayList<>();
        typeList = new ArrayList<>();
        totalDataList = new ArrayList<>();

        refreshData();
    }

    private void refreshData(){
        LocalData.initDBHelper(context.getApplicationContext());

        showInfo = "";
        allAchievementList.clear();
        completedAchievementList.clear();
        abandonedAchievementList.clear();
        typeList.clear();
        totalDataList.clear();

        //只统计当年年内结算的事情
        String startOfYear = TimeUtil.parseTime(System.currentTimeMillis(), "yyyy") + "-01-01";
        long day1 = TimeUtil.stringToLong(startOfYear, "yyyy-MM-dd");
        String endOfYear = TimeUtil.parseTime(System.currentTimeMillis(), "yyyy") + "-12-31";
        long day2 = TimeUtil.stringToLong(endOfYear, "yyyy-MM-dd");

        allAchievementList = LocalData.getAchiData(day1, day2);

        typeList = LocalData.getTypeListData();
        if(allAchievementList.size() > 0) {
            int shortestId = -1;
            int longestId = -1;
            long period = 0;
            long shortestTime = -1;
            long longestTime = -1;

            for (AchievementType type : typeList){
                totalDataList.add(new TotalData(type.getId(), type.getContent(), 0));
            }

            Achievement achievement = null;
            for (int i=0;i<allAchievementList.size();i++) {
                achievement = allAchievementList.get(i);
                if (achievement.getStatus() == AchievementStatus.COMPLETED) {
                    completedAchievementList.add(achievement);
                    //各类成就统计添加数据
                    for (int j=0;j<totalDataList.size();j++){
                        if(achievement.getType() == totalDataList.get(j).getType()){
                            totalDataList.get(j).setSum(totalDataList.get(j).getSum() + 1);
                            break;
                        }
                    }

                    //介于影视类（如电影）容易在一天内完成，故排除此类
                    if (LocalData.getTypeById(achievement.getType()).getContent().equals("影视"))
                        continue;
                    period = achievement.getEndDate() - achievement.getStartDate();
                    //最长时长及事件id
                    if(period >= longestTime) {
                        longestId = i;
                        longestTime = period;
                    }
                    //最短时长及事件id
                    if(shortestTime == -1){
                        shortestId = i;
                        shortestTime = period;
                    }else if(period <= shortestTime){
                        shortestId = i;
                        shortestTime = period;
                    }
                }
                if (achievement.getStatus() == AchievementStatus.ABANDONED) {
                    abandonedAchievementList.add(achievement);
                }
                if (achievement.getStatus() == AchievementStatus.ONGOING){
                    ongoingAchievementList.add(achievement);
                }
                if (achievement.getStatus() == AchievementStatus.INTEND){
                    intendAchievementList.add(achievement);
                }
            }

            //事件数量统计信息
            showInfo += "这一年，一共计划了" + formatTypeOrNumber(allAchievementList.size()) + "件事情<br>"
                    + "其中，完成了" + formatTypeOrNumber(completedAchievementList.size()) + "件事情<br>";

            if (abandonedAchievementList.size() != 0){
                showInfo += "弃坑了" + formatTypeOrNumber(abandonedAchievementList.size()) + "件事情<br>";
            }
            if (ongoingAchievementList.size() != 0){
                showInfo += "有" + formatTypeOrNumber(ongoingAchievementList.size()) + "件事情正在进行<br>";
            }
            if (intendAchievementList.size() != 0){
                showInfo += "有" + formatTypeOrNumber(intendAchievementList.size()) + "件事情还未开始<br>";
            }

            int index = 0;
            int maxSum = totalDataList.get(index).getSum();
            for (int i = index + 1;i<totalDataList.size();i++){
                if(maxSum < totalDataList.get(i).getSum()){
                    index = i;
                    maxSum = totalDataList.get(i).getSum();
                }
            }

            //最多成就类事件统计信息
            showInfo += "<br>进行了" + formatTypeOrNumber(typeList.size()) + "类事情<br>"
                        + "完成最多成就的是" + formatTypeOrNumber(totalDataList.get(index).getTypeContent()) + "类，共计" + formatTypeOrNumber(maxSum) + "件<br>其他如下：<br>";

            //添加其他类事件统计信息
            for (int i = 0;i<totalDataList.size();i++){
                if(i == index) continue;
                showInfo += formatTypeOrNumber(totalDataList.get(i).getTypeContent()) + "类：" + formatTypeOrNumber(totalDataList.get(i).getSum()) + "件<br>";
            }

            showInfo += "<br>";

            //存在完成的事件时，优先显示最长时长事件
            if(longestId != -1 && completedAchievementList.size() > 0) {
                showInfo += "完成时长最长的是" + formatTypeOrNumber(allAchievementList.get(longestId).getTitle()) + "，共计" + formatTypeOrNumber(longestTime / 1000 / 3600 / 24 + 1) + "天<br>";
            }
            //完成的事件数量大于1时，方显示最短时长事件，且需要统计是否存在同长时间事件
            if(shortestId != -1 && completedAchievementList.size() > 1) {
                //showInfo += "完成时长最短的是" + formatTypeOrNumber(allAchievementList.get(shortestId).getTitle()) + "，共计" + formatTypeOrNumber(shortestTime / 1000 / 3600 / 24 + 1) + "天";
                String shortestInfo = "";
                for (int i = 0; i < completedAchievementList.size(); i++){
                    achievement = completedAchievementList.get(i);

                    //介于影视类（如电影）容易在一天内完成，故排除此类
                    if (LocalData.getTypeById(achievement.getType()).getContent().equals("影视"))
                        continue;

                    period = achievement.getEndDate() - achievement.getStartDate();
                    if (period == shortestTime){
                        shortestInfo += formatTypeOrNumber(achievement.getTitle()) + "，<br>";
                    }
                }
                //如果首次出现与末次出现的位置不一致，则证明存在多个并存时长最短事件
                if (shortestInfo.indexOf("，") != shortestInfo.lastIndexOf(",")) {
                    showInfo += "完成时长最短的是" + shortestInfo + "各计" + formatTypeOrNumber(shortestTime / 1000 / 3600 / 24 + 1) + "天";
                } else {
                    showInfo += "完成时长最短的是" + shortestInfo + "总计" + formatTypeOrNumber(shortestTime / 1000 / 3600 / 24 + 1) + "天";
                }
            }
        }else {
            showInfo += "今年还未完成过任何成就";
        }

        tvContent.setText(Html.fromHtml(showInfo));
    }

    /**
     * 格式化字符串中的类别及数字为更醒目的其他颜色
     * @param object
     * @return
     */
    private String formatTypeOrNumber(Object object){
        return "<font color='#ff0000'><big><big>" + object.toString() + "</big></big></font>";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListNotify listNotify){
        if(listNotify.isRefreshAchievementList() || listNotify.isRefreshTypeList()){
            refreshData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
