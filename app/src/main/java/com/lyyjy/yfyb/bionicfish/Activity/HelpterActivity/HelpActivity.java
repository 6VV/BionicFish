package com.lyyjy.yfyb.bionicfish.Activity.HelpterActivity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.lyyjy.yfyb.bionicfish.Activity.ParentActivity;
import com.lyyjy.yfyb.bionicfish.R;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends ParentActivity implements ViewPager.OnPageChangeListener {
    private ImageView[] mDots;
    private int[] mIds = {R.id.helpId1, R.id.helpId2,R.id.helpId3,R.id.helpId4,R.id.helpId5,R.id.helpId6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        initViews();
        initDots();
    }

    private void initViews(){
//        LayoutInflater inflater=LayoutInflater.from(this);
        List<View> views=new ArrayList<>();

        ;
        views.add(new HelpView(this,R.mipmap.help_1));
        views.add(new HelpView(this,R.mipmap.help_2));
        views.add(new HelpView(this,R.mipmap.help_3));
        views.add(new HelpView(this,R.mipmap.help_4));
        views.add(new HelpView(this,R.mipmap.help_5));
        views.add(new HelpView(this,R.mipmap.help_6));

        HelpAdapter helpAdapter=new HelpAdapter(views);
        ViewPager viewPager= (ViewPager) findViewById(R.id.vpHelp);
        viewPager.setAdapter(helpAdapter);
        viewPager.addOnPageChangeListener(this);
    }

    private void initDots(){
        mDots = new ImageView[mIds.length];
        for (int i=0;i<mIds.length;i++){
            mDots[i]= (ImageView) findViewById(mIds[i]);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
            }break;
        }
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i=0;i<mIds.length;++i){
            if (position==i){
                mDots[i].setBackgroundResource(R.mipmap.point_selected);
            }else{
                mDots[i].setBackgroundResource(R.mipmap.point_unselected);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
