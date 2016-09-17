package com.lyyjy.yfyb.bionicfish.Background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2016/8/3.
 */
public class MainBackground extends View {

    private RectF mRectFScreen;
    private Bitmap mBitmapBackground;

    public MainBackground(Context context) {
        super(context);

        init();
    }

    void init(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        mRectFScreen=new RectF(0,0,screenWidth,screenHeight);

        BitmapLoader loadBitmap=new BitmapLoader(getResources(), R.mipmap.background_main,-1,300000,screenWidth,screenHeight);
        mBitmapBackground=loadBitmap.getBitmap();

//        float scale=(float)screenWidth/mBitmapBackground.getWidth();
//        mWaveControler =new WaveControler(mBitmapBackground,scale);
//        mWaveControler.setSourcePowerSize(mBitmapBackground.getHeight()/20,1000);
//
//        persistentDataManager=PersistentDataManager.getInstance(mContext);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmapBackground,null,mRectFScreen,null);
    }
}
