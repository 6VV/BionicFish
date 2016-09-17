package com.lyyjy.yfyb.bionicfish.Activity.HelpterActivity;


import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/8/5.
 */
public class HelpView extends FrameLayout {

    public HelpView(Context context,int resource) {
        super(context);
        ImageView imageView=new ImageView(context);
        imageView.setImageResource(resource);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(imageView);

//        public HelpView(Context context,int resource){
//            super(context);
//            LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            layoutParams.setMargins(0,0,0,0);
//            setLayoutParams(layoutParams);
//            setImageResource(resource);
//        }
    }
}
