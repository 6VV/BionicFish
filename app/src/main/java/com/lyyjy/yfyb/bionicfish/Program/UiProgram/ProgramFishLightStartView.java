package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2017/2/13.
 */

public class ProgramFishLightStartView extends ProgramView implements ProgramLightInterface {
    private static final String TAG=ProgramFishLightStartView.class.getSimpleName();

    private static boolean mIsAlreadyExist=false;

    public ProgramFishLightStartView(Context context) {
        super(context);
        init(context);
    }

    public ProgramFishLightStartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void parseText(String executeText) {

    }

    @Override
    public String executeText() {
        return getResources().getString(R.string.start_light);
    }

    public static void setAlreadyExist(boolean isAlreadyExist){
        mIsAlreadyExist=isAlreadyExist;
    }

    public static boolean isAlreadyExist(){
        return mIsAlreadyExist;
    }

    @Override
    public ProgramView clone(Context context) {
        return new ProgramFishLightStartView(context);
    }

    private void init(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.layout_program_fish_light_start, null);
        addView(view);

        mProgramViewBackground = (ProgramFishStartBackground) view.findViewById(R.id.custom_view);
    }
}
