package com.lyyjy.yfyb.bionicfish.Speed;

import android.util.Log;
import android.widget.ImageButton;

import com.lyyjy.yfyb.bionicfish.Remote.CommandManager;

/**
 * Created by Administrator on 2016/5/5.
 */
@SuppressWarnings("DefaultFileTemplate")
public class SpeedManager {
    
    private static final String TAG=SpeedManager.class.getSimpleName();
    
    private static final int FISH_MAX_SPEED=0x03;    //最大速度
    private static final int FISH_MIN_SPEED=0x00;    //最小速度

    public static final int PRESS_MIN_TIME_TO_CHANGE=500;   //按下最短时常
    public static final int SPEED_MAX_DIF=FISH_MAX_SPEED-FISH_MIN_SPEED+1;

    private final SpeedView mSpeedView;
    private final ImageButton mBtnIncreaseSpeed;
    private final ImageButton mBtnDecreaseSpeed;

    private int mFishSpeed;

    public SpeedManager(SpeedView speedView, ImageButton btnIncreaseSpeed, ImageButton btnDecreaseSpeed){
        mSpeedView=speedView;
        mBtnDecreaseSpeed=btnDecreaseSpeed;
        mBtnIncreaseSpeed=btnIncreaseSpeed;

        refreshSpeedCommand();
    }

    public void startWave(){
        mSpeedView.startWave();
    }

    public void stopWave(){
        mSpeedView.stopWave();
    }

    /*鱼加速*/
    public void fishAccelerate()
    {
        new Thread(runnableFishAccelerate).start();
    }

    /*鱼减速*/
    public void fishDecelerate()
    {
        new Thread(runnableFishDecelerate).start();
    }

    private long m_timeLastAccelerate=0;     //上次鱼加速的时间
    private final Runnable runnableFishAccelerate =new Runnable() {
        @Override
        public void run() {
            /*加速键按下时*/
            Log.d(TAG, "run:"+String.valueOf(mBtnIncreaseSpeed.isPressed()));
            while(mBtnIncreaseSpeed.isPressed())
            {
            /*距离上一次加速键按下超过500ms*/
                if (System.currentTimeMillis()-m_timeLastAccelerate>PRESS_MIN_TIME_TO_CHANGE){
                    if (mFishSpeed!=FISH_MAX_SPEED){
                        mFishSpeed++;
                    }
                    refreshSpeedCommand();
                    Log.d(TAG, "run: speed up");
                    m_timeLastAccelerate=System.currentTimeMillis();
                }
            }
            m_timeLastAccelerate=0;
        }
    };

    private long m_timeLastDecelerate =0;   //上次鱼减速的时间
    private final Runnable runnableFishDecelerate =new Runnable() {
        @Override
        public void run() {
              /*加速键按下时*/
            while(mBtnDecreaseSpeed.isPressed())
            {
            /*距离上一次加速键按下超过500ms*/
                if (System.currentTimeMillis()- m_timeLastDecelerate >PRESS_MIN_TIME_TO_CHANGE){
                    if (mFishSpeed!=FISH_MIN_SPEED){
                        mFishSpeed--;
                    }
                    refreshSpeedCommand();
                    m_timeLastDecelerate =System.currentTimeMillis();
                }
            }
            m_timeLastDecelerate =0;
        }
    };

    private void refreshSpeedCommand() {

        Log.d(TAG, "refreshSpeedCommand: ");
        CommandManager.setSpeed((byte)mFishSpeed);

        //更新速度槽
        mSpeedView.setWaterLevel((mFishSpeed+1) * 1.0f / SPEED_MAX_DIF);
    }

}
