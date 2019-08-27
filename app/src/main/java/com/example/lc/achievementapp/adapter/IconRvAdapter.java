package com.example.lc.achievementapp.adapter;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.lc.achievementapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IconRvAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<Integer> iconList;

    private OnItemClickListener onItemClickListener = null;

    public IconRvAdapter(Context context, List<Integer> iconList) {
        this.context = context;
        this.iconList = iconList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new IconRvViewHolder(LayoutInflater.from(context).inflate(R.layout.item_icon, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        IconRvViewHolder holder = (IconRvViewHolder) viewHolder;

        int imgId = iconList.get(i);
        Glide.with(context).load(imgId).into(holder.ivIcon);
        holder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null){
                    onItemClickListener.onItemClick(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return iconList.size();
    }

    public class IconRvViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iv_item_icon)
        ImageView ivIcon;

        public IconRvViewHolder(@NonNull View itemView) {
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
