package com.lyyjy.yfyb.bionicfish.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MenuItem;

import com.lyyjy.yfyb.bionicfish.Program.UiProgram.SimulationFishWithLight;
import com.lyyjy.yfyb.bionicfish.R;

public class SimulationActivity extends ParentActivity {

    @SuppressWarnings("unused")
    private static final String TAG = SimulationActivity.class.getSimpleName();

    private static final String EXTRA_MOVEMENT_TEXT = "movement_text";
    private static final String EXTRA_LIGHT_TEXT = "light_text";

    private SimulationFishWithLight mSimulationFishWithLight;
    private boolean mAdmitSimulation = false;
    private SimulationController mSimulationController;

    public static Intent newIntent(Context packageContext, String movementText, String lightText) {
        Intent i = new Intent(packageContext, SimulationActivity.class);
        i.putExtra(EXTRA_MOVEMENT_TEXT, movementText);
        i.putExtra(EXTRA_LIGHT_TEXT, lightText);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);

        mSimulationFishWithLight = (SimulationFishWithLight) findViewById(R.id.simulation_fish);
        mSimulationFishWithLight.post(new Runnable() {
            @Override
            public void run() {
                int actionBarHeight=0;
                if (getSupportActionBar()!=null){
                    actionBarHeight=getSupportActionBar().getHeight();
                }
                mSimulationFishWithLight.setLayoutHeight(getResources().getDisplayMetrics().heightPixels - actionBarHeight);
            }
        });
        mSimulationController = new SimulationController(getIntent().getStringExtra(EXTRA_MOVEMENT_TEXT), getIntent().getStringExtra(EXTRA_LIGHT_TEXT));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAdmitSimulation = true;
        new Thread(new RunnableSimulation()).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdmitSimulation = false;
        mSimulationFishWithLight.stop();
    }

    private class RunnableSimulation implements Runnable {
        @Override
        public void run() {
            while (mAdmitSimulation) {
                mSimulationFishWithLight.post(new Runnable() {
                    @Override
                    public void run() {
                        mSimulationController.control();

                        PointF pointF = mSimulationFishWithLight.nextPosition();
                        mSimulationFishWithLight.layout((int) pointF.x, (int) pointF.y, (int) (pointF.x) + mSimulationFishWithLight.getWidth(), (int) (pointF.y) + mSimulationFishWithLight.getHeight());
                    }
                });

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class SimulationController {
        private final String[] mMovementLineTexts;
        private final String[] mLightLineTexts;

        private long mMoveTime = 0;
        private int mMovementLine = 0;
        private long mLastTime = 0;
        private long mLightTime = 0;
        private int mLightLine = 0;

        public SimulationController(String movementText, String lightText) {
            mMovementLineTexts = movementText.split("\n");
            mLightLineTexts = lightText.split("\n");
            mLastTime = System.currentTimeMillis();
        }

        public void control() {
            long dTime = System.currentTimeMillis() - mLastTime;

            controlMovement(dTime); //执行movement命令
            controlLight(dTime);    //执行light命令

        }

        private void controlLight(long dTime) {
            if (mLightTime <= dTime) {
                if (mLightLineTexts.length > 0) {
                    String text = mLightLineTexts[mLightLine++];
                    mLightLine = mLightLine >= mLightLineTexts.length ? 0 : mLightLine;

                    String[] commands = text.split(" ");
                    if (commands.length != 3) {
                        return;
                    }

                    int color = 0;
                    if (commands[1].equals(getResources().getString(R.string.black))) {
                        color = SimulationFishWithLight.COLOR_BLACK;
                    } else if (commands[1].equals(getResources().getString(R.string.blue))) {
                        color = SimulationFishWithLight.COLOR_BLUE;
                    } else if (commands[1].equals(getResources().getString(R.string.green))) {
                        color = SimulationFishWithLight.COLOR_GREEN;
                    } else if (commands[1].equals(getResources().getString(R.string.cyan))) {
                        color = SimulationFishWithLight.COLOR_CYAN;
                    } else if (commands[1].equals(getResources().getString(R.string.red))) {
                        color = SimulationFishWithLight.COLOR_RED;
                    } else if (commands[1].equals(getResources().getString(R.string.magenta))) {
                        color = SimulationFishWithLight.COLOR_MAGENTA;
                    } else if (commands[1].equals(getResources().getString(R.string.yellow))) {
                        color = SimulationFishWithLight.COLOR_YELLOW;
                    } else if (commands[1].equals(getResources().getString(R.string.white))) {
                        color = SimulationFishWithLight.COLOR_WHITE;
                    }

                    mSimulationFishWithLight.setLightColor(color);
                    mLightTime = (long) (Double.parseDouble(commands[2]) * 1000);
                }
            } else {
                mLightTime -= dTime;
            }

            mLastTime = System.currentTimeMillis();
        }

        private void controlMovement(long dTime) {
            if (mMoveTime <= dTime) {
                if (mMovementLineTexts.length > 0) {
                    String text = mMovementLineTexts[mMovementLine++];
                    mMovementLine = mMovementLine >= mMovementLineTexts.length ? 0 : mMovementLine;

                    String[] commands = text.split(" ");
                    SimulationFishWithLight.Movement movement = null;
                    if (commands[0].equals(getResources().getString(R.string.up))) {
                        movement = SimulationFishWithLight.Movement.UP;
                    } else if (commands[0].equals(getResources().getString(R.string.left))) {
                        movement = SimulationFishWithLight.Movement.LEFT;
                    } else if (commands[0].equals(getResources().getString(R.string.right))) {
                        movement = SimulationFishWithLight.Movement.RIGHT;
                    } else if (commands[0].equals(getResources().getString(R.string.wait))) {
                        movement = SimulationFishWithLight.Movement.STOP;
                    }
                    if (movement == null) {
                        return;
                    }
                    switch (movement) {
                        case UP:
                        case RIGHT:
                        case LEFT: {
                            mSimulationFishWithLight.setSpeed(Integer.parseInt(commands[1]));
                            mSimulationFishWithLight.start();
                        }
                        break;
                        case STOP: {
                            mSimulationFishWithLight.stop();
                        }
                        break;
                        default:
                            break;
                    }
                    mSimulationFishWithLight.setMovement(movement);
                    mMoveTime = (long) (Double.parseDouble(commands[commands.length - 1]) * 1000);
                }
            } else {
                mMoveTime -= dTime;
            }

            mLastTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
            }break;
        }
        return super.onOptionsItemSelected(item);
    }
}
