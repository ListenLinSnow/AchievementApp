package com.example.lc.achievementapp.fragment;

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

public class AbandonedFragment extends Fragment {

    @BindView(R.id.tv_fragment_abandoned_type_info)
    TextView tvInfo;
    @BindView(R.id.rv_fragment_abandoned)
    RecyclerView rvAbandoned;

    Context context;

    int lastType = Constant.ACHIEVEMENT_TYPE_ALL;

    private List<Achievement> achievementList = null;
    private CompleteItemAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_abandoned, container, false);
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
        context = getContext();

        achievementList = new ArrayList<>();
        refreshData(lastType);

        rvAbandoned.setLayoutManager(new LinearLayoutManager(context));
        rvAbandoned.setItemAnimator(new DefaultItemAnimator());
        adapter = new CompleteItemAdapter(getActivity(), R.layout.item_complete_card_view, achievementList);
        adapter.setOnItemClickListener(new CompleteItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), AchievementDetailActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("achievement", achievementList.get(position));
                startActivity(intent);
            }
        });
        rvAbandoned.setAdapter(adapter);
    }

    /**
     * 添加 已弃坑 的成就
     */
    private void refreshData(int type){
        achievementList.clear();
        LocalData.initDBHelper(context);
        //按完成日期降序查询
        List<Achievement> list = LocalData.getAchiData(type, DBHelper.END_DATE_COLUMN + " desc");
        if(list.size() > 0) {
            Achievement achievement = null;
            for (int i = 0; i < list.size(); i++) {
                achievement = list.get(i);
                if (achievement.getStatus() == AchievementStatus.ABANDONED) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListNotify listNotify){
        if(listNotify.isRefreshAchievementList()){
            refreshData(lastType);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).registerAbandonedTypeClickListener(new MainActivity.OnAbandonedTypeClickListener() {
                @Override
                public void onAbandonedTypeClick(int type) {
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
