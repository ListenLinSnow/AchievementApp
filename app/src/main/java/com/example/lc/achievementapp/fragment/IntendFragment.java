package com.example.lc.achievementapp.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.activity.AchievementDetailActivity;
import com.example.lc.achievementapp.activity.MainActivity;
import com.example.lc.achievementapp.adapter.IntendItemAdapter;
import com.example.lc.achievementapp.adapter.OngoingItemAdapter;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
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
import butterknife.OnClick;

public class IntendFragment extends Fragment {

    @BindView(R.id.tv_fragment_intend_type_info)
    TextView tvInfo;
    @BindView(R.id.rv_fragment_intend)
    RecyclerView rvIntend;
    @BindView(R.id.fab_fragment_intend)
    FloatingActionButton fabIntend;

    int lastType = Constant.ACHIEVEMENT_TYPE_ALL;       //最后一次查看的成就类型

    IntendItemAdapter adapter = null;

    List<Achievement> achievementList = null;
    List<AchievementType> typeList = null;

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
        View view = inflater.inflate(R.layout.fragment_intend, container, false);
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
        LocalData.initDBHelper(context.getApplicationContext());
        typeList = LocalData.getTypeListData();

        achievementList = new ArrayList<>();
        //默认获取所有类型的成就
        refreshData(lastType);
        adapter = new IntendItemAdapter(context, achievementList);
        adapter.setOnItemClickListener(new IntendItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(context, AchievementDetailActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("achievement", achievementList.get(position));
                startActivity(intent);
            }
        });
        adapter.setOnItemCompleteClickListener(new IntendItemAdapter.OnItemCompleteClickListener() {
            @Override
            public void onItemCompleteClick(int position) {
                //点击 复选框 表示提上日程，加入到计划中
                Achievement achievement = achievementList.get(position);
                LocalData.updateAchiDataStatus(achievement.getId(), TimeUtil.getDateTime(), AchievementStatus.ONGOING);
                EventBus.getDefault().post(new ListNotify(true, false));
                showToast("任务已加入到进行中");
            }
        });
        adapter.setOnItemAbandonClickListener(new IntendItemAdapter.OnItemDeleteClickListener() {
            @Override
            public void onItemAbandonClick(int position) {
                //删除 操作
                LocalData.initDBHelper(context.getApplicationContext());
                if (LocalData.deleteAchiData(achievementList.get(position).getId())){
                    EventBus.getDefault().post(new ListNotify(true, false));
                } else {
                    showToast("删除失败");
                }
            }
        });
        rvIntend.setLayoutManager(new LinearLayoutManager(context));
        rvIntend.setItemAnimator(new DefaultItemAnimator());
        rvIntend.setAdapter(adapter);
    }

    /**
     * 刷新数据
     */
    private void refreshData(int type){
        achievementList.clear();
        //添加 计划中 的成就
        LocalData.initDBHelper(context.getApplicationContext());
        List<Achievement> list = LocalData.getAchiData(type, DBHelper.START_DATE_COLUMN + " desc");
        if(list.size() > 0) {
            Achievement achievement = null;
            for (int i = 0; i < list.size(); i++) {
                achievement = list.get(i);
                if (achievement.getStatus() == AchievementStatus.INTEND) {
                    achievementList.add(achievement);
                }
            }
        }
        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }

        if(type == Constant.ACHIEVEMENT_TYPE_ALL) {
            tvInfo.setText("当前分类：所有集；条目数量：" + achievementList.size());
        }else {
            AchievementType achievementType = LocalData.getTypeById(type);
            tvInfo.setText("当前分类：" + achievementType.getContent() + "；条目数量：" + achievementList.size());
        }
    }

    @OnClick({R.id.fab_fragment_intend})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.fab_fragment_intend:
                //添加新成就
                Intent intent = new Intent(context, AchievementDetailActivity.class);
                intent.putExtra("action", "intend");
                startActivity(intent);
                break;
        }
    }

    private void showToast(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
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
            ((MainActivity) getActivity()).registerOnIntendTypeClickListener(new MainActivity.OnIntendTypeClickListener() {
                @Override
                public void onIntendTypeClick(int type) {
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
