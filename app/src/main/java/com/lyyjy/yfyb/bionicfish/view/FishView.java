package com.lyyjy.yfyb.bionicfish.view;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;

import java.util.Random;

/**
 * Created by Administrator on 2016/3/27.
 */
@SuppressWarnings("DefaultFileTemplate")
public class FishView extends android.support.v7.widget.AppCompatImageView {
    private final float SPEED_RATIO =getResources().getDisplayMetrics().widthPixels/1920f;
    //基础速度
    private float baseSpeed(){
        return 10* SPEED_RATIO;
    }
    @SuppressWarnings("FieldCanBeLocal")
    private final int mSpeedUpTimes=5;           //加速倍数
    private float mSpeed= baseSpeed();       //当前速度

    //基础转角
    @SuppressWarnings("SameReturnValue")
    private float baseRotation(){
        return 2;
    }
    @SuppressWarnings("FieldCanBeLocal")
    private final int mRotationUpTimes=5;      //转角倍数
    private float mRotation=0;              //转角

    private int mScreenWidth;   //屏幕宽度
    private int mScreenHeight;  //屏幕高度

    private float mTargetLocationX=0; //目标地点
    private float mTargetLocationY=0;

    private long mLastSetLocationTime=0;    //上次设置目标地点的时间

    private float mScaleToSizeOfScreen;   //最大值相对于屏幕的比率

    public FishView(Context context) {
        super(context);
        Init();
    }

    private void Init(){
        //获取屏幕大小
        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        //设置控件属性
        this.setBackgroundColor(Color.TRANSPARENT);
        this.setScaleType(ScaleType.FIT_CENTER);
    }

    public void setTargetLocation(float x, float y){
        mLastSetLocationTime=System.currentTimeMillis();
        mTargetLocationX=x;
        mTargetLocationY=y;
    }

    private void getNewRotation(){
        if (mTargetLocationX<0.01 && mTargetLocationY<0.01){
            mSpeed= baseSpeed();
            mRotation=mRotation+ (new Random()).nextFloat()*(baseRotation() *2)- baseRotation();
        }
        else{
            //弧度
            double mRadian=mRotation*Math.PI/180;

            //鱼头部坐标
            float x=(float)(getX()+getWidth()/2+getHeight()/2*Math.sin(mRadian));
            float y=(float)(getY()+getHeight()/2*(1-Math.cos(mRadian)));

            //目标位置偏离角度
            float targetRotation=(float)(90-Math.atan2(y-mTargetLocationY,mTargetLocationX-x)*180/Math.PI);

            //调整速度
            getVelocity(x,y);

            //目标位置与当前位置的角度差值（角度）
            float diffRotation=mRotation-targetRotation;
            float increaseRotation=getIncreaseRotation(diffRotation);

            //若角度变化小于0.1且速度小于0.1
            if (increaseRotation<=0.2 && mSpeed<0.2){
                if (System.currentTimeMillis()-mLastSetLocationTime>1000){
                    mTargetLocationX=0;
                    mTargetLocationY=0;
                }
                increaseRotation=0;
                mSpeed=0;
            }
            mRotation+=increaseRotation;
        }

        if (mRotation>=360){
            mRotation-=360;
        }else if (mRotation<=0){
            mRotation+=360;
        }
    }


    private void getVelocity(float currentX,float currentY){
        double distance=Math.sqrt(Math.pow(mTargetLocationY-currentY,2)+Math.pow(mTargetLocationX-currentX,2));
        if (distance<50){
            mSpeed=0;
        }
        else{
            mSpeed=(float)(Math.sqrt(distance / mScreenWidth)* baseSpeed() *mSpeedUpTimes);
        }
    }

    private float getIncreaseRotation(float diffRotation){
        if (diffRotation<0){
            while (diffRotation<0){
                diffRotation+=360;
            }
        }
        else if (diffRotation>360){
            while (diffRotation>360){
                diffRotation-=360;
            }
        }

        float increaseRotation=0;
        //若差值大于180度，则增大当前角度
        if (diffRotation>180){
            diffRotation=360-diffRotation;
            //若差值大于5度
            if (diffRotation>5){
                increaseRotation=(diffRotation)/180* baseRotation() *mRotationUpTimes;
            }
        }
        else{
            if (diffRotation>5){
                increaseRotation=-1*(diffRotation)/180* baseRotation() *mRotationUpTimes;
            }
        }

        return increaseRotation;
    }

    public int[] getNextPosition(){
        getNewRotation();
        this.setRotation(mRotation);

        int nextX=(int)(getX()+mSpeed*Math.sin(mRotation*Math.PI/180));
        int nextY=(int)(getY()-mSpeed*Math.cos(mRotation*Math.PI/180));

        if (nextX>=mScreenWidth){
            nextX=-this.getWidth();
        }
        else if (nextX<-this.getWidth()){
            nextX=mScreenWidth;
        }

        if (nextY>=mScreenHeight){
            nextY=-this.getHeight();
        }
        else if (nextY<-this.getHeight()){
            nextY=mScreenHeight;
        }

        return new int[]{nextX,nextY};
    }

    public void setScaleToSizeOfScreen(float scale){
        mScaleToSizeOfScreen =scale;
    }

    public float getScaleToSizeOfScreen(){
        return mScaleToSizeOfScreen;
    }
}
