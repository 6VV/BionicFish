package com.lyyjy.yfyb.bionicfish.Background;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.View;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2016/8/3.
 */
@SuppressWarnings("DefaultFileTemplate")
public class MainBackground extends View {

    private RectF mRectFScreen;
    private Bitmap mBitmapBackground;

    public MainBackground(Context context) {
        super(context);

        init();
    }

    private void init(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        mRectFScreen=new RectF(0,0,screenWidth,screenHeight);

        BitmapLoader loadBitmap=new BitmapLoader(getResources(), R.drawable.background_main,-1,300000,screenWidth,screenHeight);
        mBitmapBackground=loadBitmap.getBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmapBackground,null,mRectFScreen,null);
    }
}
