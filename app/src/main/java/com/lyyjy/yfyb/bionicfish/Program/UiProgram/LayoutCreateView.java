package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.lyyjy.yfyb.bionicfish.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/9.
 */

@SuppressWarnings("DefaultFileTemplate")
public class LayoutCreateView extends ScrollView {
    @SuppressWarnings("unused")
    private static final String TAG = LayoutCreateView.class.getSimpleName();

    private final ArrayList<ProgramView> mChildViews = new ArrayList<>();

    public LayoutCreateView(Context context) {
        super(context);
        init(context);
    }

    public LayoutCreateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        ProgramFishLightStartView.setAlreadyExist(false);
        ProgramFishMovementStartView.setAlreadyExist(false);

        View view = LayoutInflater.from(context).inflate(R.layout.layout_create_view, this);
//        addView(view);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.view_list);
        for (int i = 0; i < layout.getChildCount(); ++i) {
            ProgramView programView=(ProgramView) layout.getChildAt(i);
            mChildViews.add(programView);
            programView.setFocusable(false);
        }
    }

    public ProgramBlock newBlock(Context context, PointF pointF) {
        for (ProgramView view : mChildViews) {
            if (isViewTouched(view, pointF.x + getScrollX(), pointF.y + getScrollY())) {
                //若为开始块，且不可生成
                if ((view instanceof ProgramFishLightStartView && ProgramFishLightStartView.isAlreadyExist())
                        || (view instanceof ProgramFishMovementStartView && ProgramFishMovementStartView.isAlreadyExist())){
                    return null;
                }

                ProgramView newView = (view).clone(context);
                newView.setLeft(view.getLeft() - getScrollX());
                newView.setTop(view.getTop() - getScrollY());
                return new ProgramBlock(newView);
            }
        }

        return null;
    }

    private boolean isViewTouched(View view, float x, float y) {
        return (x >= view.getLeft() && x < view.getRight() &&
                y >= view.getTop() && y < view.getBottom());
    }

}
