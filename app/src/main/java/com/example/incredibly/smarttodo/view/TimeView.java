package com.example.incredibly.smarttodo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class TimeView extends View {

    private Paint mainPaint;
    private Paint shadePaint;
    private Paint textPaint;
    private float angle;
    private float density;
    private int strokeWidth=8;

    public TimeView(Context context) {
        this(context, null);
    }

    public TimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        density = getResources().getDisplayMetrics().density;
        mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mainPaint.setColor(0x55FFFFFF);
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setStrokeWidth(strokeWidth* density);
        shadePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadePaint.setColor(Color.WHITE);
        shadePaint.setStyle(Paint.Style.STROKE);
        shadePaint.setStrokeWidth(strokeWidth* density);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int plusWidth = (int) (strokeWidth*density);
        super.onMeasure(widthMeasureSpec+plusWidth, widthMeasureSpec+plusWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth()-getPaddingLeft()-getPaddingRight();
        int height = getHeight()-getPaddingTop()-getPaddingBottom();
        int x = getPaddingLeft()+width/2;
        int y = getPaddingTop()+height/2;
        int radius = width*7/16;
        canvas.drawCircle(x,y, radius, mainPaint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(x-radius, y-radius, x+radius, y+radius, 270, -360*angle,false, shadePaint);
        }
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
        postInvalidate();
    }

}
