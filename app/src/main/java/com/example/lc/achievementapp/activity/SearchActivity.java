package com.example.lc.achievementapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.adapter.ShowAchiAdapter;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.ListNotify;
import com.example.lc.achievementapp.common.Constant;
import com.example.lc.achievementapp.data.DBHelper;
import com.example.lc.achievementapp.data.LocalData;
import com.mypopsy.widget.FloatingSearchView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.fsv_search)
    FloatingSearchView searchView;
    @BindView(R.id.tv_search_num)
    TextView tvNum;
    @BindView(R.id.rv_search)
    RecyclerView rvSearch;

    List<Achievement> showList = null;
    ShowAchiAdapter achiAdapter = null;

    List<Achievement> achievementList = null;
    List<AchievementType> typeList = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        init();
    }

    private void init(){
        LocalData.initDBHelper(this);
        achievementList = LocalData.getAchiData(Constant.ACHIEVEMENT_TYPE_ALL, DBHelper.ID_COLUMN + " asc");
        typeList = LocalData.getTypeListData();
        showList = new ArrayList<>();

        //设置适配器
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        rvSearch.setItemAnimator(new DefaultItemAnimator());
        achiAdapter = new ShowAchiAdapter(this, showList);
        achiAdapter.setOnItemClickListener(new ShowAchiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Achievement achievement) {
                Intent intent = new Intent(SearchActivity.this, AchievementDetailActivity.class);
                intent.putExtra("action", "edit");
                intent.putExtra("achievement", achievement);
                startActivity(intent);
            }
        });
        rvSearch.setAdapter(achiAdapter);

        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSearchAction(CharSequence text) {
                filterAchievement(text.toString());
                searchView.clearFocus();
            }
        });
    }

    /**
     * 筛选成就事件内容
     * @param keyWord
     */
    private void filterAchievement(String keyWord){
        showList.clear();
        Achievement achievement = null;
        for (int i=0;i<achievementList.size();i++){
            achievement = achievementList.get(i);
            //标题或备注包含搜索关键词
            if(achievement.getTitle().contains(keyWord) || achievement.getRemarks().contains(keyWord)){
                showList.add(achievement);
                continue;
            }
            //分类名称包含搜索关键词
            for (AchievementType type: typeList){
                if(type.getId() == achievement.getType() && type.getContent().contains(keyWord)){
                    showList.add(achievement);
                    break;
                }
            }
        }

        if(achiAdapter != null){
            achiAdapter.setKeyWord(keyWord);
            achiAdapter.notifyDataSetChanged();

            if(showList.size() == 0){
                tvNum.setText("未找到该关键词的相关条目");
            }else {
                tvNum.setText("共找到" + showList.size() + "条相关条目");
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ListNotify listNotify){
        if(listNotify.isRefreshAchievementList()){
            achievementList = LocalData.getAchiData(Constant.ACHIEVEMENT_TYPE_ALL, DBHelper.ID_COLUMN + " asc");
            filterAchievement(searchView.getText().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
