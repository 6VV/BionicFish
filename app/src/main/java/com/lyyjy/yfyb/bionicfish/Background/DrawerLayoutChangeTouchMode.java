package com.lyyjy.yfyb.bionicfish.Background;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/5/17.
 */

public class DrawerLayoutChangeTouchMode extends DrawerLayout {

    private boolean mIsTouchDown = false;

    public DrawerLayoutChangeTouchMode(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (mIsTouchDown) {
                return super.onTouchEvent(ev);
            } else {
                if (ev.getX() < dip2px(getContext(), 30)) {
                    mIsTouchDown = true;
                    return super.onTouchEvent(ev);
                } else if (ev.getX() > dip2px(getContext(), 300)) {
                    closeDrawers();
                    return false;
                } else {
                    return false;
                }
            }
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            mIsTouchDown = false;
            return super.onTouchEvent(ev);
        } else {
            return mIsTouchDown && super.onTouchEvent(ev);
        }
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
