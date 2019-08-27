package com.example.lc.achievementapp.view;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.adapter.IconRvAdapter;
import com.example.lc.achievementapp.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TypeIconDialog extends Dialog {

    @BindView(R.id.rv_dialog_type_icon)
    RecyclerView rvIcon;

    private Context context;

    private List<Integer> iconList = null;
    private IconRvAdapter adapter = null;

    private OnItemClickListener onItemClickListener = null;

    public TypeIconDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView(){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_type_icon, null);
        setContentView(view);
        ButterKnife.bind(this);

        iconList = new ArrayList<>();
        iconList.add(R.mipmap.ic_read);
        iconList.add(R.mipmap.ic_video);
        iconList.add(R.mipmap.ic_game);
        iconList.add(R.mipmap.ic_code);
        iconList.add(R.mipmap.ic_more);

        rvIcon.setLayoutManager(new GridLayoutManager(context, 3));
        rvIcon.setItemAnimator(new DefaultItemAnimator());
        adapter = new IconRvAdapter(context, iconList);
        adapter.setOnItemClickListener(new IconRvAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(FileUtil.resourceIdToUri(context, iconList.get(position)).toString());
                }
            }
        });
        rvIcon.setAdapter(adapter);

    }

    public interface OnItemClickListener{
        void onItemClick(String icon);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
