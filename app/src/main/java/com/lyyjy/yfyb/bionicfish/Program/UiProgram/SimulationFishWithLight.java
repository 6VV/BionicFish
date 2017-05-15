package com.lyyjy.yfyb.bionicfish.Program.UiProgram;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lyyjy.yfyb.bionicfish.R;

/**
 * Created by Administrator on 2017/2/14.
 */

public class SimulationFishWithLight extends FrameLayout {
    private static final String TAG = SimulationFishWithLight.class.getSimpleName();

    public enum Movement{
        UP,
        LEFT,
        RIGHT,
        STOP,
    }

    public static final int COLOR_BLACK= Color.argb(255,0,0,0);
    public static final int COLOR_BLUE= Color.argb(255,0,0,255);
    public static final int COLOR_GREEN= Color.argb(255,0,255,0);
    public static final int COLOR_CYAN= Color.argb(255,0,255,255);
    public static final int COLOR_RED= Color.argb(255,255,0,0);
    public static final int COLOR_MAGENTA= Color.argb(255,255,0,255);
    public static final int COLOR_YELLOW= Color.argb(255,255,255,0);
    public static final int COLOR_WHITE= Color.argb(255,255,255,255);

    private static final float INIT_XY_OFFSET =10; //初始移动间隔
    private static final float INIT_ROTATION_OFFSET =2;    //初始旋转角度

    private Movement mMovement= Movement.STOP;   //当前方向
    private PointF mPosition=new PointF();  //当前位置
    private float mRotation=0;  //当前角度
    private float mXYOffset =INIT_XY_OFFSET;    //xy方向每次移动的间隔
    private float mRotationOffset =INIT_ROTATION_OFFSET;    //每次旋转的角度

    private int mLayoutWidth =0;    //所在布局宽度
    private int mLayoutHeight =0;   //所在布局高度

    private ImageView mSimulationFish;
    private FishLight mFishLight;

    public SimulationFishWithLight(Context context) {
        super(context);
        init(context);
    }

    public SimulationFishWithLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_fish_with_light,null);
        addView(view);

        mSimulationFish = (ImageView) view.findViewById(R.id.fish_view);
        mFishLight = (FishLight) view.findViewById(R.id.fish_light_view);

        //获取屏幕大小
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mLayoutWidth = dm.widthPixels;
        mLayoutHeight = dm.heightPixels;

        mRotation=getRotation();
    }

    public PointF nextPosition(){
        mPosition.x=getLeft();
        mPosition.y=getTop();

        switch (mMovement){
            case UP:{
                mPosition.x+= mXYOffset * Math.sin(mRotation* Math.PI/180);
                mPosition.y-= mXYOffset * Math.cos(mRotation* Math.PI/180);
            }break;
            case LEFT:{
                mRotation-= mRotationOffset;
                setRotation(mRotation);
                mPosition.x+= mXYOffset * Math.sin(mRotation* Math.PI/180);
                mPosition.y-= mXYOffset * Math.cos(mRotation* Math.PI/180);
            }break;
            case RIGHT:{
                mRotation+= mRotationOffset;
                setRotation(mRotation);
                mPosition.x+= mXYOffset * Math.sin(mRotation* Math.PI/180);
                mPosition.y-= mXYOffset * Math.cos(mRotation* Math.PI/180);
            }break;
            default:break;
        }

        if (mPosition.x>= mLayoutWidth){
            mPosition.x=-this.getWidth();
        }
        else if (mPosition.x<-this.getWidth()){
            mPosition.x= mLayoutWidth;
        }

        if (mPosition.y>= mLayoutHeight){
            mPosition.y=-this.getHeight();
        }
        else if (mPosition.y<-this.getHeight()){
            mPosition.y= mLayoutHeight;
        }

        return mPosition;
    }

    public void setMovement(Movement movement){
        mMovement=movement;
    }

    public void setSpeed(int speed){
        mXYOffset=INIT_XY_OFFSET*speed;
        mRotationOffset=INIT_ROTATION_OFFSET*speed;
    }

    public void setLayoutHeight(int layoutHeight) {
        mLayoutHeight = layoutHeight;
    }

    public void setLightColor(int color){
        mFishLight.setColor(color);
    }

    public void start(){
        ((AnimationDrawable)mSimulationFish.getDrawable()).start();
    }

    public void stop(){
        ((AnimationDrawable)mSimulationFish.getDrawable()).stop();
    }

}
