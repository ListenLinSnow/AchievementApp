package com.example.lc.achievementapp.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.lc.achievementapp.R;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WarningDialog extends Dialog {

    @BindView(R.id.tv_dialog_warning_title)
    TextView tvTitle;
    @BindView(R.id.tv_dialog_warning_message)
    TextView tvMessage;
    @BindView(R.id.btn_dialog_warning_sure)
    Button btnSure;
    @BindView(R.id.btn_dialog_warning_cancel)
    Button btnCancel;

    private Context context;

    private OnSureBtnClickListener onSureBtnClickListener = null;
    private OnCancelBtnClickListener onCancelBtnClickListener = null;

    public WarningDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView(){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_warning, null);
        setContentView(view);
        ButterKnife.bind(this);
    }

    /**
     * 设置标题
     */
    public void setTit(String title){
        tvTitle.setText(title);
    }

    /**
     * 设置警告内容
     * @param message
     */
    public void setMsg(String message){
        tvMessage.setText(message);
    }

    /**
     * 设置确定项显示内容
     * @param info
     */
    public void setSureInfo(String info){
        btnSure.setText(info);
    }

    /**
     * 设置取消项显示内容
     * @param info
     */
    public void setCancelInfo(String info){
        btnCancel.setText(info);
    }

    @OnClick({R.id.btn_dialog_warning_sure, R.id.btn_dialog_warning_cancel})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_dialog_warning_sure:
                if(onSureBtnClickListener != null){
                    onSureBtnClickListener.onSureBtnClick(this);
                }
                break;
            case R.id.btn_dialog_warning_cancel:
                if(onCancelBtnClickListener != null){
                    onCancelBtnClickListener.onCancelBtnClick(this);
                }
                break;
        }
    }

    public interface OnSureBtnClickListener{
        void onSureBtnClick(WarningDialog dialog);
    }

    public interface OnCancelBtnClickListener{
        void onCancelBtnClick(WarningDialog dialog);
    }

    public void setOnSureBtnClickListener(OnSureBtnClickListener onSureBtnClickListener) {
        this.onSureBtnClickListener = onSureBtnClickListener;
    }

    public void setOnCancelBtnClickListener(OnCancelBtnClickListener onCancelBtnClickListener) {
        this.onCancelBtnClickListener = onCancelBtnClickListener;
    }
}
