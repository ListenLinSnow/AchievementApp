package com.example.lc.achievementapp.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.bean.Achievement;
import com.example.lc.achievementapp.util.TimeUtil;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OngoingItemAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Achievement> achievementList;

    private OnItemClickListener onItemClickListener;
    private OnItemAbandonClickListener onItemAbandonClickListener;
    private OnItemCompleteClickListener onItemCompleteClickListener;

    public OngoingItemAdapter(Context context, List<Achievement> achievementList) {
        this.context = context;
        this.achievementList = achievementList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OngoingItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_ongoing_rv, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Achievement achievement = achievementList.get(i);

        OngoingItemViewHolder holder = (OngoingItemViewHolder) viewHolder;
        holder.swipe.setSwipeEnable(true);
        //当完成某个成就的动画后，该界面的子项起始x点有可能是错的，故需要纠正为正常的起点
        holder.item.setTranslationX(0);

        holder.tvTitle.setText(achievement.getTitle());
        holder.tvStartDate.setText(TimeUtil.parseTime(achievement.getStartDate()));
        holder.cbSelected.setChecked(false);
        holder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(onItemCompleteClickListener != null){
                        //完成成就的子项退出动画
                        float curTranslationX = holder.item.getTranslationX();
                        float width = holder.item.getWidth();
                        Animator animatorX = ObjectAnimator.ofFloat(holder.item, "translationX", curTranslationX, width);
                        animatorX.setInterpolator(new AnticipateInterpolator());
                        animatorX.setDuration(500).start();

                        animatorX.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                onItemCompleteClickListener.onItemCompleteClick(i);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }
                }
            }
        });
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(i);
                }
            }
        });
        holder.tvAbandon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemAbandonClickListener != null){
                    //先关闭侧滑，将布局恢复至正常形态
                    holder.swipe.quickClose();
                    //弃坑成就的子项退出动画
                    float curTranslationX = holder.item.getTranslationX();
                    float width = holder.item.getWidth();
                    Animator animatorX = ObjectAnimator.ofFloat(holder.item, "translationX", curTranslationX, width);
                    animatorX.setInterpolator(new AnticipateInterpolator());
                    animatorX.setDuration(500).start();

                    animatorX.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            onItemAbandonClickListener.onItemAbandonClick(i);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return achievementList.size();
    }



    public class OngoingItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.sml_item_ongoing)
        SwipeMenuLayout swipe;
        @BindView(R.id.rl_item_ongoing)
        RelativeLayout item;
        @BindView(R.id.cb_item_ongoing_selected)
        CheckBox cbSelected;
        @BindView(R.id.tv_item_ongoing_title)
        TextView tvTitle;
        @BindView(R.id.tv_item_ongoing_start_date)
        TextView tvStartDate;
        @BindView(R.id.tv_item_ongoing_abandon)
        TextView tvAbandon;

        public OngoingItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnItemAbandonClickListener{
        void onItemAbandonClick(int position);
    }

    public interface OnItemCompleteClickListener{
        void onItemCompleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemAbandonClickListener(OnItemAbandonClickListener onItemAbandonClickListener) {
        this.onItemAbandonClickListener = onItemAbandonClickListener;
    }

    public void setOnItemCompleteClickListener(OnItemCompleteClickListener onItemCompleteClickListener) {
        this.onItemCompleteClickListener = onItemCompleteClickListener;
    }

}
