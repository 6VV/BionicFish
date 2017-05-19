package com.lyyjy.yfyb.bionicfish.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.Background.FishBackground;
import com.lyyjy.yfyb.bionicfish.Background.MainBackground;
import com.lyyjy.yfyb.bionicfish.Background.WidgetBackground;
import com.lyyjy.yfyb.bionicfish.ContextUtil;
import com.lyyjy.yfyb.bionicfish.Device;
import com.lyyjy.yfyb.bionicfish.Light.LightColorManager;
import com.lyyjy.yfyb.bionicfish.R;
import com.lyyjy.yfyb.bionicfish.Remote.CommandManager;
import com.lyyjy.yfyb.bionicfish.Remote.IRemoteCallback;
import com.lyyjy.yfyb.bionicfish.Remote.RemoteFactory;
import com.lyyjy.yfyb.bionicfish.Remote.RemoteParent;
import com.lyyjy.yfyb.bionicfish.SettingContext;
import com.lyyjy.yfyb.bionicfish.View.BatteryView;

public class MainActivity extends ParentActivity implements IRemoteCallback {
    private FishBackground mFishBackground;
    private WidgetBackground mWidgetBackground;

    private MenuItem mItemRemote;
    private MenuItem mItemConnectState;
    private MenuItem mItemSensor;
    private BatteryView mBatteryView;
    private BroadcastReceiver mBroadcastReceiver=null;
    private DrawerLayout mDrawerLayout;
    @SuppressWarnings("FieldCanBeLocal")
    private NavigationView mNavigationView;

//    private final int HANDLER_POWER_CHANGED=2;
//    private final int HANDLER_RESET_NAME=3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayout();
    }

    private void initLayout() {
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        RelativeLayout layoutMain = (RelativeLayout) inflater.inflate(R.layout.activity_main, null);
        FrameLayout frameLayout=new FrameLayout(this);

        frameLayout.addView(new MainBackground(this));


        mFishBackground=new FishBackground(this);
        frameLayout.addView(mFishBackground);

        mWidgetBackground=new WidgetBackground(this);
        frameLayout.addView(mWidgetBackground);

        getLayoutInflater().inflate(R.layout.navigation,frameLayout);

        setContentView(frameLayout);

        mDrawerLayout= (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView= (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setItemIconTintList(null);
        mNavigationView.getMenu().findItem(R.id.action_battery).setVisible(false);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                onOptionsItemSelected(item);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        findViewById(R.id.navigation_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mItemRemote = menu.findItem(R.id.action_remote);
        mItemConnectState = menu.findItem(R.id.action_connect);
        mItemSensor = menu.findItem(R.id.action_sensor);

        updateRemoteIcon();
        updateConnectStateIcon();
        updateSensorIcon();
//        mMenuItemMoveWidget = menu.findItem(R.id.action_moveWidget);

        initBatteryView(menu);

        return true;
    }

    private void initBatteryView(Menu menu) {
        final MenuItem item=menu.findItem(R.id.action_battery);
        mBatteryView=new BatteryView(this);

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                item.setActionView(mBatteryView);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                exit();
            }break;
            case R.id.action_search:{
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
            }break;
            case R.id.action_help:{
                startActivity(new Intent(MainActivity.this, HelpDocumentActivity.class));
            }break;
            case R.id.action_sensor:{
                mWidgetBackground.changeSensor();
                updateSensorIcon();
            }break;
            case R.id.action_remote:{
                if(!RemoteFactory.getRemote().changeEnabled()){
                    Toast.makeText(MainActivity.this,"切换失败",Toast.LENGTH_LONG).show();
                }
            }break;
            case R.id.action_moveWidget:{
                mWidgetBackground.showWidgetLayoutDialog();
            }break;
            case R.id.action_program:{
                startActivity(new Intent(MainActivity.this, ProgramActivity.class));
            }break;
            case R.id.action_ui_program:{
                startActivity(new Intent(MainActivity.this,UiProgramActivity.class));
            }break;
            case R.id.action_connect:{
                changeConnectState();
            }break;
            case R.id.action_select_light:{
                new LightColorManager(this).showDialog();
            }break;
            case R.id.action_swimMode:{
                changeSwimMode();
            }break;
            case R.id.action_changeName:{
                changeDeviceName();
            }break;
            case R.id.action_setting:{
                changeSettings();
            }break;
            case R.id.action_test:{
                Toast.makeText(ContextUtil.getInstance(),"test",Toast.LENGTH_LONG).show();
            }break;
        }
        return true;
    }

    private void changeSettings() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("设置");
        @SuppressLint("InflateParams") View view= LayoutInflater.from(this).inflate(R.layout.dialog_setting,null);
        final Switch autoCloseWireless= (Switch) view.findViewById(R.id.autoCloseWireless);
        final Switch autoReconnect = (Switch) view.findViewById(R.id.autoConnect);
        autoCloseWireless.setChecked(SettingContext.getInstance().isAutoCloseWireless());
        autoReconnect.setChecked(SettingContext.getInstance().isAutoConnect());
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SettingContext.ReconnectState reconnectState= SettingContext.ReconnectState.MANUAL_CONNECT;
                if (autoReconnect.isChecked()){
                    reconnectState= SettingContext.ReconnectState.AUTO_CONNECT;
                }
                SettingContext.CloseWirelessState closeWirelessState= SettingContext.CloseWirelessState.MANUAL_CLOSE_WIRELESS;
                if (autoCloseWireless.isChecked()){
                    closeWirelessState= SettingContext.CloseWirelessState.AUTO_CLOSE_WIRELESS;
                }
                SettingContext.getInstance().saveSettings(reconnectState,closeWirelessState);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void changeDeviceName() {
        if (RemoteFactory.getRemote().getConnectState()!= RemoteParent.ConnectState.CONNECTED){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("请先连接一个设备");
            builder.setPositiveButton("确定",null);
            builder.show();
            return;
        }
        final EditText editText=new EditText(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("新名字：");
        builder.setView(editText);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int length=editText.getText().toString().getBytes().length;
                if(length==0 || length>12){
                    AlertDialog.Builder lengthDialog=new AlertDialog.Builder(MainActivity.this);

                    if (length == 0) {
                        lengthDialog.setTitle("名字不能为空");
                    }else{
                        lengthDialog.setTitle("名字过长");
                        lengthDialog.setMessage("长度不应超过12个字母或3个汉字");
                    }
                    lengthDialog.setPositiveButton("确定",null);
                    lengthDialog.show();
                }else{
                    CommandManager.resetName(editText.getText().toString());
                }
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();

    }

    private void changeSwimMode() {
        final int[] swimMode = {0};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("游动模式");
        builder.setSingleChoiceItems(new String[]{"手动", "自动"}, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                swimMode[0] =which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (swimMode[0]==0){
//                    CommandManager.sendSwimMode(CommandManager.CommandCode.AUTO_SWIM);
//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    CommandManager.sendSwimMode(CommandManager.CommandCode.MANUAL_SWIM);
                }else{
                    CommandManager.sendSwimMode(CommandManager.CommandCode.AUTO_SWIM);
                }
            }
        });
        builder.setNegativeButton("取消",null);
        builder.show();
    }

    private void changeConnectState() {
        final RemoteParent remote=RemoteFactory.getRemote();
        if (remote.getConnectState()== RemoteParent.ConnectState.CONNECTED){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("断开连接?");
            builder.setMessage("名字："+remote.getDeviceConnected().getName()+"\n"
            +"地址："+remote.getDeviceConnected().getAddress());
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    remote.disconnect();
                    onConnectChanged();
                }
            });
            builder.setNegativeButton("取消",null);
            builder.show();
        }else{
            final Device device=remote.getDeviceConnected();
            if (device==null){
                Toast.makeText(this,"请选择一个设备",Toast.LENGTH_SHORT).show();
            }else{
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("连接设备?");
                builder.setMessage("名字："+device.getName()+"\n"
                        +"地址："+device.getAddress());
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remote.connect(device);
                    }
                });
                builder.setNegativeButton("取消",null);
                builder.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RemoteFactory.getRemote().registerRemoteCallback(this);
        mBroadcastReceiver= RemoteFactory.getRemote().registerEnableReceiver(this,this);
        mFishBackground.beginSwim();
        mWidgetBackground.onResume();
        if (mItemRemote!=null){
            updateRemoteIcon();
        }
        if (mItemConnectState!=null){
            updateConnectStateIcon();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        RemoteFactory.getRemote().unregisterRemoteCallback();
        if (mBroadcastReceiver!=null){
            RemoteFactory.getRemote().unregisterEnableReceiver(this,mBroadcastReceiver);
        }
        mFishBackground.stopSwim();
        mWidgetBackground.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RemoteFactory.getRemote().disconnect();
        if (SettingContext.getInstance().isAutoCloseWireless()){
            if (RemoteFactory.getRemote().isEnabled()){
                RemoteFactory.getRemote().changeEnabled();
            }
        }

    }

    private float mBackTime=0;
    private void exit() {
        final int CLICK_INTERVAL=2000;
        if (System.currentTimeMillis() - mBackTime > CLICK_INTERVAL) {
            Toast.makeText(MainActivity.this, "再次按下后退键后退出程序", Toast.LENGTH_SHORT).show();
            mBackTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public void onEnableChanged() {
        updateRemoteIcon();
    }

    @Override
    public void onConnectChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateConnectStateIcon();
            }
        });
    }

    private void onElectricQuantityChanged(final int power) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBatteryView.setPower(power);
            }
        });
    }

    @Override
    public void onReceiveData(byte[] data) {
        switch (data[0]){
            case CommandManager.BACK_SUCCESS:
            {
                if (data.length<2){
                    return;
                }
                if (data[1]==CommandManager.BACK_RESET_NAME){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CommandManager.resetDevice();
                            String text;
                            if (SettingContext.getInstance().isAutoConnect()){
                                text="重置名字成功\n正在重新连接";
                            }else{
                                text="重置名字成功\n请重新连接";
                            }
                            Toast.makeText(MainActivity.this,text,Toast.LENGTH_LONG).show();
                        }
                    });
//                    Message msg=new Message();
//                    msg.what=HANDLER_RESET_NAME;
//                    MyHandler.sendMessage(msg);
                }
            }break;
            case CommandManager.BACK_HEART_HIT:{
                if (data.length<6){
                    return;
                }
                int power=0x000000ff & data[5];
                onElectricQuantityChanged(power);
            }break;
            default:break;
        }
    }

    private void updateRemoteIcon() {
        if (RemoteFactory.getRemote().isEnabled()){
            mItemRemote.setIcon(R.drawable.bluetooth_enable);
        }else{
            mItemRemote.setIcon(R.drawable.bluetooth_disabled);
        }
    }

    private void updateConnectStateIcon() {
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RemoteParent.ConnectState state=RemoteFactory.getRemote().getConnectState();

                if (state== RemoteParent.ConnectState.CONNECTED){
                    mItemConnectState.setIcon(R.drawable.connected);
                }else{
                    mItemConnectState.setIcon(R.drawable.disconnected);
                }
            }
        });
    }

    private void updateSensorIcon() {
        if (mWidgetBackground.isSensor()){
            mItemSensor.setIcon(R.drawable.sensor);
            mWidgetBackground.setVisibility(View.INVISIBLE);
        }else{
            mItemSensor.setIcon(R.drawable.manual);
            mWidgetBackground.setVisibility(View.VISIBLE);
        }
    }
}
