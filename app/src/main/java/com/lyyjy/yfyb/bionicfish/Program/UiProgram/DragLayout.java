package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * Created by Administrator on 2017/2/9.
 */

public class DragLayout extends FrameLayout {
    private static final String TAG = DragLayout.class.getSimpleName();

    private Context mContext;
    private ViewDragHelper mViewDragHelper;

    private LayoutCreateView mLayoutCreateView;      //用于创建新控件的视图
    private LayoutDisplayView mLayoutDisplayView;   //用户放置控件的视图
    private ProgramBlock mNewBlock = null;    //新控件块


    public void setLayoutCreateView(LayoutCreateView view) {
        mLayoutCreateView = view;
    }

    public void setLayoutDisplayView(LayoutDisplayView view) {
        mLayoutDisplayView = view;
    }

    public DragLayout(Context context) {
        super(context);
        init(context);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mViewDragHelper = ViewDragHelper.create(this, 1, mCallback);
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mLayoutDisplayView.contain(child);
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);

            ((ProgramView) capturedChild).getProgramBlock().changeState(ProgramBlock.State.CAPTURED);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth() - child.getWidth() - leftBound;

            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topBound = getPaddingTop();
//            final int bottomBound = getHeight() - child.getHeight() - topBound;
//            final int newTop = Math.min(Math.max(top, topBound), bottomBound);
            final int newTop = Math.max(top, topBound);

            return newTop;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            mLayoutDisplayView.onViewPositionChanged(changedView, dx, dy);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

            DragLayout.this.onViewReleased(releasedChild);

        }
    };

    private void onViewReleased(View releasedChild) {
        if (releasedChild.getLeft() >= mLayoutCreateView.getLeft() + mLayoutCreateView.getWidth()) {
            mLayoutDisplayView.onViewPositionChanged(releasedChild, 0, 0);
            mLayoutDisplayView.onViewReleased(releasedChild);

        }

        //删除程序块
        for (View view : ((ProgramView) releasedChild).getProgramBlock().getViews()) {
            removeView(view);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);

        //若按下且当前未捕捉任何控件
        if (event.getAction() == MotionEvent.ACTION_DOWN
                && !mViewDragHelper.isCapturedViewUnder((int) event.getX(), (int) event.getY())
                && mLayoutCreateView != null
                && mLayoutDisplayView != null) {
            mNewBlock = mLayoutCreateView.newBlock(mContext, new PointF(event.getX(), event.getY()));

            if (mNewBlock==null){
                mNewBlock = mLayoutDisplayView.newBlock(mContext, new PointF(event.getX(), event.getY()));
            }

            if (mNewBlock != null) {
                for (View view : mNewBlock.getViews()) {
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(view.getLeft(), view.getTop(), 0, 0);
                    addView(view, params);
                }

                mNewBlock.changeState(ProgramBlock.State.CAPTURED);
                return true;
            }

            //下放Touch事件
            return false;
        }

        //若存在新控件
        if (mNewBlock != null) {
            View view = mNewBlock.getViews().get(0);

            //若当前控件未绘制
            if (view.getWidth() == 0) {
                return true;
            }
            //否则捕捉该控件
            else {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    onViewReleased(view);
                    return true;
                }
                mViewDragHelper.captureChildView(view, event.getActionIndex());
                mNewBlock = null;
            }
        }

        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
