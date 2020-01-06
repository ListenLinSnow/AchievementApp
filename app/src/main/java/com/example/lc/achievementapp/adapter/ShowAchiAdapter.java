package com.example.lc.achievementapp.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.common.AchievementStatus;
import com.example.lc.achievementapp.util.TimeUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowAchiAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Achievement> achievementList;
    private String keyWord = null;

    private OnItemClickListener onItemClickListener = null;

    public ShowAchiAdapter(Context context, List<Achievement> achievementList) {
        this.context = context;
        this.achievementList = achievementList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ShowAchiViewHolder(LayoutInflater.from(context).inflate(R.layout.item_show_achi, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ShowAchiViewHolder holder = (ShowAchiViewHolder) viewHolder;

        Achievement achievement = achievementList.get(i);

        //存在关键字时对关键字进行醒目设置
        String title = achievement.getTitle();
        if(keyWord != null && title.contains(keyWord)){
            holder.tvTitle.setText(Html.fromHtml(title.replace(keyWord, "<font color='#ff0000'>" + keyWord + "</font>")));
        }else {
            holder.tvTitle.setText(achievement.getTitle());
        }

        if(TextUtils.isEmpty(achievement.getRemarks())) {
            holder.tvRemarks.setVisibility(View.GONE);
        }else {
            holder.tvRemarks.setVisibility(View.VISIBLE);

            String remarks = achievement.getRemarks();
            if(keyWord != null && remarks.contains(keyWord)){
                holder.tvRemarks.setText(Html.fromHtml(remarks.replace(keyWord, "<font color='#ff0000'>" + keyWord + "</font>")));
            }else {
                holder.tvRemarks.setText(achievement.getRemarks());
            }
        }

        if(achievement.getStatus() == AchievementStatus.ONGOING || achievement.getStatus() == AchievementStatus.INTEND) {
            holder.tvTime.setText(TimeUtil.parseTime(achievement.getStartDate()));
        }else {
            holder.tvTime.setText(TimeUtil.parseTime(achievement.getEndDate()));
        }

        holder.cvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(achievement);
                }
            }
        });

        //设置出现时的动画
        Animator animatorX = ObjectAnimator.ofFloat(holder.cvItem, "scaleX", 0f, 1.2f, 1f);
        Animator animatorY = ObjectAnimator.ofFloat(holder.cvItem, "scaleY", 0f, 1.2f, 1f);
        animatorX.setDuration(500).start();
        animatorY.setDuration(500).start();
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }

    /**
     * 设置关键字
     * @param keyWord
     */
    public void setKeyWord(String keyWord){
        this.keyWord = keyWord;
    }

    public class ShowAchiViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.cv_item_show_achi)
        CardView cvItem;
        @BindView(R.id.tv_item_show_achi_title)
        TextView tvTitle;
        @BindView(R.id.tv_item_show_achi_remarks)
        TextView tvRemarks;
        @BindView(R.id.tv_item_show_achi_time)
        TextView tvTime;

        public ShowAchiViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(Achievement achievement);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
