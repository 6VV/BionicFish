package com.lyyjy.yfyb.bionicfish.program.ui_program;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2017/2/13.
 */

@SuppressWarnings("DefaultFileTemplate")
public class ProgramFishMovementStartView extends ProgramView implements ProgramMoveInterface{
    private static boolean mIsAlreadyExist=false;

    public static boolean isAlreadyExist() {
        return mIsAlreadyExist;
    }

    public static void setAlreadyExist(boolean isAlreadyExist) {
        mIsAlreadyExist = isAlreadyExist;
    }

    public ProgramFishMovementStartView(Context context) {
        super(context);
        init(context);
    }

    public ProgramFishMovementStartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void parseText(String executeText) {

    }

    @Override
    public String executeText() {
        return getResources().getString(R.string.start_swim);
    }

    @Override
    public ProgramView clone(Context context) {
        return new ProgramFishMovementStartView(context);
    }

    private void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_program_fish_movement_start, this);
//        addView(view);

        mProgramViewBackground = (ProgramFishStartBackground) view.findViewById(R.id.custom_view);
        mProgramViewBackground.setBackgroundShader(ProgramViewBackground.SHADER_MOVEMENT);
    }
}
