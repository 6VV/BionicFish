package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/2/14.
 */

public class FishLight extends View {
    private static final String TAG=FishLight.class.getSimpleName();

    private int mRadius=5;  //半径
    private Paint mPaint=null;

    public FishLight(Context context) {
        super(context);
        init();
    }

    public FishLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mPaint=new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    public void setColor(int color){
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mRadius,mRadius,mRadius,mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize= MeasureSpec.getSize(widthMeasureSpec);
        int widthMode= MeasureSpec.getMode(widthMeasureSpec);
        int heightSize= MeasureSpec.getSize(heightMeasureSpec);
        int heightMode= MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode!= MeasureSpec.EXACTLY){
            widthSize= Math.min(mRadius*2,widthSize);
        }
        if (heightMode!= MeasureSpec.EXACTLY){
            heightSize= Math.min(mRadius*2,heightSize);
        }

        mRadius=widthSize/2;

        setMeasuredDimension(widthSize,heightSize);
    }
}
