package com.lyyjy.yfyb.bionicfish.Light;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lyyjy.yfyb.bionicfish.R;

import java.util.List;

/**
 * Created by Administrator on 2016/5/5.
 */
@SuppressWarnings("DefaultFileTemplate")
class LightColorAdapter extends ArrayAdapter<LightColor> {
    private final int mResource;

    @SuppressWarnings("SameParameterValue")
    public LightColorAdapter(Context context, int resource, List<LightColor> objects) {
        super(context, resource, objects);
        mResource=resource;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return showColor(position);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return showColor(position);
    }

    private View showColor(int position) {
        View view= LayoutInflater.from(getContext()).inflate(mResource, null);

        LightColor lightColor=getItem(position);
        TextView textView= (TextView) view.findViewById(R.id.tvLightColor);

        if (lightColor==null){
            textView.setBackgroundColor(Color.argb(255,0,0,0));
        }else {
            textView.setBackgroundColor(lightColor.getRgbColor());
        }

        textView.setText("");

        return view;
    }


}
