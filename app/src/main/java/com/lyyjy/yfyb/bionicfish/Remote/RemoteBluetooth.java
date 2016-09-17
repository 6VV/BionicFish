package com.lyyjy.yfyb.bionicfish.Remote;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.lyyjy.yfyb.bionicfish.ContextUtil;
import com.lyyjy.yfyb.bionicfish.Device;
import com.lyyjy.yfyb.bionicfish.SettingContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 2016/8/8.
 */
public class RemoteBluetooth extends RemoteParent {
    /*服务及特性UUID*/
    private static final UUID UUID_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");   //读写对应的服务UUID
    private static final UUID UUID_CHARACTERISTIC_WRITE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");  //写入特性对应的UUID
    private static final UUID UUID_CHARACTERISTIC_READ = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");   //读取特性对应的UUID

    private ConnectState mConnectState =ConnectState.DISCONNECT;    //当前连接状态
    private Map<String,BluetoothDevice> mDeviceSearched=new HashMap<>();

    private IRemoteCallback mIRemoteCallback=null;

    //蓝牙相关
    private BluetoothDevice mDeviceConnected=null;
    private BluetoothAdapter mBluetoothAdapter=null;   //蓝牙适配器
    private BluetoothGatt mBluetoothGatt=null;

    private BluetoothGattCharacteristic mBluetoothGattCharacteristicWrite;  //写入用Characteristic

    private HeartbeatListener mHeartbeatListener=new HeartbeatListener();

    public RemoteBluetooth(){
        getAdapter();
    }

    @Override
    public void registerRemoteCallback(IRemoteCallback iRemoteCallback) {
        mIRemoteCallback=iRemoteCallback;
    }

    @Override
    public void unregisterRemoteCallback(IRemoteCallback iRemoteCallback) {
        mIRemoteCallback=null;
    }

    @Override
    public BroadcastReceiver registerEnableReceiver(Context context, IRemoteCallback iRemoteCallback) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        BluetoothChangeReceiver bluetoothChangeReceiver = new BluetoothChangeReceiver(iRemoteCallback);
        context.registerReceiver(bluetoothChangeReceiver, intentFilter);

