package com.lyyjy.yfyb.bionicfish.Light;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

import com.lyyjy.yfyb.bionicfish.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/5/5.
 */
public class LightColorSpinner extends Spinner {
    public ArrayList<LightColor> mArrayList=new ArrayList<LightColor>(){};
    private Context mContext;

    public LightColorSpinner(Context context) {
        super(context);
        mContext=context;
        init();
    }

    public LightColorSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        init();
    }

    public LightColor getColor(int index){
        return mArrayList.get(index);
    }

    private void init() {
        for (int color :
                LightColor.COLOR_MAP.values()) {
            mArrayList.add(new LightColor(color));
        }

        LightColorAdapter lightColorAdapter=new LightColorAdapter(mContext, R.layout.adapter_light_color,mArrayList);
        this.setAdapter(lightColorAdapter);
    }
}
