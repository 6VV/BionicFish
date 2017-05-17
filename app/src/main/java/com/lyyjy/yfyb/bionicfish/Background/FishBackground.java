package com.lyyjy.yfyb.bionicfish.Background;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.lyyjy.yfyb.bionicfish.R;
import com.lyyjy.yfyb.bionicfish.View.FishView;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Administrator on 2016/8/4.
 */
@SuppressWarnings("DefaultFileTemplate")
public class FishBackground extends FrameLayout {
    private static final int HANDLER_FISH_SWIM=1;
    private static final int HANDLER_INIT_STATE=2;

    private final Vector<FishView> mFishes =new Vector<>();
    private final Vector<AnimationDrawable> mFishAnimations = new Vector<>();
    private final Context mContext;

    private boolean mIsSwimming =false;
    private final HandlerFish mHandlerFish=new HandlerFish(this);

    public FishBackground(Context context) {
        super(context);
        mContext=context;
        initFishes();
    }

    public void beginSwim() {
        for (AnimationDrawable animation : mFishAnimations) {
            animation.start();
        }

        mIsSwimming = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsSwimming) {
                    Message msg = new Message();
                    msg.what = HANDLER_FISH_SWIM;
                    mHandlerFish.sendMessage(msg);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopSwim() {
        mIsSwimming = false;
        for (AnimationDrawable animation : mFishAnimations) {
            animation.stop();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (FishView fish : mFishes) {
            fish.setTargetLocation(event.getX(), event.getY());
        }
        return true;
    }

    private static class HandlerFish extends Handler{

        private final WeakReference<FishBackground> mFishBackground;

        public HandlerFish(FishBackground context){
            mFishBackground=new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            FishBackground fishBackground=mFishBackground.get();

            switch (msg.what){
                case FishBackground.HANDLER_FISH_SWIM: {
                    for (FishView fish : fishBackground.mFishes) {
                        int[] vec = fish.getNextPosition();
                        if (vec.length<2){
                            return;
                        }
                        int nextX = vec[0];
                        int nextY = vec[1];
                        fish.layout(nextX, nextY, nextX + fish.getWidth(), nextY + fish.getHeight());
                    }
                }break;
                case FishBackground.HANDLER_INIT_STATE: {
                    //初始化小鱼状态
                    fishBackground.randomFishPosition();
                }
                break;
            }
        }
    }

    private void initFishes() {
        //添加小鱼
        addFishes();
        initFishSize();
        initFishPosition();

    }

    private void initFishPosition() {
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                Message msg=new Message();
                msg.what=HANDLER_INIT_STATE;
                mHandlerFish.sendMessage(msg);
            }
        },100);
    }

    private void addFishes() {
        for (int i = 0; i < 2; ++i) {
            FishView ivFish = new FishView(mContext);
            ivFish.setImageResource(R.drawable.yellow_fish);
            ivFish.setScaleToSizeOfScreen(4);
            AnimationDrawable fishAnimation = (AnimationDrawable) ivFish.getDrawable();
            mFishes.add(ivFish);
            mFishAnimations.add(fishAnimation);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.addView(ivFish, layoutParams);
        }

        for (int i = 0; i < 3; ++i) {
            FishView ivFish = new FishView(mContext);
            ivFish.setImageResource(R.drawable.ink_fish);
            ivFish.setScaleToSizeOfScreen(8);
            AnimationDrawable fishAnimation = (AnimationDrawable) ivFish.getDrawable();
            mFishes.add(ivFish);
            mFishAnimations.add(fishAnimation);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            this.addView(ivFish, layoutParams);
        }
    }

    private void initFishSize() {
        if (mFishes.size()==0){
            return;
        }
        mFishes.get(0).post(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int screenHeight = displayMetrics.heightPixels;
                for (FishView fish : mFishes) {
                    float scale = (float) screenHeight / fish.getHeight() / fish.getScaleToSizeOfScreen();
                    fish.setScaleX(scale);
                    fish.setScaleY(scale);
                }
            }
        });
    }

    private void randomFishPosition() {
        Random random=new Random();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth=displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        for (FishView fish : mFishes) {
            int x = random.nextInt(screenWidth);
            int y = random.nextInt(screenHeight);
            fish.layout(x, y, fish.getWidth() + x, fish.getHeight() + y);
            fish.setRotation(random.nextFloat() * 180);
        }

    }

}
