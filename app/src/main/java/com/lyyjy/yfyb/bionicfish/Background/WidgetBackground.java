package com.lyyjy.yfyb.bionicfish.Background;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.DataPersistence.DatabaseManager;
import com.lyyjy.yfyb.bionicfish.R;
import com.lyyjy.yfyb.bionicfish.Remote.CommandManager;
import com.lyyjy.yfyb.bionicfish.Speed.SpeedManager;
import com.lyyjy.yfyb.bionicfish.Speed.SpeedView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/4.
 */
@SuppressWarnings("DefaultFileTemplate")
public class WidgetBackground extends FrameLayout implements View.OnTouchListener {
    private static final String TAG=WidgetBackground.class.getSimpleName();

    public enum TouchBehavior{
        MOVE_WIDGET,
        CONTROL_FISH,
    }

    private Context mContext=null;
    private WidgetManager mWidgetManager;
    private TouchBehavior mTouchBehavior=TouchBehavior.CONTROL_FISH;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mActionBarHeight;

    private SpeedManager mSpeedManager;

    private boolean mIsSensor=false;
    private SensorManager mSensorManager;   //重力管理器
    private final MySensorEventListener mSensorEventListener=new MySensorEventListener();

    public WidgetBackground(Context context) {
        super(context);
        init(context);
    }

    public void changeSensor(){
        mIsSensor=!mIsSensor;
    }

    public boolean isSensor(){
        return mIsSensor;
    }

