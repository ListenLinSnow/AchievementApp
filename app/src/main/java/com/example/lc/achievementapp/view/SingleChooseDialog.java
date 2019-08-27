package com.example.lc.achievementapp.view;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lc.achievementapp.R;
import com.example.lc.achievementapp.adapter.SingleChooseItemAdapter;
import com.example.lc.achievementapp.adapter.SlideItemAdapter;
import com.example.lc.achievementapp.bean.AchievementType;
import com.example.lc.achievementapp.bean.SingleChooseItem;
import com.example.lc.achievementapp.bean.SingleItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SingleChooseDialog extends Dialog {

    @BindView(R.id.tv_dialog_single_choose_title)
    TextView tvTitle;
    @BindView(R.id.rv_dialog_single_choose)
    RecyclerView rvSingleChoose;
    @BindView(R.id.btn_dialog_single_choose_sure)
    Button btnSure;
    @BindView(R.id.btn_dialog_single_choose_cancel)
    Button btnCancel;

    private Context context;

    int selectedPosition = 0;

    private OnSureBtnClickListener onSureBtnClickListener;
    private OnCancelBtnClickListener onCancelBtnClickListener;

    public SingleChooseDialog(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView(){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_single_choose, null);
        setContentView(view);
        ButterKnife.bind(this);
    }

    /**
     * 设置单选框标题
     * @param title
     */
    public void setTit(String title){
        tvTitle.setText(title);
    }

    /**
     * 设置单选框列表内容
     * @param itemList
     */
    public void setList(List<SingleChooseItem<AchievementType>> itemList){
        SingleChooseItemAdapter adapter = new SingleChooseItemAdapter(context, itemList);
        adapter.setOnItemClickListener(new SingleChooseItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectedPosition = position;
                adapter.notifyDataSetChanged();
            }
        });
        rvSingleChoose.setLayoutManager(new LinearLayoutManager(context));
        rvSingleChoose.setItemAnimator(new DefaultItemAnimator());
        rvSingleChoose.setAdapter(adapter);
    }

    @OnClick({R.id.btn_dialog_single_choose_sure, R.id.btn_dialog_single_choose_cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_dialog_single_choose_sure:
                if(onSureBtnClickListener != null){
                    onSureBtnClickListener.onSureBtnClick(selectedPosition);
                }
                break;
            case R.id.btn_dialog_single_choose_cancel:
                if(onCancelBtnClickListener != null){
                    onCancelBtnClickListener.onCancelBtnClick();
                }
                break;
        }
    }

    public interface OnSureBtnClickListener{
        void onSureBtnClick(int position);
    }

    public interface OnCancelBtnClickListener{
        void onCancelBtnClick();
    }

    public void setOnSureBtnClickListener(OnSureBtnClickListener onSureBtnClickListener) {
        this.onSureBtnClickListener = onSureBtnClickListener;
    }

    public void setOnCancelBtnClickListener(OnCancelBtnClickListener onCancelBtnClickListener) {
        this.onCancelBtnClickListener = onCancelBtnClickListener;
    }
}
