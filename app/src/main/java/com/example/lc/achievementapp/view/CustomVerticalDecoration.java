package com.example.lc.achievementapp.view;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * create by lc on 2019/04/19
 * 自定义recyclerView分割线，主要是去掉最后一行分割线
 */

public class CustomVerticalDecoration extends RecyclerView.ItemDecoration {

    private Drawable divider;

    public CustomVerticalDecoration(Drawable divider) {
        this.divider = divider;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int dividerLeft = parent.getPaddingLeft();
        int dividerRight = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i <= childCount - 2; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int dividerTop = child.getBottom() + params.bottomMargin;
            int dividerBottom = dividerTop + divider.getIntrinsicHeight();

            divider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            divider.draw(canvas);
        }
    }

}
