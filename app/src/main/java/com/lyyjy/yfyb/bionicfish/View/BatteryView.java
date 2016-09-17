package com.lyyjy.yfyb.bionicfish.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Administrator on 2016/5/3.
 */
public class BatteryView extends View {
    private float mPowerUpperLimit =220f;    //电池电量上限
    private float mPowerLowerLimit=43f;     //电池电量下限
    private float mCurrentPower = 220f;      //当前电池电量

    private float mHeadBodyWidthRate =1/6f;   //电池身与电池头部比例
    private float mHeightWidthRate =0.5f;    //电池身宽高比

    private int mBodyWidth =90;             //电池宽度
    private int mBodyHeight = (int) (mBodyWidth * mHeightWidthRate); //电池高度
    private int mHeadWidth= (int) (mBodyWidth * mHeadBodyWidthRate);  //电池头部宽度
    private int mHeadHeight=mHeadWidth; //电池头部高度
    private int mInsideMargin=mBodyWidth/18;        //电池内部间隔

    public BatteryView(Context context) {
        super(context);
        init(context);

    }

    private void init(final Context context) {
        post(new Runnable() {
            @Override
            public void run() {
                int height=((AppCompatActivity)context).getSupportActionBar().getHeight();
                resizeWidth(height);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画外框
        drawBorder(canvas);
        //画电量
        drawElectricQuantity(canvas);
        //画电池头
        drawHead(canvas);
    }

    private void drawHead(Canvas canvas) {
        int left = getLeftPosition() + mBodyWidth;
        int top = getTopPosition() + mBodyHeight / 2 - mHeadHeight / 2;
        Rect rect3 = new Rect(left, top, left + mHeadWidth, top + mHeadHeight);
        canvas.drawRect(rect3, getFillPaint());
    }

    private void drawElectricQuantity(Canvas canvas) {
        float powerPercent = getPowerPercent();
        if(powerPercent > 0) {
            int left = getLeftPosition() + mInsideMargin;
            int top = getTopPosition() + mInsideMargin;
            int right = left + (int)((mBodyWidth - mInsideMargin*2) * powerPercent);
            int bottom = top + mBodyHeight - mInsideMargin * 2;
            Rect rect2 = new Rect(left, top, right , bottom);
            canvas.drawRect(rect2, getFillPaint());
        }
    }

    private void drawBorder(Canvas canvas) {
        Rect rect = new Rect(getLeftPosition(), getTopPosition(),
                getLeftPosition() + mBodyWidth, getTopPosition() + mBodyHeight);
        canvas.drawRect(rect, getBorderPaint());
    }

    private float getPowerPercent() {
        return (mCurrentPower-mPowerLowerLimit) / (mPowerUpperLimit-mPowerLowerLimit);
    }

    private Paint getFillPaint() {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    private Paint getBorderPaint() {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.STROKE);
        return paint;
    }

    private Paint getPaint() {
        Paint paint = new Paint();
        if ((mCurrentPower-mPowerLowerLimit)>(mPowerUpperLimit-mPowerLowerLimit)*0.3){
            paint.setColor(Color.argb(255,0x00,0xBB,0x00));
        }
        else {
            paint.setColor(Color.argb(255,0xFF,0x00,0x00));
        }
        paint.setAntiAlias(true);
        return paint;
    }

    public void setPower(int power){
        mCurrentPower=power;
        mCurrentPower=mCurrentPower>mPowerUpperLimit?mPowerUpperLimit:mCurrentPower;
        mCurrentPower=mCurrentPower<mPowerLowerLimit?mPowerLowerLimit:mCurrentPower;
        invalidate();
    }


    private int mTopMargin=0;
    private int mLeftMargin=0;
    public void resizeWidth(int width){
        float newWidth= (float) (width*0.6);
        float newHeight=newWidth*mHeightWidthRate;
        mTopMargin= (int) ((mBodyHeight-newHeight)/2);
        mLeftMargin= (int) ((mBodyWidth-newWidth)*(1+mHeadBodyWidthRate)/2);

        mBodyWidth = (int) newWidth;
        mBodyHeight = (int) (newHeight);
        mHeadWidth= (int) (mBodyWidth * mHeadBodyWidthRate);
        mHeadHeight=mHeadWidth;
        invalidate();
    }

    private int getTopPosition(){
        return getPaddingTop()+mTopMargin;
    }

    private int getLeftPosition(){
        return  getPaddingLeft()+mLeftMargin;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0, height = 0;
        int desiredWidth = getLeftPosition() + getPaddingRight() + mBodyWidth+mHeadWidth;
        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                width = Math.min(widthSize, desiredWidth);
                break;
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                width = desiredWidth;
                break;
        }
        int contentHeight = mBodyHeight;
        int desiredHeight = getTopPosition() + getPaddingBottom() + contentHeight;
        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(heightSize, desiredHeight);
                break;
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                height = contentHeight;
                break;
        }
        setMeasuredDimension(width, height);
    }


}