        return  bluetoothChangeReceiver;
    }

    @Override
    public void unregisterEnableReceiver(Context context, BroadcastReceiver broadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver);
    }

    private class BluetoothChangeReceiver extends BroadcastReceiver {
        private IRemoteCallback mIRemoteCallback;
        public BluetoothChangeReceiver(IRemoteCallback iRemoteCallback){
            mIRemoteCallback = iRemoteCallback;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            mIRemoteCallback.onEnableChanged();
        }
    }

    @Override
    public boolean isEnabled() {
        if (isBluetoothInvalid()){
            return false;
        }

        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public boolean isScanning() {
        return mIsScanning;
    }

    @Override
    public boolean changeEnabled() {
        if (isBluetoothInvalid()){
            return false;
        }

        if (mBluetoothAdapter.isEnabled()){
            return mBluetoothAdapter.disable();
        }else{
            return mBluetoothAdapter.enable();
        }
    }

    @Override
    public void connect(Device device) {
        if (device==null || (mDeviceConnected=mDeviceSearched.get(device.getAddress()))==null){
            Toast.makeText(ContextUtil.getInstance(),"未找到该设备",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isEnabled()){
            Toast.makeText(ContextUtil.getInstance(), "请先开启蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        //销毁原有gatt
        disconnect();
        mConnectState =ConnectState.CONNECTING;
        mBluetoothGatt = mDeviceConnected.connectGatt(ContextUtil.getInstance(), false, new MyBluetoothGattCallback());
    }

    @Override
    public void disconnect() {
        //重置心跳
        mHeartbeatListener.reset();

        mConnectState = ConnectState.DISCONNECT;

        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public void destroy(){
        mBluetoothGatt.close();
        mBluetoothGatt=null;
        mBluetoothAdapter=null;
    }

    @Override
    public ConnectState getConnectState() {
        return mConnectState;
    }

    @Override
    public Device getDeviceConnected() {
        if (mDeviceConnected==null){
            return null;
        }
        return new Device(mDeviceConnected.getName(),mDeviceConnected.getAddress());
    }

    private class MyBluetoothGattCallback extends BluetoothGattCallback{
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectState=ConnectState.CONNECTED;
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                reconnect();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            checkServiceAndConnect();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            onReceiveData(characteristic.getValue());
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }
    }

    private void onReceiveData(byte[] data){
        if (data.length<1){
            return;
        }

        if (data[0]==CommandManager.BACK_HEART_HIT){
            mHeartbeatListener.beat();
        }
        mIRemoteCallback.onReceiveData(data);

//        switch (data[0]){
//            case CommandManager.BACK_SUCCESS:
//            {
//                if (data.length<2){
//                    return;
//                }
//                if (data[1]==CommandManager.BACK_RESET_NAME){
//                    mIRemoteCallback.onReceiveData(data);
//                }
//            }break;
//            case CommandManager.BACK_HEART_HIT:{
//                if (data.length<6){
//                    return;
//                }
//                int power=0x000000ff & data[5];
//                mIRemoteCallback.onElectricQuantityChanged(power);
//
//                mHeartbeatListener.beat();
//            }break;
//            case CommandManager.BACK_PROGRAM:{
//            }break;
//        }
    }

    private class HeartbeatListener {
        private final int MAX_INTERVAL=2000;

        void beat(){
            handlerHearHitStop.removeCallbacks(runnableHearHitStop);
            handlerHearHitStop.postDelayed(runnableHearHitStop, MAX_INTERVAL);
        }

        void reset(){
            handlerHearHitStop.removeCallbacks(runnableHearHitStop);
        }

        private Handler handlerHearHitStop =new Handler();   //心跳断开时
        private Runnable runnableHearHitStop =new Runnable() {
            @Override
            public void run() {
                reconnect();
            }
        };
    }

    private void reconnect() {
        mConnectState=ConnectState.DISCONNECT;
        if (SettingContext.getInstance().isAutoConnect()){
            connect(getDeviceConnected());
        }else{
            disconnect();
        }
        if (mIRemoteCallback!=null){
            mIRemoteCallback.onConnectChanged();
        }
    }

    //建立连接时的调用的函数
    private void checkServiceAndConnect(){
        BluetoothGattService bluetoothGattService = mBluetoothGatt.getService(UUID_SERVICE);    //获取特定服务

        //若未找到相关服务，则断开连接
        if (bluetoothGattService == null) {
            disconnect();
            return;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristicRead = bluetoothGattService.getCharacteristic(UUID_CHARACTERISTIC_READ);    //获取读特性
        mBluetoothGattCharacteristicWrite=bluetoothGattService.getCharacteristic(UUID_CHARACTERISTIC_WRITE);    //获取写特性

        //若未找到相关特性
        if (bluetoothGattCharacteristicRead == null||mBluetoothGattCharacteristicWrite==null) {
            disconnect();
            return;
        }

        mBluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristicRead, true);   //监听读事件

        mIRemoteCallback.onConnectChanged();
    }

    //Android6.0需手动申请权限
    @Override
    public void tryScanDevice(boolean enable, Context context,IRemoteScan iRemoteScan,int permission) {
        if (!isEnabled()){
           return;
        }else{
            //若为开始搜索且sdk>=23
            if (enable && Build.VERSION.SDK_INT >= 23) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
                if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                    //如果需要向用户解释，为什么要申请该权限
                    if(ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        Toast.makeText(context, "Android6.0以上版本需要开启定位才能搜索蓝牙设备", Toast.LENGTH_LONG).show();
                    }
                    ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},permission);
                    return;
                }
            }

            //若开始搜索，则清空原来的搜索记录
            if (enable){
                mDeviceSearched.clear();
            }
            iRemoteScan.scan(enable);
        }
    }

    private MyLeScanCallback mLeScanCallback=null;
    private boolean mIsScanning=false;

    @Override
    public void scanDevice(boolean enable,IRemoteScan iRemoteScan) {
        if (!mBluetoothAdapter.isEnabled()){
            return;
        }
        if (enable) {
            if (mIsScanning){
                return;
            }
            mLeScanCallback=new MyLeScanCallback(iRemoteScan);
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mIsScanning = true;
        } else {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mIsScanning = false;
        }
    }

    private class MyLeScanCallback implements BluetoothAdapter.LeScanCallback{
        private IRemoteScan mIRemoteScan;

        public MyLeScanCallback(IRemoteScan iRemoteScan){
            mIRemoteScan=iRemoteScan;
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mDeviceSearched.containsKey(device.getAddress())){
                return;
            }
            mDeviceSearched.put(device.getAddress(),device);
            mIRemoteScan.onScan(new Device(device.getName(),device.getAddress()));
        }
    }

    @Override
    public void send(byte[] data) {
        if (mBluetoothGatt == null) {
            return ;
        }

        if (mConnectState==ConnectState.DISCONNECT){
            return  ;
        }

        mBluetoothGattCharacteristicWrite.setValue(data);
        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristicWrite);
    }

    private void getAdapter() {
        Context context= ContextUtil.getInstance();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (isBluetoothInvalid()
                || !context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定",null);
//                    new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    System.exit(0);
//                }
//            });

            if (isBluetoothInvalid()) {
                dialog.setTitle("不支持蓝牙设备");
            }

            if (!context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                dialog.setTitle("不支持蓝牙4.0");
            }

            dialog.show();
        }
    }

    private boolean isBluetoothInvalid(){
        return mBluetoothAdapter==null;
    }
}
