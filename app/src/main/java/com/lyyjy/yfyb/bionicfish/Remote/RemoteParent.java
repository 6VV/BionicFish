package com.lyyjy.yfyb.bionicfish.Remote;

import android.content.BroadcastReceiver;
import android.content.Context;

import com.lyyjy.yfyb.bionicfish.Device;

/**
 * Created by Administrator on 2016/8/8.
 */
public abstract class RemoteParent {
    public enum Command{
        FISH_UP,
        FISH_LEFT,
        FISH_RIGHT,
    }

    /*连接状态标志*/
    public enum ConnectState{
        CONNECTING,
        CONNECTED,
        DISCONNECT,
    }

    public abstract void registerRemoteCallback(IRemoteCallback iRemoteCallback);
    public abstract void unregisterRemoteCallback(IRemoteCallback iRemoteCallback);

    public abstract BroadcastReceiver registerEnableReceiver(Context context, IRemoteCallback iRemoteCallback);
    public abstract void unregisterEnableReceiver(Context context, BroadcastReceiver broadcastReceiver);

    public abstract boolean changeEnabled();
    public abstract void connect(Device device);
    public abstract void disconnect();

    public abstract ConnectState getConnectState();
    public abstract Device getDeviceConnected();

    public abstract boolean isEnabled();
    public abstract boolean isScanning();

//    public abstract void scanDevice(boolean enable,IRemoteScan iRemoteScan);
    public abstract void tryScanDevice(boolean enable,IRemoteScan iRemoteScan);
    public abstract void send(byte[] data);
}
