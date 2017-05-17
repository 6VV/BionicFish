package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2017/2/13.
 */

@SuppressWarnings("DefaultFileTemplate")
public abstract class ProgramViewBackground extends View {
    @SuppressWarnings("unused")
    private static final String TAG = ProgramFishBackground.class.getSimpleName();

    private static final Shader SHADER_NORMAL = new LinearGradient(0, 0, 40, 60, new int[]{
            Color.argb(150, 255, 100, 100), Color.argb(150, 100, 255, 100), Color.argb(150, 100, 100, 255)}, null,
            Shader.TileMode.REPEAT);
//    private static final Shader SHADER_UNABLE = new LinearGradient(0, 0, 40, 60, new int[]{
//            Color.argb(100, 50, 50, 50), Color.argb(100, 100, 100, 100), Color.argb(100, 150, 150, 150)}, null,
//            Shader.TileMode.REPEAT);
    public static final Shader SHADER_MOVEMENT=new LinearGradient(0, 0, 40, 60, new int[] {
            Color.BLUE, Color.argb(200,50,50,255), Color.argb(100,20,20,255)}, null,
            Shader.TileMode.REPEAT);

    private Paint mPaint;
    private Paint mStrokePaint; //外部框架画笔
    Path mPath;

    int mLeftWidth = 0;    //左边宽
    int mRaisedRadius = 0;    //凸起圆弧半径
    @SuppressWarnings("unused")
    int mSmoothRadius = 0;    //平滑过渡圆弧半径

    public int getRadius() {
        return mRaisedRadius;
    }

    public abstract Rect getTopRaisedArea();

    public abstract Rect getBottomConcaveArea();

    public ProgramViewBackground(Context context) {
        super(context);
        init();
        mLeftWidth = defaultLeftLineLength();
        mRaisedRadius = defaultRaisedRadius();
        mSmoothRadius = defaultSmoothRadius();
    }

    public ProgramViewBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mStrokePaint = new Paint();
        mPath = new Path();

        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(100);
        mPaint.setShader(SHADER_NORMAL);

        mStrokePaint.setColor(Color.BLUE);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(10);
    }

    protected abstract void updatePath();

    private boolean mIsFirstDraw = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIsFirstDraw) {
            updatePath();
            mIsFirstDraw = false;
        }

        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mPath, mStrokePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = 2 * (mLeftWidth + 2 * getRadius());
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = 4 * defaultRaisedRadius();
        }

        setMeasuredDimension(width, height);
    }

    public void setColor(int color) {
        mStrokePaint.setColor(color);
        invalidate();
    }

    @SuppressWarnings("SameParameterValue")
    public void setBackgroundShader(Shader shader) {
        mPaint.setShader(shader);
        invalidate();
    }

    //右边宽
    int rightLineLength() {
        return getWidth() - mLeftWidth - 2 * getRadius();
    }

    //默认左边宽
    int defaultLeftLineLength() {
        return (int) getResources().getDimension(R.dimen.program_block_fish_movement_top_left_line_width);
    }

    //圆弧半径
    int defaultRaisedRadius() {
        return (int) getResources().getDimension(R.dimen.program_block_fish_background_raised_radius);
    }

    int defaultSmoothRadius() {
        return (int) getResources().getDimension(R.dimen.program_block_fish_background_smooth_radius);
    }
}
