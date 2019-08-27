package com.example.lc.achievementapp.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.common.AchievementStatus;
import com.example.lc.achievementapp.common.PersonalInfo;
import com.example.lc.achievementapp.data.LocalData;
import com.example.lc.achievementapp.util.TimeUtil;
import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CompleteItemAdapter extends RecyclerView.Adapter {

    private Context context;
    private int layoutId;
    private List<Achievement> achievementList;

    private OnItemClickListener onItemClickListener = null;

    public CompleteItemAdapter(Context context, int layoutId, List<Achievement> achievementList) {
        this.context = context;
        this.layoutId = layoutId;
        this.achievementList = achievementList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CompleteItemViewHolder(LayoutInflater.from(context).inflate(layoutId, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        CompleteItemViewHolder holder = (CompleteItemViewHolder) viewHolder;

        Achievement item = achievementList.get(i);

        //设置时间线的首尾时间线端的显示与隐藏
        //holder.timeline.initLine(TimelineView.getTimeLineViewType(i, achievementList.size()));
        if(item.getStatus() == AchievementStatus.COMPLETED) {
            holder.timeline.setMarker(context.getDrawable(R.mipmap.ic_completed));
            holder.timeline.setStartLineColor(context.getResources().getColor(R.color.md_blue_500), TimelineView.getTimeLineViewType(i, achievementList.size()));
            holder.timeline.setEndLineColor(context.getResources().getColor(R.color.md_blue_500), TimelineView.getTimeLineViewType(i, achievementList.size()));
        }else {
            holder.timeline.setMarker(context.getDrawable(R.mipmap.ic_abandoned));
            holder.timeline.setStartLineColor(context.getResources().getColor(R.color.md_grey_500), TimelineView.getTimeLineViewType(i, achievementList.size()));
            holder.timeline.setEndLineColor(context.getResources().getColor(R.color.md_grey_500), TimelineView.getTimeLineViewType(i, achievementList.size()));
        }

        //设置时间
        String time = TimeUtil.parseTime(item.getEndDate(), "yyyy.MM.dd");
        holder.tvMonth.setText(time.substring(0,7));
        holder.tvDay.setText(time.substring(8));

        //设置标题及内容显示
        holder.tvTitle.setText(item.getTitle());
        if(TextUtils.isEmpty(item.getRemarks())) {
            holder.tvRemarks.setVisibility(View.GONE);
        }else {
            holder.tvRemarks.setVisibility(View.VISIBLE);
            holder.tvRemarks.setText(item.getRemarks());
        }

        holder.cvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(i);
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

    public class CompleteItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.timeline_item_complete)
        TimelineView timeline;
        @BindView(R.id.cv_item_complete)
        CardView cvItem;
        @BindView(R.id.tv_item_complete_time_month)
        TextView tvMonth;
        @BindView(R.id.tv_item_complete_time_day)
        TextView tvDay;
        @BindView(R.id.tv_item_complete_title)
        TextView tvTitle;
        @BindView(R.id.tv_item_complete_remarks)
        TextView tvRemarks;

        public CompleteItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
