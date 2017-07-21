package com.lyyjy.yfyb.bionicfish.program.ui_program;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2017/2/14.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ProgramFishWaitView extends ProgramView implements ProgramMoveInterface {

    private EditText mTimeEditText;

    public ProgramFishWaitView(Context context) {
        super(context);
        init(context);
    }


    public ProgramFishWaitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void parseText(String executeText) {
        String[] texts = executeText.split(" ");
        mTimeEditText.setText(texts[1]);
    }

    @Override
    public ProgramView clone(Context context) {
        ProgramFishWaitView view=new ProgramFishWaitView(context);
        view.mTimeEditText.setText(mTimeEditText.getText().toString());
        return view;
    }

    @Override
    public String executeText() {
        return getResources().getString(R.string.wait)+" "+mTimeEditText.getText().toString();
    }

    private void init(Context context) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_program_fish_wait,this);
//        addView(view);

        mProgramViewBackground = (ProgramFishBackground) view.findViewById(R.id.custom_view);
        mProgramViewBackground.setBackgroundShader(ProgramViewBackground.SHADER_MOVEMENT);

        mTimeEditText= (EditText) view.findViewById(R.id.time_edit_text);
        mTimeEditText.addTextChangedListener(new TimeLengthTextWatcher(mTimeEditText));

        LinearLayout layout= (LinearLayout) view.findViewById(R.id.widget_layout);
        for (int i=0;i<layout.getChildCount();++i){
            mChildViews.add(layout.getChildAt(i));
        }
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        mTimeEditText.setFocusable(false);
    }
}
