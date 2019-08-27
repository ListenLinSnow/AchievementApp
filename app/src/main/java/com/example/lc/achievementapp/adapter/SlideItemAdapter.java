package com.example.lc.achievementapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.SingleItem;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主页侧滑界面分类列表适配器
 */

public class SlideItemAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<AchievementType> typeList;

    OnItemClickListener onItemClickListener;

    public SlideItemAdapter(Context context, List<AchievementType> typeList) {
        this.context = context;
        this.typeList = typeList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SlideItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main_slide, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int i) {
        SlideItemViewHolder viewHolder = (SlideItemViewHolder) holder;

        AchievementType type = typeList.get(i);
        viewHolder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(i);
                }
            }
        });
        Glide.with(context).load(type.getIcon()).into(viewHolder.ivIcon);
        viewHolder.tvDesc.setText(type.getContent());
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    public class SlideItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.rl_item_main_slide)
        RelativeLayout rlItem;
        @BindView(R.id.iv_item_main_slide_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_item_main_slide_desc)
        TextView tvDesc;

        public SlideItemViewHolder(@NonNull View itemView) {
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
