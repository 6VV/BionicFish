package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2017/2/6.
 */

public class ProgramFishBackground extends ProgramViewBackground {

    @Override
    public Rect getTopRaisedArea() {
        return new Rect(mLeftWidth, 0, mLeftWidth + 2 * getRadius(), getRadius());
    }

    @Override
    public Rect getBottomConcaveArea() {
        return new Rect(mLeftWidth, getHeight(), mLeftWidth + 2 * getRadius(), getHeight() + getRadius());
    }

    public ProgramFishBackground(Context context) {
        super(context);
    }

    public ProgramFishBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    @Override
    protected void updatePath() {
        int startX = 0;
        int startY = getRadius();

        int topLeftX = 0;
        int topLeftY = getRadius();
        int bottomLeftX = 0;
        int bottomLeftY = getHeight();

        mPath.moveTo(startX, startY);

        //绘制左上边线
        mPath.lineTo(startX + mLeftWidth, startY);

        //绘制上端凸起圆弧
        startX = startX + mLeftWidth;
        RectF rectF = new RectF();

        rectF.set(startX, startY - getRadius(), startX + 2 * getRadius(), startY + getRadius());
        mPath.addArc(rectF, 180, 180);

        //绘制右上边线
        startX += 2 * getRadius();
        mPath.lineTo(startX + rightLineLength(), startY);

        //绘制右边线
        mPath.lineTo(bottomLeftX + getWidth(), bottomLeftY);


        //绘制右下边线
        startX = getWidth();
        startY = bottomLeftY;
        mPath.lineTo(startX - rightLineLength(), startY);

        //绘制下端凹陷圆弧，采用三阶贝塞尔曲线绘制
        startX = bottomLeftX + mLeftWidth + 2 * getRadius();
        float controlRate = 0.552284749831f;
        mPath.cubicTo(startX, startY - controlRate * getRadius(), startX - getRadius() + controlRate * getRadius(), startY - getRadius(),
                startX - getRadius(), startY - getRadius());
        mPath.cubicTo(startX - getRadius() - controlRate * getRadius(), startY - getRadius(), startX - 2 * getRadius(), startY - controlRate * getRadius(),
                startX - 2 * getRadius(), startY);

        //绘制左下边线
        mPath.lineTo(bottomLeftX, startY);

        //绘制左边线
        mPath.lineTo(topLeftX, topLeftY);

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
    }


}
