package com.example.lc.achievementapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.SingleChooseItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleChooseItemAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<SingleChooseItem<AchievementType>> itemList;

    private OnItemClickListener onItemClickListener;

    public SingleChooseItemAdapter(Context context, List<SingleChooseItem<AchievementType>> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new SingleChooseItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item_single_choose, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        SingleChooseItemViewHolder holder = (SingleChooseItemViewHolder) viewHolder;

        SingleChooseItem item = itemList.get(i);
        AchievementType type = (AchievementType) item.getT();

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(i);
            }
        });
        Glide.with(context).load(type.getIcon()).into(holder.ivItem);
        holder.tvTitle.setText(type.getContent());
        if(item.isSelected()){
            holder.rbSelected.setChecked(true);
        }else {
            holder.rbSelected.setChecked(false);
        }
        holder.rbSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(i);
            }
        });
    }

    /**
     * 当选择某项时
     * @param position
     */
    private void onItemClick(int position){
        if(onItemClickListener != null){
            onItemClickListener.onItemClick(position);
            for (int j=0;j<itemList.size();j++){
                if(j==position){
                    itemList.get(j).setSelected(true);
                }else {
                    itemList.get(j).setSelected(false);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class SingleChooseItemViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.ll_item_single_choose)
        LinearLayout llItem;
        @BindView(R.id.iv_item_single_choose)
        ImageView ivItem;
        @BindView(R.id.tv_item_single_choose_title)
        TextView tvTitle;
        @BindView(R.id.rb_item_single_choose_selected)
        RadioButton rbSelected;

        public SingleChooseItemViewHolder(@NonNull View itemView) {
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
