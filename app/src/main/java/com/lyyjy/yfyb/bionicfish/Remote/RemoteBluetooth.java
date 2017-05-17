package com.lyyjy.yfyb.bionicfish.Remote;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
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
@SuppressWarnings("DefaultFileTemplate")
public class RemoteBluetooth extends RemoteParent {
    @SuppressWarnings("unused")
    private final String TAG="RemoteBluetooth";

    /*服务及特性UUID*/
    private static final UUID UUID_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");   //读写对应的服务UUID
    private static final UUID UUID_CHARACTERISTIC_WRITE = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");  //写入特性对应的UUID
    private static final UUID UUID_CHARACTERISTIC_READ = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");   //读取特性对应的UUID

    private ConnectState mConnectState = ConnectState.DISCONNECT;    //当前连接状态
    private final Map<String, BluetoothDevice> mDeviceSearched = new HashMap<>();

    private IRemoteCallback mIRemoteCallback = null;
    private MyLeScanCallback mLeScanCallback = null;
    private MyScanCallback mScanCallback=null;
    private boolean mIsScanning = false;


    //蓝牙相关
    private BluetoothDevice mDeviceConnected = null;
    private BluetoothAdapter mBluetoothAdapter = null;   //蓝牙适配器
    private BluetoothGatt mBluetoothGatt = null;

    private BluetoothGattCharacteristic mBluetoothGattCharacteristicWrite;  //写入用Characteristic

    private final HeartbeatListener mHeartbeatListener = new HeartbeatListener();

    public RemoteBluetooth() {
        getAdapter();
    }

    @Override
    public void registerRemoteCallback(IRemoteCallback iRemoteCallback) {
        mIRemoteCallback = iRemoteCallback;
    }

    @Override
    public void unregisterRemoteCallback() {
        mIRemoteCallback = null;
    }

    @Override
    public BroadcastReceiver registerEnableReceiver(Context context, IRemoteCallback iRemoteCallback) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        BluetoothChangeReceiver bluetoothChangeReceiver = new BluetoothChangeReceiver(iRemoteCallback);
        context.registerReceiver(bluetoothChangeReceiver, intentFilter);

