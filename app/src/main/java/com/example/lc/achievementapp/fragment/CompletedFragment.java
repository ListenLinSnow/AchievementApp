package com.example.lc.achievementapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.activity.AchievementDetailActivity;
import com.example.lc.achievementapp.activity.MainActivity;
import com.example.lc.achievementapp.adapter.CompleteItemAdapter;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
import com.example.lc.achievementapp.common.AchievementStatus;
import com.example.lc.achievementapp.common.Constant;
import com.example.lc.achievementapp.data.DBHelper;
import com.example.lc.achievementapp.data.LocalData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompletedFragment extends Fragment {

    @BindView(R.id.tv_fragment_completed_type_info)
    TextView tvInfo;
    @BindView(R.id.rv_fragment_complete)
    RecyclerView rvComplete;

    Context context;

    int lastType = Constant.ACHIEVEMENT_TYPE_ALL;

    private List<Achievement> achievementList = null;
    private CompleteItemAdapter adapter = null;

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
        View view = inflater.inflate(R.layout.fragment_completed, container, false);
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
        achievementList = new ArrayList<>();
        refreshData(lastType);

        rvComplete.setLayoutManager(new LinearLayoutManager(context));
        rvComplete.setItemAnimator(new DefaultItemAnimator());
        adapter = new CompleteItemAdapter(getActivity(), R.layout.item_complete_card_view, achievementList);
        adapter.setOnItemClickListener(new CompleteItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(context, AchievementDetailActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("achievement", achievementList.get(position));
                startActivity(intent);
            }
        });
        rvComplete.setAdapter(adapter);
    }

    /**
     * 添加 已完成 的成就
     */
    private void refreshData(int type){
        achievementList.clear();
        LocalData.initDBHelper(context.getApplicationContext());
        //按完成日期降序查询
        List<Achievement> list = LocalData.getAchiData(type, DBHelper.END_DATE_COLUMN + " desc");
        if(list.size() > 0) {
            Achievement achievement = null;
            for (int i = 0; i < list.size(); i++) {
                achievement = list.get(i);
                if (achievement.getStatus() == AchievementStatus.COMPLETED) {
                    achievementList.add(achievement);
                }
            }
        }
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }

        if(type == Constant.ACHIEVEMENT_TYPE_ALL) {
            tvInfo.setText("当前分类：所有集；条目数量：" + achievementList.size());
        }else {
            AchievementType achievementType = LocalData.getTypeById(type);
            tvInfo.setText("当前分类：" + achievementType.getContent() + "；条目数量：" + achievementList.size());
        }
    }

    /**
     * 接收到消息即更新界面
     * @param listNotify
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListNotify listNotify){
        if(listNotify.isRefreshAchievementList()){
            refreshData(lastType);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).registerCompletedTypeClickListener(new MainActivity.OnCompletedTypeClickListener() {
                @Override
                public void onCompletedTypeClickListener(int type) {
                    lastType = type;
                    refreshData(lastType);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
