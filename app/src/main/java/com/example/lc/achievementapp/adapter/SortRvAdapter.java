package com.example.lc.achievementapp.adapter;

import android.content.Context;
import android.support.annotation.BinderThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.callback.ItemTouchHelperCallback;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SortRvAdapter extends RecyclerView.Adapter implements ItemTouchHelperCallback.ItemTouchHelperAdapter {

    private Context context;
    private List<AchievementType> typeList;

    private OnItemClickListener onItemClickListener = null;
    private OnDeleteClickListener onDeleteClickListener = null;

    public SortRvAdapter(Context context, List<AchievementType> typeList) {
        this.context = context;
        this.typeList = typeList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SortRvViewHolder(LayoutInflater.from(context).inflate(R.layout.item_manage_type, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        SortRvViewHolder holder = (SortRvViewHolder) viewHolder;

        AchievementType type = typeList.get(i);

        holder.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(i);
                }
            }
        });
        Glide.with(context).load(type.getIcon()).into(holder.ivIcon);
        holder.tvDesc.setText(type.getContent());
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDeleteClickListener != null){
                    onDeleteClickListener.onDeleteClick(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

    @Override
    public void onMove(int srcPosition, int targetPosition) {
        Collections.swap(typeList, srcPosition, targetPosition);
        notifyItemMoved(srcPosition, targetPosition);
    }

    @Override
    public void onSwipe(int position) {
        typeList.remove(position);
        notifyItemRemoved(position);
    }

    public class SortRvViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.rl_item_manage_type)
        RelativeLayout rlItem;
        @BindView(R.id.iv_item_manage_type_icon)
        ImageView ivIcon;
        @BindView(R.id.tv_item_manage_type_desc)
        TextView tvDesc;
        @BindView(R.id.iv_item_manage_type_delete)
        ImageView ivDelete;

        public SortRvViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnDeleteClickListener{
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }
}
