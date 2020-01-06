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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.activity.AchievementDetailActivity;
import com.example.lc.achievementapp.activity.MainActivity;
import com.example.lc.achievementapp.adapter.OngoingItemAdapter;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
import com.example.lc.achievementapp.bean.SingleChooseItem;
import com.example.lc.achievementapp.common.AchievementStatus;
import com.example.lc.achievementapp.common.Constant;
import com.example.lc.achievementapp.data.DBHelper;
import com.example.lc.achievementapp.data.LocalData;
import com.example.lc.achievementapp.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OngoingFragment extends Fragment{

    @BindView(R.id.tv_fragment_ongoing_type_info)
    TextView tvInfo;                                            //类别内容及数量提示
    @BindView(R.id.rv_fragment_ongoing)
    RecyclerView rvOngoing;                                     //进行中 成就列表
    @BindView(R.id.fab_fragment_ongoing)
    FloatingActionButton fabCreate;                             //添加新进行中的成就

    int lastType = Constant.ACHIEVEMENT_TYPE_ALL;               //最后一次查看的成就类型，默认为所有类型

    OngoingItemAdapter adapter = null;

    List<Achievement> achievementList = null;
    List<AchievementType> typeList = null;
    List<SingleChooseItem<AchievementType>> itemList = null;

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
        View view = inflater.inflate(R.layout.fragment_ongoing, container, false);
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

        //添加分类列表
        itemList = new ArrayList<>();
        for (int i=0;i<typeList.size();i++){
            itemList.add(new SingleChooseItem<>(false, typeList.get(i)));
        }

        achievementList = new ArrayList<>();
        //默认获取所有类型的成就
        refreshData(lastType);
        adapter = new OngoingItemAdapter(context, achievementList);
        adapter.setOnItemClickListener(new OngoingItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(context, AchievementDetailActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("achievement", achievementList.get(position));
                startActivity(intent);
            }
        });
        adapter.setOnItemCompleteClickListener(new OngoingItemAdapter.OnItemCompleteClickListener() {
            @Override
            public void onItemCompleteClick(int position) {
                Achievement achievement = achievementList.get(position);
                LocalData.updateAchiDataStatus(achievement.getId(), TimeUtil.getDateTime(), AchievementStatus.COMPLETED);
                EventBus.getDefault().post(new ListNotify(true, false));
            }
        });
        adapter.setOnItemAbandonClickListener(new OngoingItemAdapter.OnItemAbandonClickListener() {
            @Override
            public void onItemAbandonClick(int position) {
                //弃坑操作
                LocalData.initDBHelper(context.getApplicationContext());
                if(LocalData.updateAchiDataStatus(achievementList.get(position).getId(), TimeUtil.getDateTime(), AchievementStatus.ABANDONED)) {
                    EventBus.getDefault().post(new ListNotify(true, false));
                    showToast("已弃坑");
                }else {
                    showToast("操作失败");
                }
            }
        });
        rvOngoing.setLayoutManager(new LinearLayoutManager(context));
        rvOngoing.setItemAnimator(new DefaultItemAnimator());
        rvOngoing.setAdapter(adapter);
    }

    /**
     * 刷新数据
     */
    private void refreshData(int type){
        achievementList.clear();
        //添加 进行中 的成就
        LocalData.initDBHelper(context.getApplicationContext());
        List<Achievement> list = LocalData.getAchiData(type, DBHelper.START_DATE_COLUMN + " desc");
        if(list.size() > 0) {
            Achievement achievement = null;
            for (int i = 0; i < list.size(); i++) {
                achievement = list.get(i);
                if (achievement.getStatus() == AchievementStatus.ONGOING) {
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

    @OnClick({R.id.fab_fragment_ongoing})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.fab_fragment_ongoing:
                //添加新成就
                Intent intent = new Intent(context, AchievementDetailActivity.class);
                intent.putExtra("action", "ongoing");
                startActivity(intent);
                break;
        }
    }

    private void showToast(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
            ((MainActivity) getActivity()).registerOngoingTypeClickListener(new MainActivity.OnOngoingTypeClickListener() {
                @Override
                public void onOngoingTypeClick(int type) {
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