    public void onResume(){
        mSpeedManager.startWave();
        //注册重力传感器

        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause(){
        mSpeedManager.stopWave();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private void init(Context context) {
        mContext=context;

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
        updateActionbarHeight(context);

        LayoutInflater layoutInflater=LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.activity_main, this);

        mWidgetManager=new WidgetManager(mContext);

        findViewById(R.id.btnFishUp).setOnTouchListener(this);
        findViewById(R.id.btnFishLeft).setOnTouchListener(this);
        findViewById(R.id.btnFishRight).setOnTouchListener(this);

        ImageButton btnFishAccelerate= (ImageButton) findViewById(R.id.btnFishAccelerate);
        ImageButton btnFishDecelerate= (ImageButton) findViewById(R.id.btnFishDecelerate);
        SpeedView speedView= (SpeedView) findViewById(R.id.viewFishSpeed);

        btnFishAccelerate.setOnTouchListener(this);
        btnFishDecelerate.setOnTouchListener(this);
        speedView.setOnTouchListener(this);

        mSpeedManager = new SpeedManager(speedView, btnFishAccelerate, btnFishDecelerate);

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    }

    private void updateActionbarHeight(final Context context) {
        post(new Runnable() {
            @Override
            public void run() {
                ActionBar actionBar=((AppCompatActivity)context).getSupportActionBar();
                int height=0;
                if (actionBar!=null){
                    height=actionBar.getHeight();
                }
                mActionBarHeight=height;
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (mTouchBehavior){
            case CONTROL_FISH:{
                controlFish(v,event);
            }break;
            case MOVE_WIDGET:{
                moveWidget(v, event);
            }break;
        }
        return false;
    }

    public void showWidgetLayoutDialog(){
        LayoutInflater inflater=LayoutInflater.from(mContext);
        @SuppressLint("InflateParams") View layout= inflater.inflate(R.layout.dialog_widget_layout,null);
        final Switch checkView= (Switch) layout.findViewById(R.id.checkMoveWidget);
        if (mTouchBehavior==TouchBehavior.CONTROL_FISH){
            checkView.setChecked(false);
        }else{
            checkView.setChecked(true);
        }
        (layout.findViewById(R.id.btnSaveLayout)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               mWidgetManager.saveWidgetLayout();
            }
        });

        (layout.findViewById(R.id.btnLoadLayout)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mWidgetManager.loadWidgetLayout();
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("控件布局");
        builder.setView(layout);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (checkView.isChecked()){
                    mTouchBehavior=TouchBehavior.MOVE_WIDGET;
                }else{
                    mTouchBehavior=TouchBehavior.CONTROL_FISH;
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private class WidgetManager{
        private final int WIDGET_FISH_UP=1;
        private final int WIDGET_FISH_RIGHT=2;
        private final int WIDGET_FISH_LEFT=3;
        private final int WIDGET_FISH_ACCELERATE=4;
        private final int WIDGET_FISH_DECELERATE=5;
        private final int WIDGET_FISH_SPEED_VIEW=6;

        private final Context mContext;
        private final Map<Integer,View> mViewMap=new HashMap<Integer,View>(){};

        WidgetManager(Context context){
            mContext=context;

            mViewMap.put(WIDGET_FISH_UP,findViewById(R.id.btnFishUp));
            mViewMap.put(WIDGET_FISH_LEFT, findViewById(R.id.btnFishLeft));
            mViewMap.put(WIDGET_FISH_RIGHT, findViewById(R.id.btnFishRight));
            mViewMap.put(WIDGET_FISH_ACCELERATE, findViewById(R.id.btnFishAccelerate));
            mViewMap.put(WIDGET_FISH_DECELERATE, findViewById(R.id.btnFishDecelerate));
            mViewMap.put(WIDGET_FISH_SPEED_VIEW, findViewById(R.id.viewFishSpeed));
        }

        public void saveWidgetLayout(){
            AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
            builder.setTitle("保存控件布局");
            builder.setMessage("该操作会覆盖之前保存的控件布局存档\n是否覆盖?");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    @SuppressLint("UseSparseArrays") Map<Integer,Rect> map=new HashMap<>();

                    for (int viewId : mViewMap.keySet()) {
                        View view=mViewMap.get(viewId);
                        map.put(viewId, new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
                    }

                    DatabaseManager.getInstance().replaceWidgetLayout(map);
                }
            });
            builder.setNegativeButton("取消",null);
            builder.show();
        }

        public void loadWidgetLayout(){
            final Map<Integer,Rect> map=DatabaseManager.getInstance().selectWidgetLayout();
            if (map.size()==0){
                Toast.makeText(mContext,"无布局存档",Toast.LENGTH_SHORT).show();
                return;
            }
            (new Handler()).post(new Runnable() {
                @Override
                public void run() {
                    for (int viewId : mViewMap.keySet()) {
                        Rect rect=map.get(viewId);
                        if (rect==null){
                            continue;
                        }

                        mViewMap.get(viewId).layout(rect.left,rect.top,rect.right,rect.bottom);
                    }
                }
            });

        }
    }

    private int mLastX;
    private int mLastY;
    private void moveWidget(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;

                int left = v.getLeft() + dx;
                int top = v.getTop() + dy;
                int right = v.getRight() + dx;
                int bottom = v.getBottom() + dy;

                if (left < 0) {
                    left = 0;
                    right = left + v.getWidth();
                }
                if (right > mScreenWidth) {
                    right = mScreenWidth;
                    left = right - v.getWidth();
                }
                if (top < mActionBarHeight) {
                    top = mActionBarHeight;
                    bottom = top + v.getHeight();
                }
                if (bottom > mScreenHeight) {
                    bottom = mScreenHeight;
                    top = bottom - v.getHeight();
                }
                v.layout(left, top, right, bottom);
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
            }
            break;
            case MotionEvent.ACTION_UP: {

            }
            break;
        }
    }

    private enum FishDirection{
        FISH_UP,
        FISH_LEFT,
        FISH_RIGHT,
    }

    private final MessageSender mMessageSender=new MessageSender();

    private void controlFish(View view,MotionEvent event){
        switch (view.getId()){
            case R.id.btnFishAccelerate: {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "controlFish: fish accelerate");
                    mSpeedManager.fishAccelerate();
                }
            }break;
            case R.id.btnFishDecelerate: {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mSpeedManager.fishDecelerate();
                }
            }break;
            case R.id.btnFishUp:
            case R.id.btnFishLeft:
            case R.id.btnFishRight:{
                if (mIsSensor) {
                    return;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE: {
                        switch (view.getId()){
                            case R.id.btnFishUp:{
                                mMessageSender.send(FishDirection.FISH_UP);
                            }break;
                            case R.id.btnFishLeft:{
                                mMessageSender.send(FishDirection.FISH_LEFT);
                            }break;
                            case R.id.btnFishRight:{
                                mMessageSender.send(FishDirection.FISH_RIGHT);
                            }break;
                        }
                    }break;
                    case MotionEvent.ACTION_UP: {
                        mMessageSender.stop();
                    }break;
                }
            }break;
        }
    }

    private class MessageSender{
        private boolean mBeginSendMessage=false;
        private final int PRESS_INTERVAL=100;
        private Thread mThread=null;
        private FishDirection mFishDirection=FishDirection.FISH_UP;
        void send(FishDirection fishDirection){
            if (mThread==null){
                mBeginSendMessage=true;
                mThread=new Thread(runnableFishControl);
                mThread.start();
            }
            mFishDirection=fishDirection;
        }
        void stop(){
            mBeginSendMessage=false;
            mThread=null;
        }

        private final Runnable runnableFishControl = new Runnable() {
            @Override
            public void run() {
                while (mBeginSendMessage) {
//                    handlerFishControl.sendEmptyMessage(0);
                    sendData();
                    try {
                        Thread.sleep(PRESS_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        private void sendData(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    switch (mFishDirection){
                        case FISH_UP:{
                            CommandManager.sendSwimDirection(CommandManager.CommandCode.FISH_UP);
                        }break;
                        case FISH_LEFT:{
                            CommandManager.sendSwimDirection(CommandManager.CommandCode.FISH_LEFT);
                        }break;
                        case FISH_RIGHT:{
                            CommandManager.sendSwimDirection(CommandManager.CommandCode.FISH_RIGHT);
                        }break;
                        default:
                    }
                }
            }).start();
        }

//        private  Handler handlerFishControl = new Handler() {
//
//            @Override
//            public void handleMessage(Message msg) {
//                switch (mFishDirection){
//                    case FISH_UP:{
//                        CommandManager.sendSwimDirection(CommandManager.CommandCode.FISH_UP);
//                    }break;
//                    case FISH_LEFT:{
//                        CommandManager.sendSwimDirection(CommandManager.CommandCode.FISH_LEFT);
//                    }break;
//                    case FISH_RIGHT:{
//                        CommandManager.sendSwimDirection(CommandManager.CommandCode.FISH_RIGHT);
//                    }break;
//                    default:return;
//                }
//            }
//        };

    }

    //传感器监听器
    private final class MySensorEventListener implements SensorEventListener {
        private final int START_VALUE = 2;
        private final int INTERVAL=2;

        @Override
        public void onSensorChanged(SensorEvent event) {
            //若为按键控制
            if (!mIsSensor) {
                return;
            }
            //可以得到传感器实时测量出来的变化值
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[SensorManager.DATA_X];
                float y = event.values[SensorManager.DATA_Y];
//                float z = event.values[SensorManager.DATA_Z];
                //若手机Y轴向下
                if (y < -START_VALUE) {
                    updateSpeed(y);
                    mMessageSender.send(FishDirection.FISH_LEFT);
                } else if (y > START_VALUE) {
                    updateSpeed(y);
                    mMessageSender.send(FishDirection.FISH_RIGHT);
                } else if (x < -START_VALUE) {
                    updateSpeed(x);
                    mMessageSender.send(FishDirection.FISH_UP);
                } else {
                    mMessageSender.stop();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        private void updateSpeed(float value){
            float newValue=Math.abs(value);
            int speed;
            if (newValue<START_VALUE+INTERVAL){
                speed=0x00;
            }else if(newValue<START_VALUE+INTERVAL*2){
                speed=0x01;
            }else if (newValue<START_VALUE+INTERVAL*3){
                speed=0x02;
            }else{
                speed=0x03;
            }
            CommandManager.setSpeed((byte) speed);
        }
    }
}
