package com.example.lc.achievementapp.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lc.achievementapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NormalInputDialog extends Dialog {

    @BindView(R.id.tv_dialog_normal_input_title)
    TextView tvTitle;
    @BindView(R.id.et_dialog_normal_input_content)
    EditText etContent;
    @BindView(R.id.btn_dialog_normal_input_sure)
    Button btnSure;
    @BindView(R.id.btn_dialog_normal_input_cancel)
    Button btnCancel;

    private Context context;

    private OnSureBtnClickListener onSureBtnClickListener;
    private OnCancelBtnClickListener onCancelBtnClickListener;

    public NormalInputDialog(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public NormalInputDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    private void initView(){
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_normal_input, null);
        setContentView(view);
        ButterKnife.bind(this);
    }

    public void setTit(int title){
        tvTitle.setText(title);
    }

    public void setContent(String content){
        etContent.setText(content);
    }

    @OnClick({R.id.btn_dialog_normal_input_cancel, R.id.btn_dialog_normal_input_sure})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_dialog_normal_input_sure:
                if(onSureBtnClickListener != null){
                    onSureBtnClickListener.onSureBtnClick(this, etContent.getText().toString());
                }
                break;
            case R.id.btn_dialog_normal_input_cancel:
                if(onCancelBtnClickListener!=null){
                    onCancelBtnClickListener.onCancelBtnClick(this);
                }
                break;
        }
    }

    public interface OnSureBtnClickListener{
        void onSureBtnClick(NormalInputDialog dialog, String message);
    }

    public interface OnCancelBtnClickListener{
        void onCancelBtnClick(NormalInputDialog dialog);
    }

    public void setOnSureBtnClickListener(OnSureBtnClickListener onSureBtnClickListener) {
        this.onSureBtnClickListener = onSureBtnClickListener;
    }

    public void setOnCancelBtnClickListener(OnCancelBtnClickListener onCancelBtnClickListener) {
        this.onCancelBtnClickListener = onCancelBtnClickListener;
    }
}
