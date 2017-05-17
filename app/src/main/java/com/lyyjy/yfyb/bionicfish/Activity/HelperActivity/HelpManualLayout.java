package com.lyyjy.yfyb.bionicfish.Activity.HelperActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lyyjy.yfyb.bionicfish.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/17.
 */

@SuppressWarnings("DefaultFileTemplate")
@SuppressLint("ViewConstructor")
public class HelpManualLayout extends FrameLayout implements ViewPager.OnPageChangeListener {
    private final Context mContext;

    private ImageView[] mDots;
    private final int[] mIds;

    public HelpManualLayout(Context context, int[] viewIds) {
        super(context);
        mContext=context;

        mIds=viewIds;
        init();
    }

    private void init(){
        LayoutInflater inflater=LayoutInflater.from(mContext);

        inflater.inflate(R.layout.activity_help,this);

        initViews();
        initDots();
    }

    private void initViews(){
        List<View> views=new ArrayList<>();

        for (int id:mIds){
            views.add(new HelpView(mContext,id));
        }

        HelpAdapter helpAdapter=new HelpAdapter(views);
        ViewPager viewPager= (ViewPager) findViewById(R.id.vpHelp);
        viewPager.setAdapter(helpAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    private void initDots(){
        LinearLayout layout= (LinearLayout) findViewById(R.id.layoutItem);
        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        mDots = new ImageView[mIds.length];
        for (int i=0;i<mDots.length;i++){
            mDots[i]=new ImageView(mContext);
            layout.addView(mDots[i],layoutParams);
            mDots[i].setBackgroundResource(R.drawable.ic_action_unselected);
        }

        if (mDots.length>0){
            mDots[0].setBackgroundResource(R.drawable.ic_action_selected);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i=0;i<mDots.length;++i){
            if (position==i){
                mDots[i].setBackgroundResource(R.drawable.ic_action_selected);
            }else{
                mDots[i].setBackgroundResource(R.drawable.ic_action_unselected);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
