package com.lyyjy.yfyb.bionicfish.program.ui_program;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2017/2/13.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ProgramFishStartBackground extends ProgramViewBackground {
    @SuppressWarnings("unused")
    private static final String TAG= ProgramFishStartBackground.class.getSimpleName();

    @Override
    public Rect getTopRaisedArea() {
        return new Rect(-1,-1,-1,-1);
    }

    @Override
    public Rect getBottomConcaveArea() {
        return new Rect(mLeftWidth, getHeight(), mLeftWidth + 2 * getRadius(), getHeight() + getRadius());
    }

    public ProgramFishStartBackground(Context context) {
        super(context);
    }

    public ProgramFishStartBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    @Override
    protected void updatePath() {
        mPath.moveTo(0,0);
        mPath.lineTo(getWidth(),0);
        mPath.lineTo(getWidth(),getHeight());
        mPath.lineTo(mLeftWidth+2*getRadius(),getHeight());

        float controlRate = 0.552284749831f;
        int startX=mLeftWidth+2*getRadius();
        int startY=getHeight();
        mPath.cubicTo(startX, startY - controlRate * getRadius(),
                startX - getRadius() + controlRate * getRadius(), startY - getRadius(),
                startX - getRadius(), startY - getRadius());
        mPath.cubicTo(startX - getRadius() - controlRate * getRadius(), startY - getRadius(),
                startX - 2 * getRadius(), startY - controlRate * getRadius(),
                startX - 2 * getRadius(), startY);

        mPath.lineTo(0,getHeight());
        mPath.lineTo(0,0);
        mPath.close();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgramViewBackground);
        mLeftWidth = (int) typedArray.getDimension(R.styleable.ProgramViewBackground_left_width,
                defaultLeftLineLength());
        mRaisedRadius = (int) typedArray.getDimension(R.styleable.ProgramViewBackground_raised_radius,
                defaultRaisedRadius());
        mSmoothRadius = (int) typedArray.getDimension(R.styleable.ProgramViewBackground_smooth_radius,
                defaultSmoothRadius());

        typedArray.recycle();
    }
}
