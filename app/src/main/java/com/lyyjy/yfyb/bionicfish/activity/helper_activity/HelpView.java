package com.lyyjy.yfyb.bionicfish.activity.helper_activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by Administrator on 2016/8/5.
 */
@SuppressWarnings("DefaultFileTemplate")
@SuppressLint("ViewConstructor")
public class HelpView extends FrameLayout {

    public HelpView(Context context,int resource) {
        super(context);
        ImageView imageView=new ImageView(context);
        Glide.with(context).load(resource).into(imageView);
//        imageView.setImageResource(resource);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(imageView);
    }
}
