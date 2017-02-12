package com.example.incredibly.smarttodo.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class DivideDecor extends RecyclerView.ItemDecoration {

    private int width;
    private Paint paint;

    public DivideDecor(int width, int color) {
        super();
        this.width = width;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count = parent.getChildCount();
        int left = parent.getLeft();
        int right = parent.getRight();
        for (int i = 0; i < count; i++) {
            View child = parent.getChildAt(i);
            int bottom = child.getBottom();
            c.drawRect(left, bottom, right, bottom + width, paint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, width);
    }
}

