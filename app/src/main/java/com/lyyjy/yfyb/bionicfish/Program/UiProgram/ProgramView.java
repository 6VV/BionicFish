package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/8.
 */

public abstract class ProgramView extends FrameLayout {
    private static final String TAG = ProgramView.class.getSimpleName();

    private static final int COLOR_INSERTED = Color.GREEN;   //被插入时的颜色
    private static final int COLOR_NORMAL = Color.BLUE;   //正常颜色
    private static final int COLOR_CAPTURED = Color.RED;  //被拖动时的颜色

    ProgramViewBackground mProgramViewBackground;
    ProgramBlock mProgramBlock =null;  //控件所在块

    protected ArrayList<View> mChildViews=new ArrayList<>();

    public abstract ProgramView clone(Context context);
    public abstract String executeText();

    public ProgramView(Context context) {
        super(context);
    }

    public ProgramView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void changeState(ProgramBlock.State state) {
        switch (state) {
            case INSERTED: {
                setColor(COLOR_INSERTED);
            }
            break;
            case CAPTURED: {
                setColor(COLOR_CAPTURED);
            }
            break;
            case NORMAL: {
                setColor(COLOR_NORMAL);
            }
            break;
            default:
                break;
        }
    }

    public ProgramBlock getProgramBlock() {
        return mProgramBlock;
    }

    public void setProgramBlock(ProgramBlock programBlock) {
        mProgramBlock = programBlock;
    }

    public Rect getTopRaisedArea() {
        return getActualArea(mProgramViewBackground.getTopRaisedArea());
    }

    public Rect getBottomConcaveArea() {
        return getActualArea(mProgramViewBackground.getBottomConcaveArea());
    }

    public int getRadius(){
        return mProgramViewBackground.getRadius();
    }

    public boolean isBoundingTouched(PointF pointF){
        if (!isViewTouched(mProgramViewBackground,pointF.x,pointF.y)){
            return false;
        }

        for (View view:mChildViews){
            if (isViewTouched(view,pointF.x,pointF.y)){
                return false;
            }
        }

        return true;
    }

    private boolean isViewTouched(View view, float x, float y) {
//        Log.e(TAG, String.valueOf(new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom())));
        return (x >= view.getLeft()+getLeft() && x < view.getRight()+getLeft() &&
                y >= view.getTop()+getTop() && y < view.getBottom()+getTop());
    }

    private void setColor(int color) {
        mProgramViewBackground.setColor(color);
    }

    protected void setSpinnerText(Spinner spinner, String text) {
        int count=spinner.getCount();
        for (int i=0;i<count;++i){
            if (spinner.getItemAtPosition(i).equals(text)){
                spinner.setSelection(i);
                break;
            }
        }
    }

    private Rect getActualArea(Rect area) {
        if (area.left<=0){
            return area;
        }
        area.left += getLeft();
        area.right += getLeft();
        area.top += getTop();
        area.bottom += getTop();
        return area;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 测量每一个child的宽和高
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        // 如果是warp_content情况下，记录宽和高
        int width = 0;
        int height = 0;

        int childCount = getChildCount();

        // 遍历每个子元素
        for (int i = 0; i < childCount; i++)
        {
            View child = getChildAt(i);

            // 得到child的lp
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            // 当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;

            width = Math.max(width, childWidth);
            height = Math.max(height, childHeight);

        }

        measureChild(mProgramViewBackground, MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
                : height);
    }

    public abstract void parseText(String executeText);
}