        return bluetoothChangeReceiver;
    }

    @Override
    public void unregisterEnableReceiver(Context context, BroadcastReceiver broadcastReceiver) {
        context.unregisterReceiver(broadcastReceiver);
    }

    private class BluetoothChangeReceiver extends BroadcastReceiver {
        private final IRemoteCallback mIRemoteCallback;

        public BluetoothChangeReceiver(IRemoteCallback iRemoteCallback) {
            mIRemoteCallback = iRemoteCallback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            mIRemoteCallback.onEnableChanged();
        }
    }

    @Override
    public boolean isEnabled() {
        return !isBluetoothInvalid() && mBluetoothAdapter.isEnabled();
    }

    @Override
    public boolean isScanning() {
        return mIsScanning;
    }

    @Override
    public boolean changeEnabled() {
        if (isBluetoothInvalid()) {
            return false;
        }

        if (mBluetoothAdapter.isEnabled()) {
            return mBluetoothAdapter.disable();
        } else {
            return mBluetoothAdapter.enable();
        }
    }

    @Override
    public void connect(Device device) {
        if (device == null || (mDeviceConnected = mDeviceSearched.get(device.getAddress())) == null) {
            Toast.makeText(ContextUtil.getInstance(), "未找到该设备", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEnabled()) {
            Toast.makeText(ContextUtil.getInstance(), "请先开启蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }

        //销毁原有gatt
        disconnect();
        mConnectState = ConnectState.CONNECTING;
        mBluetoothGatt = mDeviceConnected.connectGatt(ContextUtil.getInstance(), false, new MyBluetoothGattCallback());
//        Log.e(TAG,"connect");

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

    @Override
    public void destroy() {
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        mBluetoothAdapter = null;
    }

    @Override
    public ConnectState getConnectState() {
        return mConnectState;
    }

    @Override
    public Device getDeviceConnected() {
        if (mDeviceConnected == null) {
            return null;
        }
        return new Device(mDeviceConnected.getName(), mDeviceConnected.getAddress());
    }

    private class MyBluetoothGattCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mConnectState = ConnectState.CONNECTED;
//                Log.e(TAG,"onConnectionStateChange");
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
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            onReceiveData(characteristic.getValue());
        }

    }

    private void onReceiveData(byte[] data) {
        if (data.length < 1) {
            return;
        }

        if (data[0] == CommandManager.BACK_HEART_HIT) {
            mHeartbeatListener.beat();
        }
        if(mIRemoteCallback!=null){
            mIRemoteCallback.onReceiveData(data);
        }

    }

    private class HeartbeatListener {
        private final int MAX_INTERVAL = 2000;

        void beat() {
            handlerHearHitStop.removeCallbacks(runnableHearHitStop);
            handlerHearHitStop.postDelayed(runnableHearHitStop, MAX_INTERVAL);
        }

        void reset() {
            handlerHearHitStop.removeCallbacks(runnableHearHitStop);
        }

        private final Handler handlerHearHitStop = new Handler();   //心跳断开时
        private final Runnable runnableHearHitStop = new Runnable() {
            @Override
            public void run() {
                reconnect();
            }
        };
    }

    private void reconnect() {
        mConnectState = ConnectState.DISCONNECT;
        if (SettingContext.getInstance().isAutoConnect()) {
            connect(getDeviceConnected());
        } else {
            disconnect();
        }
        if (mIRemoteCallback != null) {
            mIRemoteCallback.onConnectChanged();
        }
    }

    //建立连接时的调用的函数
    private void checkServiceAndConnect() {
//        Log.e(TAG,"begin getService");
        BluetoothGattService bluetoothGattService = mBluetoothGatt.getService(UUID_SERVICE);    //获取特定服务
        //若未找到相关服务，则断开连接
        if (bluetoothGattService == null) {
//            Log.e(TAG,"未找到服务");
            disconnect();
            return;
        }

//        Log.e(TAG,"begin getCharacteristic");
        BluetoothGattCharacteristic bluetoothGattCharacteristicRead = bluetoothGattService.getCharacteristic(UUID_CHARACTERISTIC_READ);    //获取读特性
        mBluetoothGattCharacteristicWrite = bluetoothGattService.getCharacteristic(UUID_CHARACTERISTIC_WRITE);    //获取写特性
//        Log.e(TAG,"end getCharacteristic");
        //若未找到相关特性
        if (bluetoothGattCharacteristicRead == null || mBluetoothGattCharacteristicWrite == null) {
//            Log.e(TAG,"未找到特性");
            disconnect();
            return;
        }

        mBluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristicRead, true);   //监听读事件

        mIRemoteCallback.onConnectChanged();
//        Log.e(TAG,"checkServiceAndConnect");
    }

    @Override
    public void tryScanDevice(boolean enable, IRemoteScan iRemoteScan) {
        if (!isEnabled()) {
            return;
        }

        //若开始搜索，则清空原来的搜索记录
        if (enable) {
            if (mIsScanning) {
                return;
            }

            mDeviceSearched.clear();
            if (Build.VERSION.SDK_INT<21){
                mLeScanCallback = new MyLeScanCallback(iRemoteScan);
                //noinspection deprecation
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }else {
                mScanCallback=new MyScanCallback(iRemoteScan);
                BluetoothLeScanner scanner=mBluetoothAdapter.getBluetoothLeScanner();
                scanner.startScan(mScanCallback);
            }
            mIsScanning = true;
        } else {
            if (Build.VERSION.SDK_INT<21){
                //noinspection deprecation
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }else{
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
            }
            mIsScanning = false;
        }
    }

    private class MyLeScanCallback implements BluetoothAdapter.LeScanCallback {
        private final IRemoteScan mIRemoteScan;

        public MyLeScanCallback(IRemoteScan iRemoteScan) {
            mIRemoteScan = iRemoteScan;
        }

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mDeviceSearched.containsKey(device.getAddress())) {
                return;
            }
            mDeviceSearched.put(device.getAddress(), device);
            mIRemoteScan.onScan(new Device(device.getName(), device.getAddress()));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MyScanCallback extends ScanCallback {
        private final IRemoteScan mIRemoteScan;

        public MyScanCallback(IRemoteScan iRemoteScan){
            mIRemoteScan=iRemoteScan;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            BluetoothDevice device=result.getDevice();

            if (mDeviceSearched.containsKey(device.getAddress())) {
                return;
            }
            mDeviceSearched.put(device.getAddress(), device);
            mIRemoteScan.onScan(new Device(device.getName(), device.getAddress()));
        }
    }

    @Override
    public void send(byte[] data) {
        if (mBluetoothGatt == null) {
            return;
        }

        if (mConnectState == ConnectState.DISCONNECT) {
            return;
        }

        mBluetoothGattCharacteristicWrite.setValue(data);
        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristicWrite);
    }

    private void getAdapter() {
        Context context = ContextUtil.getInstance();
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        if (isBluetoothInvalid()
                || !context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

            if (isBluetoothInvalid()) {
                Toast.makeText(context,"不支持蓝牙设备",Toast.LENGTH_LONG).show();
            }

            if (!context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(context,"不支持蓝牙4.0",Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isBluetoothInvalid() {
        return mBluetoothAdapter == null;
    }
}
