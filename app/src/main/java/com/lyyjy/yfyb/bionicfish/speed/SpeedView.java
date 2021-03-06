package com.lyyjy.yfyb.bionicfish.speed;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/4/6.
 */
@SuppressWarnings({"DefaultFileTemplate", "FieldCanBeLocal"})
public class SpeedView extends View {

    private static final String TAG=SpeedView.class.getSimpleName();

    private Paint mWavePaint;
    private Paint mTextPaint;

    private final int mWaveColor=Color.RED;
    private final int mTextColor = Color.WHITE;

    private Handler mHandler;
    private boolean mStartWave = false;

    private long mCount = 0L;
    private float mAmplitude = 5.0F; // 振幅
    private final float mAngleInterval = 0.033F;

    private final int mAlpha = 150;// 透明度

    private float mTargetWaterLevel = 0.5F;// 水高(0~1)
    private float mCurrentWaterLevel=mTargetWaterLevel;

    private static final int REFRESH_TIME=60;  //刷新间隔
    private final float mWaterLevelIncrement=1f*REFRESH_TIME/SpeedManager.PRESS_MIN_TIME_TO_CHANGE/SpeedManager.SPEED_MAX_DIF;   //增量

    private final RectF mOval=new RectF();

    public SpeedView(Context context) {
        super(context);
        init();
    }

    public SpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mTextPaint = new Paint();
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(60);

        mWavePaint = new Paint();
        mWavePaint.setStrokeWidth(1.0F);
        mWavePaint.setColor(mWaveColor);
        mWavePaint.setAlpha(mAlpha);

        mHandler = new MyHandler(this);
    }

    private static class MyHandler extends Handler{

        private final WeakReference<SpeedView> mSpeedViewWeakReference;

        public MyHandler(SpeedView speedView){
            mSpeedViewWeakReference=new WeakReference<>(speedView);
        }

        @Override
        public void handleMessage(Message msg) {
            SpeedView speedView=mSpeedViewWeakReference.get();
            if (msg.what == 0) {
                speedView.invalidate();
                if (speedView.mStartWave) {
                    // 不断发消息给自己，使自己不断被重绘
                    sendEmptyMessageDelayed(0, SpeedView.REFRESH_TIME);
                }
            }
        }
    }


    public void setWaterLevel(float waterLevel){
        Log.d(TAG, "setWaterLevel: ");
        mTargetWaterLevel =waterLevel;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 得到控件的宽高
        int width = getWidth();
        int height = getHeight();

        mCurrentWaterLevel=getNextWaterLevel();

        //计算当前油量线和水平中线的距离
        float centerOffset = Math.abs(width * mCurrentWaterLevel - width / 2);
        //计算油量线和与水平中线的角度
        float horizonAngle = (float)(Math.asin(centerOffset / (width / 2)) * 180 / Math.PI);
        //扇形的起始角度和扫过角度
        float startAngle, sweepAngle;
        if (mCurrentWaterLevel > 0.5F) {
            startAngle = 360F - horizonAngle;
            sweepAngle = 180F + 2 * horizonAngle;
        } else {
            startAngle = horizonAngle;
            sweepAngle = 180F - 2 * horizonAngle;
        }

        int percent= (int) (mCurrentWaterLevel *100);
        String textPercent=String.valueOf(percent)+"%";
        float left = mTextPaint.measureText(textPercent);
        Paint.FontMetrics fm=mTextPaint.getFontMetrics();
        int fontHeight=(int)Math.ceil(-fm.descent-fm.ascent);
        canvas.drawText(textPercent, width/2 - left / 2,
                width/2+fontHeight/2, mTextPaint);

        // 绘制一个扇形
//        RectF oval = new RectF(0, 0, width, width );
        //noinspection SuspiciousNameCombination
        mOval.set(0,0,width,width);
        canvas.drawArc(mOval, startAngle, sweepAngle, false, mWavePaint);

        //如果未开始（未调用startWave方法）
        if ((!mStartWave) || (width == 0) || (height == 0)) {
//            // 绘制,即水面静止时的高度
//            RectF oval = new RectF(0, 0, width, width );
//            canvas.drawArc(oval, startAngle, sweepAngle, false, mWavePaint);
            return;
        }

        if (this.mCount >= 8388607L) {
            this.mCount = 0L;
        }
        // 每次onDraw时c都会自增
        mCount = (1L + mCount);
        mAmplitude=width>>6;
        float waterBellow = width * (1.0F - mCurrentWaterLevel) - mAmplitude;
        //当前油量线的长度
        float waveWidth = (float)Math.sqrt(width * width / 4 - centerOffset * centerOffset);
        //与圆半径的偏移量
        float offsetWidth = width / 2 - waveWidth;

        int top = (int) (waterBellow + mAmplitude);

        //起始振动X坐标，结束振动X坐标
        int startX, endX;
        if (mCurrentWaterLevel > 0.50F) {
            startX = (int) (offsetWidth);
            endX = (int) (width - offsetWidth);
        } else {
            startX = (int) (offsetWidth - mAmplitude);
            endX = (int) (width - offsetWidth + mAmplitude);
        }
        // 波浪效果
        while (startX < endX) {
            int startY = (int)
                    (waterBellow - mAmplitude * Math.sin(Math.PI * (2.0F * (startX + this.mCount * width * this.mAngleInterval)) / width));
            canvas.drawLine(startX, startY, startX, top, mWavePaint);
            startX++;
        }

        canvas.restore();
    }

    private float getNextWaterLevel(){
        float multiple=Math.abs(mTargetWaterLevel-mCurrentWaterLevel)*SpeedManager.SPEED_MAX_DIF*1f;
        multiple=multiple<1?1:multiple*multiple;
        float waterIncrement=mWaterLevelIncrement*multiple;

        float nextWaterLevel;
        if (mCurrentWaterLevel>mTargetWaterLevel){
            nextWaterLevel=mCurrentWaterLevel-waterIncrement;
            if (nextWaterLevel<mTargetWaterLevel){
                nextWaterLevel=mTargetWaterLevel;
            }
        }else if (mCurrentWaterLevel<mTargetWaterLevel){
            nextWaterLevel=mCurrentWaterLevel+waterIncrement;
            if (nextWaterLevel>mTargetWaterLevel){
                nextWaterLevel=mTargetWaterLevel;
            }
        }else{
            nextWaterLevel=mTargetWaterLevel;
        }
        return nextWaterLevel;
    }

    public void startWave() {
        Log.d(TAG, "startWave:start wave ");
        if (!mStartWave) {
            this.mCount = 0L;
            mStartWave = true;
            this.mHandler.sendEmptyMessage(0);
        }
    }

    public void stopWave() {
        if (mStartWave) {
            this.mCount = 0L;
            mStartWave = false;
            this.mHandler.removeMessages(0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.progress = (int) mCount;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mCount = ss.progress;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 关闭硬件加速，防止异常unsupported operation exception
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }


    /**
     *  保存状态
     */
    static class SavedState extends BaseSavedState {
        int progress;
        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            progress = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(progress);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
